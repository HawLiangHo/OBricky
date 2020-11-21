package com.example.dellinspiron.obricky;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Register extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +      // at least 1 number
                    "(.{8,})" +        // at least 6, at most 20 character
                    "$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputAddress1;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textInputEmail = findViewById(R.id.textInputEmail2);
        textInputAddress1 = findViewById(R.id.textInputAddress1);
        textInputPassword = findViewById(R.id.textInputPassword);
        textInputConfirmPassword = findViewById(R.id.textInputConfirm);
    }

    private boolean validateEmail(){
        String emailInput = textInputEmail.getEditText().getText().toString();

        if (emailInput.isEmpty()) {
            textInputEmail.setError("This field cannot be empty!");
            return false;
        }else if(!EMAIL_PATTERN.matcher(emailInput).matches()){
            textInputEmail.setError("Invalid Email Address");
            return false;
        }
        else {
            textInputEmail.setError(null);
            return true;
        }

    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("This field cannot be empty!");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputPassword.setError("Password must contain at least 8 character, letter and number!");
            return false;
        } else if(passwordInput.length() > 15){
            textInputPassword.setError("Password cannot exceed 15 characters!");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword(){
        String confirmPassInput = textInputConfirmPassword.getEditText().getText().toString();

        if (confirmPassInput.isEmpty()) {
            textInputConfirmPassword.setError("This field cannot be empty!");
            return false;
        } else if (!confirmPassInput.equals(textInputPassword.getEditText().getText().toString())) {
            textInputConfirmPassword.setError("The Password does not match!");
            return false;
        }else if(confirmPassInput.length() > 15) {
            textInputPassword.setError("Password cannot exceed 15 characters!");
            return false;
        }else {
            textInputConfirmPassword.setError(null);
            return true;
        }
    }

    private boolean validateAddress1() {
        String addressInput1 = textInputAddress1.getEditText().getText().toString();

        //validate 1st address part
        if (addressInput1.isEmpty()) {
            textInputAddress1.setError("This field cannot be empty!");
            return false;
        } else {
            textInputAddress1.setError(null);
            return true;
        }
    }

    public void btnSignUp_onClicked(View view) {
        if (!validateEmail() | !validateAddress1() | !validatePassword()  | !validateConfirmPassword()){
            return;
        } else {
            String usernameInput = textInputEmail.getEditText().getText().toString();
            String passwordInput = textInputPassword.getEditText().getText().toString();
            String addressInput1 = textInputAddress1.getEditText().getText().toString();
            String hashed_password = MD5(passwordInput);

            Background bg = new Background();
            bg.execute(usernameInput, hashed_password, addressInput1);

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

    public class Background extends AsyncTask<String, Void, ResultSet> {
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
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);
            try {
                startActivity(new Intent(Register.this, Login.class));
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            finally {
                progressDialog.hide();
                try { result.close(); } catch (Exception e) { /* ignored */ }
                closeConn();
            }

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Register.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Processing data");
            progressDialog.show();
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            conn = connectDB();
            ResultSet result = null;
            if (conn == null) {
                return null;
            }
            try {
                String query = "insert into account (email, password, address, wallet_balance) values (?, ?, ?, ?)";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, strings[0]);
                stmt.setString(2, strings[1]);
                stmt.setString(3, strings[2]);
                stmt.setFloat(4, 0.0f);
                stmt.executeUpdate();
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return result;
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


