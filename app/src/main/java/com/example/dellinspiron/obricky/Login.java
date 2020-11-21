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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +      // at least 1 number
                    "(.{8,})" +        // at least 6, at most 20 character
                    "$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

    public static final String EXTRA_CUSTOMER_ID = "com.example.damien.realworldproject.ID";
    public static final String EXTRA_EMAIL = "com.example.damien.realworldproject.EMAIL";
    public static final String EXTRA_PASSWORD = "com.example.damien.realworldproject.PASSWORD";
    public static final String EXTRA_ADDRESS= "com.example.damien.realworldproject.ADDRESS";
    public static final String EXTRA_WALLET_BALANCE = "com.example.damien.realworldproject.WALLET";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEmail = findViewById(R.id.textInputEmail1);
        textInputPassword = findViewById(R.id.textInputPwd);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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
        } else if(passwordInput.length() > 15) {
            textInputPassword.setError("Password cannot exceed 15 characters!");
            return false;
        }else {
            textInputPassword.setError(null);
            return true;
        }
    }

    public void btnLogin_onClicked(View view) {
        if (!validateEmail() | !validatePassword()){
            return;
        } else {
            String passwordInput = textInputPassword.getEditText().getText().toString();
            String usernameInput = textInputEmail.getEditText().getText().toString();

            Background bg = new Background();
            String hashed_password = MD5(passwordInput);
            bg.execute(usernameInput, hashed_password);
        }
    }

    public void btnSignUp_onClicked(View view) {
        startActivity(new Intent(Login.this,Register.class));
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
            Intent i = new Intent(Login.this, MainActivity.class);
            String passwordInput = textInputPassword.getEditText().getText().toString();

            try {
                if (result.next()) {
                    i.putExtra(EXTRA_CUSTOMER_ID, result.getInt(1));
                    i.putExtra(EXTRA_EMAIL, result.getString(2));
                    i.putExtra(EXTRA_PASSWORD, passwordInput);
                    i.putExtra(EXTRA_ADDRESS, result.getString(4));
                    i.putExtra(EXTRA_WALLET_BALANCE, result.getFloat(5));

                    startActivity(i);
                }
                else {
                    Toast.makeText(Login.this, "Email and Password Invalid", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                Log.e("ERROR BACKGROUND", e.getMessage());
                Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
            progressDialog = new ProgressDialog(Login.this);
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
                String query = "SELECT id, email, password, address, wallet_balance FROM account WHERE email LIKE ? AND password=?";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, strings[0]);
                stmt.setString(2, strings[1]);
                result = stmt.executeQuery();
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

