package com.example.skkuljh;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GDSResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gds_result);

        TextView scoreText = findViewById(R.id.scoreText);
        TextView resultText = findViewById(R.id.resultText);
        TextView messageText = findViewById(R.id.messageText);
        Button graphButton = findViewById(R.id.graphButton);

        // Intent로 전달받은 점수 표시
        int score = getIntent().getIntExtra("gds_score", 0);
        scoreText.setText("점수: " + score);

        // 점수에 따라 결과 메시지 설정
        String resultMessage;
        String contextMessage;
        if (score >= 0 && score <= 10) {
            resultMessage = "정상";
            contextMessage = "걱정하지 않아도 될 단계입니다! 혹시 오늘 우울한 일이 있으셨나요? 산책이나 가벼운 운동을 통해 환기할 수 있답니다! 제가 항상 응원할게요!";
        } else if (score >= 11 && score <= 20) {
            resultMessage = "가벼운 우울증";
            contextMessage = "조금 걱정이 돼요. 병원에 가보는 것은 어떨까요? 우울증은 마음의 감기이기 때문에, 너무 걱정하지는 마세요! 그렇지만 병원에 일찍 가서 치료하는 것 또한 중요하답니다!";
        } else if (score >= 21 && score <= 30) {
            resultMessage = "심한 우울증";
            contextMessage = "걱정이 많이 돼요. 병원에 가서 적절한 상담을 받는 것이 좋아보여요. 의사 선생님과 함께 이야기하는 것은 어떨까요? 그렇지만 겁먹을 필요는 없어요! 항상 옆에서 응원할게요!";
        } else {
            resultMessage = "점수를 확인할 수 없습니다.";
            contextMessage="error";
        }

        resultText.setText(resultMessage); // 결과 텍스트 설정
        messageText.setText(contextMessage);

        // Graph 화면으로 이동
        graphButton.setOnClickListener(v -> {
            Intent intent = new Intent(GDSResultActivity.this, GraphActivity.class);
            startActivity(intent);
        });
    }
}