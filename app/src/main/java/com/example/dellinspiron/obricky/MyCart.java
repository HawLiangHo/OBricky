package com.example.dellinspiron.obricky;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static com.example.dellinspiron.obricky.R.drawable.ic_huawei;
import static com.example.dellinspiron.obricky.R.drawable.ic_oppo;
import static com.example.dellinspiron.obricky.R.drawable.ic_realme;
import static com.example.dellinspiron.obricky.R.drawable.ic_vivo;
import static com.example.dellinspiron.obricky.R.drawable.ic_xiaomi;

public class MyCart extends AppCompatActivity {

    //variables
    private RecyclerView mRecyclerView2;
    private MyCartAdapter mAdapter2;
    private int customer_id;

    //end of variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

//        Toast.makeText(this, "My Cart", Toast.LENGTH_SHORT).show();

        customer_id = getIntent().getIntExtra(Login.EXTRA_CUSTOMER_ID, 1);

        Background bg = new Background(Background.FETCH_DATA);
        mRecyclerView2 = (RecyclerView) findViewById(R.id.recyclerMyCart);
        mAdapter2 = new MyCartAdapter(bg);
        mRecyclerView2.setAdapter(mAdapter2);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(MyCart.this));
    }

    public class Background extends AsyncTask<String, Void, ResultSet> {
        private static final String LIBRARY = "com.mysql.jdbc.Driver";
        private static final String USERNAME = "sql12374849";
        private static final String DB_NAME = "sql12374849";
        private static final String PASSWORD = "IQF8fuIuFn";
        private static final String SERVER = "sql12.freemysqlhosting.net";

        private Connection conn;
        private PreparedStatement stmt;
        private int method;

        public static final int FETCH_DATA = 1;

        public Background(int method) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            this.method = method;
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            conn = connectDB();
            ResultSet result = null;
            Log.e("Error", conn.toString());
            if (conn == null) {
                return null;
            }
            try {
                String query;

                switch(method){
                    case FETCH_DATA:
                        query = "SELECT id, customer_id, phone_id, phone_name, status FROM phone_order WHERE customer_id=?";
                        stmt = conn.prepareStatement(query);
                        stmt.setInt(1, customer_id);//need to change to 1/2/3/4/5
                        result = stmt.executeQuery();
                        return result;
                }
            }
            catch (Exception e) {
                Log.e("ERROR MySQL Statement", e.getMessage());
            }
            return null;
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

    //RecyclerView methods
    private class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartHolder> {
        private LayoutInflater mInflater;
        private int itemCount;
        private Background bg;
        private ResultSet result;

        public MyCartAdapter(Background bg){
            this.bg = bg;
            updateResultSet();
            mInflater = LayoutInflater.from(MyCart.this);
        }

        class MyCartHolder extends RecyclerView.ViewHolder{
            ImageView mIVImage2;
            TextView mTVOrderId;
            TextView mTVPhoneId;
            TextView mTVStatus;
            TableLayout mTableLayout2;

            final MyCartAdapter mAdapter2;

            public MyCartHolder(@NonNull View itemView, MyCartAdapter adapter){
                super(itemView);

                mIVImage2 = (ImageView) itemView.findViewById(R.id.ivImage2);
                mTVOrderId = (TextView) itemView.findViewById(R.id.tvOrderId);
                mTVPhoneId = (TextView) itemView.findViewById(R.id.tvPhoneId);
                mTVStatus = (TextView) itemView.findViewById(R.id.tvStatus);
                mTableLayout2 = (TableLayout) itemView.findViewById(R.id.layout_table2);

                this.mAdapter2 = adapter;
            }
        }

        @NonNull
        @Override
        public MyCartAdapter.MyCartHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(R.layout.my_cart_layout, viewGroup, false);//need create new
            return new MyCartHolder(mItemView, this);
        }


        public void onBindViewHolder(@NonNull MyCartHolder myCartHolder, int position) {
            try {
                result.first();
                for (int i = 0; i < position; i++) {
                    result.next();
                }

                //pass to ShowItem.java
                final int orderId = result.getInt(1);
                final int customerId = result.getInt(2);
                final int phoneId = result.getInt(3);
                final String phoneName = result.getString(4);
                final String status = result.getString(5);

//                myCartHolder.mIVImage2.setImageResource(R.drawable.ic_phone_icon);
                if(phoneId == 1){
                    myCartHolder.mIVImage2.setImageResource(R.drawable.ic_huawei);
                }
                if(phoneId == 2){
                    myCartHolder.mIVImage2.setImageResource(R.drawable.ic_xiaomi);

                }
                if(phoneId == 3){
                    myCartHolder.mIVImage2.setImageResource(R.drawable.ic_oppo);

                }
                if(phoneId == 4){
                    myCartHolder.mIVImage2.setImageResource(R.drawable.ic_vivo);

                }
                if(phoneId == 5){
                    myCartHolder.mIVImage2.setImageResource(R.drawable.ic_realme);

                }

                myCartHolder.mTVOrderId.setText("Order ID: #" + orderId);
                myCartHolder.mTVPhoneId.setText("Phone ID: #" + phoneId);
                myCartHolder.mTVStatus.setText("Status: " + status);

                myCartHolder.mTableLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MyCart.this, MyCartItem.class);
                        i.putExtra("orderId", orderId);
                        i.putExtra("customerId", customerId);
                        i.putExtra("phoneId", phoneId);//need to change to 1/2/3/4/5
                        i.putExtra("phoneName", phoneName);
                        i.putExtra("status", status);

                        startActivity(i);
                    }
                });
            }
            catch (SQLException e) {
                Log.d("ERROR BIND VIEW", e.getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        private int getResultCount() {
            try {
                result.last();
                int count = result.getRow();
                result.first();
                return count;
            } catch (SQLException e) {

            }
            return 0;
        }

        public void updateResultSet() {
            try {
                bg.closeConn();
                bg = new Background(Background.FETCH_DATA);
                this.result = this.bg.execute().get();
                itemCount = getResultCount();
            } catch (ExecutionException e) {
                Log.e("ERROR EXECUTION", e.getMessage());
            } catch (InterruptedException e) {
                Log.e("ERROR INTERRUPTED", e.getMessage());
            }
        }

    }
}
