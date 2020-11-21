package com.example.dellinspiron.obricky;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class MainActivity extends AppCompatActivity {

    TextView headerEmail;
    TextView headerCredit;

    private RecyclerView mRecyclerView;
    private MainActivityAdapter mAdapter;

    private int customer_id;
    private float totalAmt;
    private String address;
    private String password;
    private String email;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;

    public static int REQUEST_CODE4 = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customer_id = getIntent().getIntExtra(Login.EXTRA_CUSTOMER_ID, 1);
        totalAmt = getIntent().getFloatExtra(Login.EXTRA_WALLET_BALANCE, 0);
        address = getIntent().getStringExtra(Login.EXTRA_ADDRESS);
        password = getIntent().getStringExtra(Login.EXTRA_PASSWORD);
        email = getIntent().getStringExtra(Login.EXTRA_EMAIL);

        //Drawer menu initilization
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //setting up navigation drawer menu
        NavigationView nvDrawer = (NavigationView)findViewById(R.id.nv);
        //call setupDrawerContent
        setupDrawerContent(nvDrawer);

        //set up header view details
        View headerView = nvDrawer.getHeaderView(0);
        headerEmail = headerView.findViewById(R.id.tvEmail);
        headerEmail.setText(email);

        View headerView2 = nvDrawer.getHeaderView(0);
        headerCredit = headerView2.findViewById(R.id.tvCredit);
        headerCredit.setText("Credit: RM " + totalAmt + "0");
        //end set up header view details

        Background bg = new Background(Background.FETCH_DATA);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCheckAppointment);
        mAdapter = new MainActivityAdapter(bg);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Class aClass;
        switch (menuItem.getItemId()) {
            case R.id.reload:
                aClass = Reload.class;
                break;
            case R.id.myCart:
                aClass = MyCart.class;
                break;
            case R.id.profile:
                aClass = Profile.class;
                break;
            case R.id.password:
                aClass = ChangePassword.class;
                break;
            case R.id.logout:
                aClass = Login.class;
                break;
            default:
                aClass = Reload.class;
        }

        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
        Intent i = new Intent(this, aClass);
        i.putExtra(Login.EXTRA_CUSTOMER_ID,customer_id);
        i.putExtra(Login.EXTRA_EMAIL,email);
        i.putExtra(Login.EXTRA_PASSWORD,password);
        i.putExtra(Login.EXTRA_ADDRESS,address);
        i.putExtra(Login.EXTRA_WALLET_BALANCE,totalAmt);

        if(aClass == Reload.class){
            startActivityForResult(i, 1);
        }else if(aClass == ChangePassword.class){
            startActivityForResult(i, 2);

        }else if(aClass == Profile.class) {
            startActivityForResult(i, 3);

        }else{
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                totalAmt = data.getFloatExtra("TOTAL_AMOUNT", 0);
                headerCredit.setText("Credit: RM " + totalAmt + "0");
            }
        }
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                password = data.getStringExtra("Password");

            }
        }else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                email = data.getStringExtra("EMAIL_EDIT");
                address = data.getStringExtra("ADDRESS_EDIT");
                headerEmail.setText(email);
            }
        }
        else if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                totalAmt = data.getFloatExtra("TOTAL_AMOUNT", 0);
                headerCredit.setText("Credit: RM " + totalAmt + "0");
                Toast.makeText(MainActivity.this, "Purchase Successfully!", Toast.LENGTH_SHORT).show();
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
                        query = "SELECT id, phone_name, network_technology, body_dimensions, display_type, storage_memory, main_camera, selfie_camera, comms, battery, price FROM phone_list";
                        stmt = conn.prepareStatement(query);
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
    private class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityHolder> {
        private LayoutInflater mInflater;
        private int itemCount;
        private Background bg;
        private ResultSet result;

        public MainActivityAdapter(Background bg){
            this.bg = bg;
            updateResultSet();
            mInflater = LayoutInflater.from(MainActivity.this);
        }

        class MainActivityHolder extends RecyclerView.ViewHolder{
            ImageView mIVImage;
            TextView mTVDetails;
            TextView mTVPrice;
            TableLayout mTableLayout;

            final MainActivityAdapter mAdapter;

            public MainActivityHolder(@NonNull View itemView,MainActivityAdapter adapter){
                super(itemView);

                mIVImage = (ImageView) itemView.findViewById(R.id.ivImage);
                mTVDetails = (TextView) itemView.findViewById(R.id.tvDetails);
                mTVPrice = (TextView) itemView.findViewById(R.id.tvPrice);
                mTableLayout = (TableLayout) itemView.findViewById(R.id.layout_table);

                this.mAdapter = adapter;
            }
        }

        @NonNull
        @Override
        public MainActivityHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mItemView = mInflater.inflate(R.layout.main_activity_layout, viewGroup, false);
            return new MainActivityHolder(mItemView, this);
        }


        public void onBindViewHolder(@NonNull MainActivityHolder mainActivityHolder, int position) {
            try {
                result.first();
                for (int i = 0; i < position; i++) {
                    result.next();
                }

                //pass to ShowItem.java
                final int phoneId = result.getInt(1);
                final String phoneName = result.getString(2);
                final String networkTechnology = result.getString(3);
                final String bodyDimensions = result.getString(4);
                final String displayType = result.getString(5);
                final String storageMemory = result.getString(6);
                final String mainCamera = result.getString(7);
                final String selfieCamera = result.getString(8);
                final String comms = result.getString(9);
                final String battery = result.getString(10);
                final Float price = result.getFloat(11);

                //set the details layout
                for (int i = 0; i < 5; i++) {
                    if(phoneId == 1){
                        mainActivityHolder.mIVImage.setImageResource(ic_huawei);
                    }
                    if(phoneId == 2){
                        mainActivityHolder.mIVImage.setImageResource(ic_xiaomi);
                    }
                    if(phoneId == 3){
                        mainActivityHolder.mIVImage.setImageResource(ic_oppo);
                    }
                    if(phoneId == 4){
                        mainActivityHolder.mIVImage.setImageResource(ic_vivo);
                    }
                    if(phoneId == 5){
                        mainActivityHolder.mIVImage.setImageResource(ic_realme);
                    }
                }


                mainActivityHolder.mTVDetails.setText(phoneName);
                mainActivityHolder.mTVPrice.setText("RM " + price + "0");
//                mainActivityHolder.TVstatus.setText(" " + status);

                mainActivityHolder.mTableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, ShowItem.class);
                        i.putExtra(Login.EXTRA_WALLET_BALANCE, totalAmt);
                        i.putExtra(Login.EXTRA_CUSTOMER_ID,customer_id);
                        i.putExtra(ShowItem.EXTRA_PHONE_ID, phoneId);//need to change to 1/2/3/4/5
                        i.putExtra("phoneName", phoneName);
                        i.putExtra("networkTechnology", networkTechnology);
                        i.putExtra("bodyDimensions", bodyDimensions);
                        i.putExtra("displayType", displayType);
                        i.putExtra("storageMemory", storageMemory);
                        i.putExtra("mainCamera", mainCamera);
                        i.putExtra("selfieCamera", selfieCamera);
                        i.putExtra("comms", comms);
                        i.putExtra("battery", battery);
                        i.putExtra(ShowItem.EXTRA_PRICE, price);

                        startActivityForResult(i, REQUEST_CODE4);
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
