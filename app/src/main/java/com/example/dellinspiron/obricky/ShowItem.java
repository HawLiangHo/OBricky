package com.example.dellinspiron.obricky;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class ShowItem extends AppCompatActivity {

    ImageView tvShowPhoneImage;

    TextView tvShowPhoneId;
    TextView tvShowPhoneName;
    TextView tvShowNetworkTechnology;
    TextView tvShowBodyDimensions;
    TextView tvShowDisplayType;
    TextView tvShowStorage;
    TextView tvShowMainCamera;
    TextView tvShowSelfieCamera;
    TextView tvShowComms;
    TextView tvShowBattery;
    TextView tvShowPrice;

    int showPhoneId;
    String showPhoneName;
    String showNetworkTechnology;
    String showBodyDimensions;
    String showDisplayType;
    String showStorage;
    String showMainCamera;
    String showSelfieCamera;
    String showComms;
    String showBattery;
    Float showPrice;

    Button btnPurchase;

    private int customer_id;
    private float totalAmt;

    public static int REQUEST_CODE5 = 5;

    public static final String EXTRA_CUSTOMER_ID = "com.example.dellinspiron.obricky.CUSTOMER_ID";
    public static final String EXTRA_PHONE_ID = "com.example.dellinspiron.obricky.PHONE_ID";
    public static final String EXTRA_PRICE = "com.example.dellinspiron.obricky.PRICE";

//    public static final String EXTRA_SERVICE_ID = "com.example.dellinspiron.obricky.SERVICE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_item);

        customer_id = getIntent().getIntExtra(Login.EXTRA_CUSTOMER_ID, 0);
        totalAmt = getIntent().getFloatExtra(Login.EXTRA_WALLET_BALANCE, -1);

        tvShowPhoneImage = findViewById(R.id.ivShowImage);

        tvShowPhoneId = findViewById(R.id.tvPhoneId);
        tvShowPhoneName = findViewById(R.id.tvPhoneName);
        tvShowNetworkTechnology = findViewById(R.id.tvNetworkTechnology);
        tvShowBodyDimensions = findViewById(R.id.tvBodyDimensions);
        tvShowDisplayType = findViewById(R.id.tvDisplayType);
        tvShowStorage = findViewById(R.id.tvStorage);
        tvShowMainCamera = findViewById(R.id.tvMainCamera);
        tvShowSelfieCamera = findViewById(R.id.tvSelfieCamera);
        tvShowComms = findViewById(R.id.tvComms);
        tvShowBattery = findViewById(R.id.tvBattery);
        tvShowPrice = findViewById(R.id.tvPrice);

        Intent i = getIntent();
        showPhoneId = i.getIntExtra(EXTRA_PHONE_ID, 1);
        showPhoneName = i.getStringExtra("phoneName");
        showNetworkTechnology = i.getStringExtra("networkTechnology");
        showBodyDimensions = i.getStringExtra("bodyDimensions");
        showDisplayType = i.getStringExtra("displayType");
        showStorage = i.getStringExtra("storageMemory");
        showMainCamera = i.getStringExtra("mainCamera");
        showSelfieCamera = i.getStringExtra("selfieCamera");
        showComms = i.getStringExtra("comms");
        showBattery = i.getStringExtra("battery");
        showPrice = i.getFloatExtra(EXTRA_PRICE, 2);

        if(showPhoneId == 1){
            tvShowPhoneImage.setImageResource(R.drawable.ic_huawei);
        }
        if(showPhoneId == 2){
            tvShowPhoneImage.setImageResource(R.drawable.ic_xiaomi);
        }
        if(showPhoneId == 3){
            tvShowPhoneImage.setImageResource(R.drawable.ic_oppo);
        }
        if(showPhoneId == 4){
            tvShowPhoneImage.setImageResource(R.drawable.ic_vivo);
        }
        if(showPhoneId == 5){
            tvShowPhoneImage.setImageResource(R.drawable.ic_realme);
        }

        tvShowPhoneId.setText(showPhoneId + " ");
        tvShowPhoneName.setText(showPhoneName);
        tvShowNetworkTechnology.setText(showNetworkTechnology + " ");
        tvShowBodyDimensions.setText(showBodyDimensions + " ");
        tvShowDisplayType.setText(showDisplayType + " ");
        tvShowStorage.setText(showStorage + " ");
        tvShowMainCamera.setText(showMainCamera + " ");
        tvShowSelfieCamera.setText(showSelfieCamera + " ");
        tvShowComms.setText(showComms + " ");
        tvShowBattery.setText(showBattery + " ");
        tvShowPrice.setText("RM " + showPrice + "0");

//        Toast.makeText(this, "Intent to ShowItem.java?", Toast.LENGTH_SHORT).show();

        btnPurchase = (Button) findViewById(R.id.btnPurchase);

        if(totalAmt < showPrice){
            Toast.makeText(ShowItem.this, "You are lack of credit! Please reload to continue purchase!", Toast.LENGTH_SHORT).show();
            btnPurchase.setVisibility(View.INVISIBLE);
        }else if(totalAmt >= showPrice){

            btnPurchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowItem.this);
                    builder.setTitle("Confirm Purchase " + showPhoneName + " ?");
                    builder.setMessage("Credit Balance : RM " + totalAmt + "0" +
                            "\n\n" + "Price Amount : RM " + showPrice + "0" +
                            "\n\n" + "Leftover Balance : RM " + (totalAmt - showPrice) + "0"
                    );

                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            totalAmt = totalAmt - showPrice;
                            new Background().execute();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.setIcon(R.drawable.ic_phone_icon);
                    AlertDialog dialog = builder.create();
                    dialog.show();
//                    totalAmt = totalAmt - showPrice;
//
//                    Toast.makeText(ShowItem.this, "Purchase Successfully!", Toast.LENGTH_SHORT).show();
//                    new Background().execute();

                }
            });
        }
    }

//    private void openConfirmationPurchase(){
//
//
//    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE5) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12374849";
        private static final String DB_NAME = "sql12374849";
        private static final String PASSWORD = "IQF8fuIuFn";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        private Connection conn;
        private PreparedStatement stmt;
        private PreparedStatement stmt2;
        private ProgressDialog progressDialog;

        public Background() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        @Override
        protected void onPostExecute(ResultSet result) {
            super.onPostExecute(result);
            Intent i = new Intent();

            try {
                i.putExtra("TOTAL_AMOUNT", totalAmt);
                setResult(RESULT_OK,i);
                finish();
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
            progressDialog = new ProgressDialog(ShowItem.this);
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
                String query = "INSERT into phone_order (customer_id, phone_id, phone_name, status) values (?, ?, ?, ?)";

                stmt = conn.prepareStatement(query);
                stmt.setInt(1, customer_id);
                stmt.setInt(2, showPhoneId);
                stmt.setString(3, showPhoneName);
                stmt.setString(4, "Info Received");
                stmt.executeUpdate();

                query = "UPDATE account SET wallet_balance = ? WHERE id = ?";

                stmt = conn.prepareStatement(query);
                stmt.setFloat(1, totalAmt);
                stmt.setInt(2, customer_id);
                stmt.executeUpdate();

//                result = stmt.executeQuery();
//                result.next();
//
//                return result;
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return result;
        }


        private Connection connectDB(){
            try {
                Class.forName(LIBRARY);
                return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DB_NAME, USERNAME, PASSWORD);
            } catch (Exception e) {
                Log.e("Error on Connection", e.getMessage());
                return null;
            }
        }

        public void closeConn() {
            try {
                stmt.close();
            } catch (Exception e) {
                /* ignored */
            }
            try {
                conn.close();
            } catch (Exception e) { /* ignored */ }
        }
    }
}
