package com.example.foodappproject.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodappproject.Adapter.CartAdapter;
import com.example.foodappproject.Helper.ManagementCart;
import com.example.foodappproject.Helper.TinyDB;
import com.example.foodappproject.R;
import com.example.foodappproject.databinding.ActivityCartBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    private ManagementCart managementCart;
    private String uid;
    private double tax;
    private TinyDB tinyDB;

    private static final String PAYPAL_CLIENT_ID = "AfhXW-vIungADWV-B4DlBiEdEIdfEj1jpSe4JTekDUWw1b5NKuHoIf1z3EO2xlkt78APTH-5dKL2Llv-";
    private static final int PAYPAL_REQUEST_CODE = 123;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(PAYPAL_CLIENT_ID);

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

        // Start PayPal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    private void processPayment(double amount) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD",
                "Order Payment", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        // Payment is successful, save the order to Firebase
                        saveOrderToFirebase();
                        Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Invalid payment", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveOrderToFirebase() {
        DatabaseReference myRef = database.getReference("Order");
        Map<String, Object> orderData = new HashMap<>();
        double total = Math.round((managementCart.getTotalFee(uid) + tax + 10) * 100) / 100;
        String Id = myRef.push().getKey();
        orderData.put("Member_Id", uid);
        orderData.put("Total_Price", total);
        myRef.child(Id).setValue(orderData);
        clearCart();
        Toast.makeText(CartActivity.this, "Order successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void calculateCard() {
        double percentTax = 0.02; // 2% tax
        double delivery = 10; // $10 delivery fee
        tax = Math.round(managementCart.getTotalFee(uid) * percentTax * 100.0) / 100;
        double total = Math.round((managementCart.getTotalFee(uid) + tax + delivery) * 100) / 100;
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

    private void initCartList() {
        if (managementCart.getListCart(uid).isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.backToShoppingBtn.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.backToShoppingBtn.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managementCart.getListCart(uid), managementCart, this::calculateCard));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.backToShoppingBtn.setOnClickListener(v -> finish());
        binding.checkOutBtn.setOnClickListener(v -> {
            double total = Math.round((managementCart.getTotalFee(uid) + tax + 10) * 100) / 100;
            processPayment(total);
        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
