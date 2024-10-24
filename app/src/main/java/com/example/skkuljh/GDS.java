package com.example.skkuljh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class GDS extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView questionText;
    private RadioGroup answerGroup;
    private Button backButton, nextButton;
    private Button exitButton;  // 나가기 버튼 추가

    private String[] questions = {
            "1. 일상생활에서 기본적으로 만족하고 계신지요?",
            "2. 귀하의 취미와 활동중의 많은 부분을 포기하셨는지요?",
            "3. 인생이 허무하다고 느끼십니까?",
            "4. 싫증이 자주 나십니까?",
            "5. 미래에 희망적이십니까?",
            "6. 생각않고 잊어버리려고 해도 자꾸 떠오르는 생각으로 괴로워 하십니까?",
            "7. 항상 기분이 좋으십니까?",
            "8. 좋지 않은 일이 생길까봐 두려워하십니까?",
            "9. 일상이 거의 기분좋게 느끼십니까?",
            "10. 귀하는 자주 무력감에 젖습니까?",
            "11. 자주 불안하고 속을 태우십니까?",
            "12. 바깥에 출타하여 새로운 일을 하는 것보다 집에 계시는 편이 낫습니까?",
            "13. 귀하는 귀하의 장래에 대하여 자주 걱정하십니까?",
            "14. 대부분의 사람들보다 귀하가 더 기억력 문제를 가지고 계시다고 생각하십니까?",
            "15. 귀하께서는 현재 살고 계신다는 것에 대하여 매우 흡족하게 생각하십니까?",
            "16. 귀하는 자주 맥이 풀리며 우울하십니까?",
            "17. 귀하는 현재의 자신이 매우 가치있는 존재라고 느끼시는지요?",
            "18. 과거에 일어났던 일들에 대하여 많이 답답하고 괴로워하십니까?",
            "19. 귀하는 인생을 즐기시고 계십니까?",
            "20. 새로운 일을 시작하시기가 힘드십니까?",
            "21. 귀하는 자신이 원기 왕성하다고 생각하시는지요?",
            "22. 귀하는 귀하의 처지가 희망적이 아니라고 보십니까?",
            "23. 귀하는 대부분의 사람들이 자신보다 더 나은 생활을 하신다고 생각하십니까?",
            "24. 사소한 일들에 자주 화가 나십니까?",
            "25. 귀하는 울 것 같은 심정을 자주 가지십니까?",
            "26. 무엇에 집중하기가 곤란하십니까?",
            "27. 아침에 즐겁게 일어나십니까?",
            "28. 사람들과 어울리는 것을 피하는 편입니까?",
            "29. 일을 쉽게 경험하십니까?",
            "30. 귀하의 심정은 예전과 다름없이 맑습니까?",
    };
    private int[] answers;  // 각 질문에 대한 답 저장 (1: 예, 0: 아니오)
    private int currentQuestion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gds);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        questionText = findViewById(R.id.questionText);
        answerGroup = findViewById(R.id.answerGroup);
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        exitButton = findViewById(R.id.exitButton);  // 나가기 버튼 초기화

        answers = new int[questions.length];  // 답변 배열 초기화

        updateQuestion();  // 첫 질문 표시

        // 다음 버튼 클릭 리스너
        nextButton.setOnClickListener(v -> {
            if (!isAnswerSelected()) {
                Toast.makeText(this, "답변을 선택해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            saveAnswer();  // 현재 질문의 답변 저장
            if (currentQuestion < questions.length - 1) {
                currentQuestion++;
                updateQuestion();
            } else {
                submitAnswers();  // 모든 질문 완료 후 제출
            }
        });

        // 뒤로 버튼 클릭 리스너
        backButton.setOnClickListener(v -> {
            if (currentQuestion > 0) {
                currentQuestion--;
                updateQuestion();
            }
        });

        // 나가기 버튼 클릭 리스너
        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(GDS.this, Login_Result_Activity.class);
            intent.putExtra("email", mAuth.getCurrentUser().getEmail()); // 현재 사용자 이메일 전달
            startActivity(intent);
            finish();  // 현재 Activity 종료
        });
    }

    private void updateQuestion() {
        questionText.setText(questions[currentQuestion]);
        answerGroup.clearCheck();  // 선택 초기화

        // 이전에 저장된 답이 있을 경우 선택 상태 유지
        if (answers[currentQuestion] == 1) {
            ((RadioButton) findViewById(R.id.yesButton)).setChecked(true);
        } else if (answers[currentQuestion] == 0) {
            ((RadioButton) findViewById(R.id.noButton)).setChecked(true);
        }
    }

    private boolean isAnswerSelected() {
        return answerGroup.getCheckedRadioButtonId() != -1;
    }

    private void saveAnswer() {
        int selectedId = answerGroup.getCheckedRadioButtonId();
        answers[currentQuestion] = (selectedId == R.id.yesButton) ? 1 : 0;
    }

    private void submitAnswers() {
        int score = calculateScore();  // GDS 점수 계산
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();  // 현재 로그인한 사용자의 UID

        Map<String, Object> scoreData = new HashMap<>();
        scoreData.put("userId", userId);
        scoreData.put("score", score);
        scoreData.put("timestamp",FieldValue.serverTimestamp());  // Firestore 서버의 타임스탬프 사용

        db.collection("GDS_Scores").add(scoreData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "점수가 저장되었습니다!", Toast.LENGTH_SHORT).show();
                    // 결과 화면으로 이동
                    Intent intent = new Intent(GDS.this, GDSResultActivity.class);
                    intent.putExtra("gds_score", score);  // 점수 전달
                    startActivity(intent);
                    finish(); // 현재 Activity 종료
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < answers.length; i++) {
            if (i == 0 || i == 4 || i == 6 || i == 8 || i == 14 || i == 16 || i == 18 || i == 20 || i == 26 || i == 28 || i == 29) {
                // 긍정 문항: "아니오" 체크 시 +1점
                score += (answers[i] == 0) ? 1 : 0;
            } else {
                // 부정 문항: '예' 체크 시 +1점
                score += answers[i];
            }
        }
        return score;
    }
}