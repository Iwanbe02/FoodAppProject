package com.example.foodappproject.Activity;

import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodappproject.Domain.Foods;
import com.example.foodappproject.Helper.ManagementCart;
import com.example.foodappproject.R;
import com.example.foodappproject.databinding.ActivityDetailBinding;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;
    private ManagementCart managementCart;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        uid = getIntent().getStringExtra("uid");
        getIntentExtra();
        setVariable();

    }
    private void setVariable(){
        managementCart = new ManagementCart(this);
        binding.backBtn.setOnClickListener(v -> finish());
        Glide.with(this)
                .load(object.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(60))
                .into(binding.pic);
        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar() + " Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num * object.getPrice() + "$"));
        binding.plusBtn.setOnClickListener(v -> {
            num = num + 1;
            binding.numTxt.setText(num + "");
            binding.totalTxt.setText("$" + (num * object.getPrice()));

        });
        binding.minusBtn.setOnClickListener(v ->{
            if (num > 1){
                num = num -1;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText("$" + (num * object.getPrice()));
            }
        });
        binding.addBtn.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managementCart.insertFood(object, uid);
        });
    }
    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}