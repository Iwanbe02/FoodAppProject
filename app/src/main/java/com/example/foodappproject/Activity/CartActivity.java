package com.example.foodappproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodappproject.Adapter.CartAdapter;
import com.example.foodappproject.Helper.ChangeNumberItemsListener;
import com.example.foodappproject.Helper.ManagementCart;
import com.example.foodappproject.Helper.TinyDB;
import com.example.foodappproject.R;
import com.example.foodappproject.databinding.ActivityCartBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    private ManagementCart managementCart;
    private String uid;
    private double tax;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managementCart = new ManagementCart(this);
        uid = getIntent().getStringExtra("uid");

        calculateCard();
        initCartList();
        setVariable();
    }

    private void initCartList() {
        if (managementCart.getListCart(uid).isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.backToShoppingBtn.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        }else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.backToShoppingBtn.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managementCart.getListCart(uid), managementCart, () -> calculateCard()));
    }

    private void calculateCard(){
        double percentTax = 0.02; //percent 2& tax
        double delivery = 10; //10 doLLAR
        tax = Math.round(managementCart.getTotalFee(uid) * percentTax * 100.0) / 100;
        double total = Math.round((managementCart.getTotalFee(uid) + tax +delivery) * 100) / 100;
        double itemTotal = Math.round(managementCart.getTotalFee(uid) * 100) / 100;
        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }
    private void clearCart() {
        TinyDB tinyDB = new TinyDB(this); // Assuming you're in an Activity context
        tinyDB.clear(); // Clear the cart storage
    }
    private void setVariable() {
        //binding.backBtn.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, MainActivity.class)));
        binding.backBtn.setOnClickListener(v -> finish());
        //binding.backToShoppingBtn.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, MainActivity.class)));
        binding.backToShoppingBtn.setOnClickListener(v -> finish());
        binding.checkOutBtn.setOnClickListener(v -> {
            DatabaseReference myRef =database.getReference("Order");
            Map<String, Object> orderData =new HashMap<>();
            double total = Math.round((managementCart.getTotalFee(uid) + tax + 10) * 100) / 100;
            String Id = myRef.push().getKey();
            orderData.put("Member_Id", uid);
            orderData.put("Total_Price", total);
            myRef.child(Id).setValue(orderData);
            clearCart();
            Toast.makeText(CartActivity.this, "Order successfully", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}