package com.example.dellinspiron.obricky;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {
    private EditText mETEMail;
    private EditText mETAddress;

    private TextInputLayout mLayoutEmail;
    private TextInputLayout mLayoutAddress;

    private int id;

    private String email;
    private String address;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mETEMail = findViewById(R.id.ETEmail);
        mETAddress = findViewById(R.id.ETAdress);

        mLayoutEmail = findViewById(R.id.layout_username);
        mLayoutAddress = findViewById(R.id.layout_address);

        Bundle i = getIntent().getExtras();

        id = i.getInt(Login.EXTRA_CUSTOMER_ID);
        email = i.getString(Login.EXTRA_EMAIL);
        address = i.getString(Login.EXTRA_ADDRESS);

        mETEMail.setText(email);
        mETAddress.setText(address);
    }

    private boolean validateEmail(){
        String emailInput = mLayoutEmail.getEditText().getText().toString();

        if (emailInput.isEmpty()) {
            mLayoutEmail.setError("This field cannot be empty!");
            return false;
        }else if(!EMAIL_PATTERN.matcher(emailInput).matches()){
            mLayoutEmail.setError("Invalid Email Address");
            return false;
        }
        else {
            mLayoutEmail.setError(null);
            return true;
        }

    }

    private boolean validateAddress() {
        String addressInput = mLayoutAddress.getEditText().getText().toString().trim();
        if (addressInput.isEmpty()) {
            mLayoutAddress.setError("This field cannot be empty!");
            return false;
        } else {
            mLayoutAddress.setError(null);
            return true;
        }
    }

    public void btnSave_onClick(View view) {
        if (!validateEmail() | !validateAddress()){
            return;
        } else {
            String emailInput = mLayoutEmail.getEditText().getText().toString().trim();
            String addressInput = mLayoutAddress.getEditText().getText().toString().trim();
            new Background().execute(emailInput, addressInput);
        }
    }
    public class Background extends AsyncTask<String, Void, String> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12374849";
        private static final String DB_NAME = "sql12374849";
        private static final String PASSWORD = "IQF8fuIuFn";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        private Connection conn;
        private PreparedStatement stmt;
        private ProgressDialog progressDialog;
        private String emailEdit;
        private String addressEdit;

        public Background() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            closeConn();
            String emailInput = mLayoutEmail.getEditText().getText().toString();
            String addressInput = mLayoutAddress.getEditText().getText().toString().trim();


            try {
                if (result.isEmpty()) {
                    Intent i = new Intent();
                    i.putExtra("EMAIL_EDIT", emailInput);
                    i.putExtra("ADDRESS_EDIT", addressInput);
                    setResult(RESULT_OK, i);
                    finish();
                }
                else {
                    Toast.makeText(Profile.this, result, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Profile.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing data");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            conn = connectDB();


            if (conn == null) {
                return null;
            }
            try {
                String query = "SELECT email FROM account WHERE email LIKE ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, emailEdit);
//                stmt.setString(2, addressEdit);
//                stmt.setInt(3, id);
                ResultSet result1 = stmt.executeQuery();
                if (result1.next()) {
                    String dEmail = result1.getString(1);
                    emailEdit = strings[0];
                    if (result1.next() || dEmail.toLowerCase().equals(emailEdit.toLowerCase())){
                        return getString(R.string.email_exists);

                    }
                } else {
                    stmt = conn.prepareStatement("UPDATE account SET email=?, address=? WHERE id=?");
                    stmt.setString(1, strings[0]);
                    stmt.setString(2, strings[1]);
                    stmt.setInt(3, id);
                    stmt.executeUpdate();
                }
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return "";
        }
        private Connection connectDB() {
            try {
                Class.forName(LIBRARY);
                return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DB_NAME, USERNAME, PASSWORD);
            }
            catch (Exception e) {
                Log.e("Error on Connection", e.getMessage());
                return null;
            }
        }
        public void closeConn () {
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
        }
    }
}
