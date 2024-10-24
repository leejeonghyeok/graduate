package com.example.skkuljh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        mAuth = FirebaseAuth.getInstance(); // FirebaseAuth 초기화

        RelativeLayout sendResetEmailButton = findViewById(R.id.SendResetEmail);
        sendResetEmailButton.setOnClickListener(v -> {
            sendPasswordResetEmail();
        });
    }

    private void sendPasswordResetEmail() {
        TextInputEditText emailInput = findViewById(R.id.TextinputResetEmail);
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            CustomToast.showToast(ResetPasswordActivity.this, "이메일을 입력해주세요 ", R.drawable.msg,3000);
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CustomToast.showToast(ResetPasswordActivity.this, "이메일이 전송되었습니다. ", R.drawable.msg,3000);
                        finish();
                    } else {
                        CustomToast.showToast(ResetPasswordActivity.this, "이메일 전송 실패 ", R.drawable.msg,3000);
                    }
                });
    }
}