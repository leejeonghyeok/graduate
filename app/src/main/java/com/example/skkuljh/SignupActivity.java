package com.example.skkuljh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputEditText TextinputEmail, TextinputPassword,TextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        TextinputEmail = findViewById(R.id.TextinputEmail);
        TextinputPassword = findViewById(R.id.TextinputPassword);

        Button canclebtn = findViewById(R.id.CancelButton);

        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });
        findViewById(R.id.SignupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = TextinputEmail.getText().toString();
                String password = TextinputPassword.getText().toString();
                registerUser(email, password);
            }
        });
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            CustomToast.showToast(SignupActivity.this, "로그인 실패: " + task.getException().getMessage(), R.drawable.msg,3000);
                            finish(); // 이전 화면으로 돌아가기
                        } else {
                            // 회원가입 실패
                            CustomToast.showToast(SignupActivity.this, "로그인 실패: " + task.getException().getMessage(), R.drawable.msg,3000);
                        }
                    }
                });
    }
}