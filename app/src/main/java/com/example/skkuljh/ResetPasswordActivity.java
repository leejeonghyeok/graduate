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
        setContentView(R.layout.reset_password); // 비밀번호 찾기 화면의 XML 레이아웃 파일로 변경

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
                        // 성공적으로 이메일이 전송되면 로그인 화면으로 돌아가기
                        finish(); // 현재 액티비티 종료
                    } else {
                        CustomToast.showToast(ResetPasswordActivity.this, "이메일 전송 실패 ", R.drawable.msg,3000);
                    }
                });
    }
}