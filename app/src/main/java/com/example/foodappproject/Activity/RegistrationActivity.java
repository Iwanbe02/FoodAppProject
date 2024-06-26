package com.example.foodappproject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodappproject.R;
import com.example.foodappproject.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class RegistrationActivity extends BaseActivity {
    ActivityRegistrationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
    }

    private void setVariable() {
        binding.signupBtn.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            String retypePassword = binding.repassword.getText().toString();
            if (password.length() < 6){
                Toast.makeText(RegistrationActivity.this, "Your password must be 6 character", Toast.LENGTH_SHORT).show();
            }
            if (!retypePassword.equals(retypePassword)){
                Toast.makeText(RegistrationActivity.this, "Your retype password must match your password", Toast.LENGTH_SHORT).show();
            }
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, task -> {
                if (task.isSuccessful()){
                    Log.i(TAG, "onCompleted: ");
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                }else {
                    Log.i(TAG, "failure: " + task.getException());
                    Toast.makeText(RegistrationActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}
