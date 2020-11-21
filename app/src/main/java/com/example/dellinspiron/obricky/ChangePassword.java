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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.regex.Pattern;

public class ChangePassword extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +      // at least 1 number
                    "(.{8,})" +        // at least 6, at most 20 character
                    "$");

    private TextInputLayout mLayoutOldPassword;
    private TextInputLayout mLayoutNewPassword;
    private TextInputLayout mLayoutConfirmPassword;


    private int id;
    private String oldPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mLayoutOldPassword = findViewById(R.id.layout_oldPassword);
        mLayoutNewPassword = findViewById(R.id.layout_newPassword);
        mLayoutConfirmPassword = findViewById(R.id.textInputConfirmPass);

        id = getIntent().getIntExtra(Login.EXTRA_CUSTOMER_ID, -1);
        oldPassword = getIntent().getStringExtra(Login.EXTRA_PASSWORD);

        EditText mEtOldPassword = findViewById(R.id.EToldpassword);
        mEtOldPassword.setText(oldPassword);
    }

    private boolean validateOldPassword(){
        String oldInput = mLayoutOldPassword.getEditText().getText().toString();

        if (oldInput.isEmpty()) {
            mLayoutOldPassword.setError("This field cannot be empty!");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(oldInput).matches()) {
            mLayoutOldPassword.setError("Password must contain at least 8 characters!");
            return false;
        } else if (!oldInput.equals(oldPassword)) {
            mLayoutOldPassword.setError("Password does not match to the original password!");
            return false;
        } else {
            mLayoutOldPassword.setError(null);
            return true;
        }
    }

    private boolean validateNewPassword(){
        String oldInput = mLayoutOldPassword.getEditText().getText().toString();
        String newInput = mLayoutNewPassword.getEditText().getText().toString();

        if (newInput.isEmpty()) {
            mLayoutNewPassword.setError("This field cannot be empty!");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(newInput).matches()) {
            mLayoutNewPassword.setError("Password must contain at least 8 characters!");
            return false;
        } else if (oldInput.equals(newInput)) {
            mLayoutNewPassword.setError("Old and new password must not be the same!");
            return false;
        } else {
            mLayoutNewPassword.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String passwordInput = mLayoutNewPassword.getEditText().getText().toString();
        String confirmPasswordInput = mLayoutConfirmPassword.getEditText().getText().toString();

        if (confirmPasswordInput.isEmpty()) {
            mLayoutConfirmPassword.setError("This field cannot be empty!");
            return false;
        } else if (!confirmPasswordInput.equals(passwordInput)) {
            mLayoutConfirmPassword.setError("Password does not match!");
            return false;
        } else {
            mLayoutConfirmPassword.setError(null);
            return true;
        }
    }

    public void btnSavePassword_onClick(View view) {
        if (!validateOldPassword() | !validateNewPassword() | !validateConfirmPassword()){
            return;
        } else {
            String newInput = mLayoutNewPassword.getEditText().getText().toString();
            String hashed_password = MD5(newInput);

            Background bg = new Background();
            bg.execute(hashed_password);
        }
    }

    public static String MD5(String password) {
        byte[] bytes = password.getBytes();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {}
        byte[] hashed_password = md.digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b: hashed_password) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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
        public Background() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            closeConn();
            String newInput = mLayoutNewPassword.getEditText().getText().toString();

            try {
                if (result.isEmpty()) {
                    Intent i = new Intent();
                    i.putExtra("Password", newInput);
                    setResult(RESULT_OK, i);
                    finish();
                }
                else {
                    Toast.makeText(ChangePassword.this, result, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(ChangePassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChangePassword.this);
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
                String query = "UPDATE account SET password = ? WHERE id = ?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, strings[0]);
                stmt.setInt(2, id);
                stmt.executeUpdate();
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
