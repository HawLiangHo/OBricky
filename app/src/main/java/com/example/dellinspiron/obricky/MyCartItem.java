package com.example.dellinspiron.obricky;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCartItem extends AppCompatActivity {

    ImageView ivImagePhone;

    TextView tvOrderId;
    TextView tvPhoneId;
    TextView tvPhoneName;
    TextView tvStatus;


    int orderId;
    int phoneId;
    String phoneName;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart_item);

//        Toast.makeText(this, "My Cart Item", Toast.LENGTH_SHORT).show();

        ivImagePhone = findViewById(R.id.ivPhoneImage);
        tvOrderId = findViewById(R.id.tvOrderTitle);
        tvPhoneId = findViewById(R.id.tvPhoneId);
        tvPhoneName = findViewById(R.id.tvPhoneName);
        tvStatus = findViewById(R.id.tvStatus);

        Intent i = getIntent();
        orderId = i.getIntExtra("orderId", 1);
        phoneId = i.getIntExtra("phoneId",1);
        phoneName = i.getStringExtra("phoneName");
        status = i.getStringExtra("status");

        if(phoneId == 1){
            ivImagePhone.setImageResource(R.drawable.ic_huawei);
        }
        if(phoneId == 2){
            ivImagePhone.setImageResource(R.drawable.ic_xiaomi);
        }
        if(phoneId == 3){
            ivImagePhone.setImageResource(R.drawable.ic_oppo);
        }
        if(phoneId == 4){
            ivImagePhone.setImageResource(R.drawable.ic_vivo);
        }
        if(phoneId == 5){
            ivImagePhone.setImageResource(R.drawable.ic_realme);
        }

        tvOrderId.setText("Order #" + orderId + " Details");
        tvPhoneId.setText(phoneId + "");
        tvPhoneName.setText(phoneName);
        tvStatus.setText(status);

    }
}
