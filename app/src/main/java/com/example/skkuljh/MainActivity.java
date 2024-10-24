package com.example.skkuljh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.skkuljh.Login_Result_Activity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    TextInputEditText TextinputEmail, TextinputPassword;
    RelativeLayout Login, LoginGoogle;
    Button SearchPassword, Signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FirebaseAuth 초기화
        mAuth = FirebaseAuth.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextinputEmail = findViewById(R.id.TextinputEmail);
        TextinputPassword = findViewById(R.id.TextinputPassword);
        Login = findViewById(R.id.Login);
        LoginGoogle = findViewById(R.id.LoginGoogle);
        SearchPassword = findViewById(R.id.SearchPassword);
        Signup = findViewById(R.id.Signup);

        // 구글 로그인 옵션 설정
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("709694022548-3vv5k1t60o35m7fkshh3v09er7r98ggg.apps.googleusercontent.com") // Firebase 콘솔에서 발급받은 웹 클라이언트 ID
                .requestEmail()
                .build();

        // GoogleSignInClient 초기화
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // 비밀번호 찾기 버튼 클릭 리스너
        SearchPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // 일반 로그인 버튼 클릭 리스너
        Login.setClickable(true);
        Login.setOnClickListener(v -> {
            String email = TextinputEmail.getText().toString().trim(); // trim()으로 공백 제거
            String password = TextinputPassword.getText().toString().trim(); // trim()으로 공백 제거

            // 입력값 확인
            if (email.isEmpty() || password.isEmpty()) {
                // 토스트 메시지로 입력값이 비어있음을 알림
                CustomToast.showToast(MainActivity.this, "이메일과 비밀번호가 필요합니다", R.drawable.msg,3000);
                return; // 입력값이 비어있으면 로그인 시도 중단
            }

            loginUser(email, password);
        });

        // 회원가입 버튼 클릭 리스너
        Signup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // 구글 로그인 버튼 클릭 리스너
        LoginGoogle.setOnClickListener(v -> signInWithGoogle());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 현재 로그인된 사용자 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // 로그인된 경우 다음 화면으로 이동
            Intent intent = new Intent(MainActivity.this, Login_Result_Activity.class);
            intent.putExtra("email", currentUser.getEmail()); // 사용자 이메일 전달
            startActivity(intent);
            finish();  // MainActivity 종료
        }
        // 로그인되지 않은 경우, 그냥 현재 로그인 화면을 유지
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(MainActivity.this, Login_Result_Activity.class);
                        intent.putExtra("email", user.getEmail());
                        startActivity(intent);
                    } else {
                        // 로그인 실패: 회원가입 안된 계정일 경우에 대한 메시지
                        String errorMessage = task.getException().getMessage();
                        if (errorMessage.contains("There is no user record corresponding to this identifier")) {
                            CustomToast.showToast(MainActivity.this, "회원가입이 필요합니다", R.drawable.msg,3000);
                        } else {
                            CustomToast.showToast(MainActivity.this, "로그인 실패: 비밀번호와 아이디를 확인해주세요", R.drawable.msg,3000);
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // 구글 계정으로 Firebase에 인증
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // Firebase에 구글 인증 정보 추가
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show();
                        CustomToast.showToast(MainActivity.this, "구글 로그인 실패", R.drawable.msg,3000);
                        // 로그인 성공 후 다음 화면으로 이동
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(MainActivity.this, Login_Result_Activity.class);
                        intent.putExtra("email", user.getEmail());
                        startActivity(intent);
                    } else {
                        CustomToast.showToast(MainActivity.this, "구글 로그인 성공", R.drawable.msg,3000);
                    }
                });
    }
}