package com.example.skkuljh;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    private LineChart lineChart;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<String> gdsDates;  // GDS 검사 날짜를 저장할 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        lineChart = findViewById(R.id.lineChart);
        Button backButton = findViewById(R.id.backButton);
        Button exitButton = findViewById(R.id.exitButton);

        // X축 설정
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(20f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // 왼쪽 Y축 설정
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(20f);
        leftAxis.setAxisMinimum(0f);

        // 오른쪽 Y축 비활성화
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Firebase 인증 및 Firestore 초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadGraphData();  // Firebase 데이터 로드

        // 뒤로 가기 버튼 클릭 이벤트
        backButton.setOnClickListener(v -> finish());

        // 나가기 버튼 클릭 이벤트
        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(GraphActivity.this, Login_Result_Activity.class);
            intent.putExtra("email", mAuth.getCurrentUser().getEmail());
            startActivity(intent);
            finish();
        });

        // 그래프 값 선택 리스너 설정
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // 선택된 값의 인덱스를 사용하여 날짜를 가져옴
                int index = (int) e.getX();
                if (index >= 0 && index < gdsDates.size()) {
                    String date = gdsDates.get(index);
                    CustomToast.showToast(GraphActivity.this, "검사 날짜: " + date, R.drawable.msg,3000);
                }
            }

            @Override
            public void onNothingSelected() {
                // 아무것도 선택되지 않을 때 처리할 내용
            }
        });
    }

    // Firebase에서 그래프 데이터 로드
    private void loadGraphData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            CustomToast.showToast(this, "로그인이 필요합니다.", R.drawable.msg, 3000);
            finish();
            return;
        }

        String userId = currentUser.getUid();
        gdsDates = new ArrayList<>();  // 날짜 리스트 초기화

        db.collection("GDS_Scores")
                .whereEqualTo("userId", userId).orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    Log.d("GraphActivity", "쿼리 완료");
                    if (task.isSuccessful()) {
                        Log.d("GraphActivity", "1포인트");
                        List<Entry> entries = new ArrayList<>();
                        int index = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int score = document.getLong("score").intValue();

                            // timestamp 필드를 com.google.firebase.Timestamp로 가져옴
                            com.google.firebase.Timestamp timestampObj = document.getTimestamp("timestamp");
                            long timestamp;

                            if (timestampObj != null) {
                                timestamp = timestampObj.toDate().getTime();
                                Log.d("GraphActivity", "Document ID: " + document.getId() + ", Timestamp: " + timestamp);
                            } else {
                                Log.d("GraphActivity", "잘못된 timestamp: " + timestampObj);
                                continue;  // timestamp가 null일 경우 건너뜀
                            }

                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(timestamp));

                            entries.add(new Entry(index++, score));
                            gdsDates.add(date);
                        }

                        if (!entries.isEmpty()) {
                            LineDataSet dataSet = new LineDataSet(entries, "GDS 점수 변화");
                            dataSet.setLineWidth(2f);
                            dataSet.setCircleRadius(5f);
                            dataSet.setValueTextSize(20f);
                            dataSet.setColor(Color.BLUE);

                            // 소수점 없는 값 포맷터 설정
                            dataSet.setValueFormatter(new ValueFormatter() {
                                @Override
                                public String getPointLabel(Entry entry) {
                                    return String.valueOf((int) entry.getY());
                                }
                            });

                            LineData lineData = new LineData(dataSet);
                            lineChart.setData(lineData);

                            // X축 좌우 스크롤 가능하도록 설정
                            lineChart.setDragEnabled(true);
                            lineChart.setScaleEnabled(false);  // 줌 기능 비활성화 (필요하면 제거 가능)

                            // 보이는 X축 범위 설정 (최신 8개의 데이터만 보이도록)
                            lineChart.setVisibleXRangeMaximum(8f);
                            lineChart.moveViewToX(entries.size() - 8); // 최신 데이터 기준으로 이동

                            // 범례(legend) 설정
                            Legend legend = lineChart.getLegend();
                            legend.setTextSize(15f);
                            legend.setFormSize(15f);
                            legend.setXEntrySpace(10f);
                            legend.setYEntrySpace(5f);
                            legend.setForm(Legend.LegendForm.LINE);

                            // 그래프 설명 비활성화
                            Description description = new Description();
                            description.setText("");
                            lineChart.setDescription(description);

                            lineChart.invalidate(); // 변경사항 반영
                        } else {
                            CustomToast.showToast(this, "데이터가 없습니다.", R.drawable.msg, 3000);
                        }
                    } else {
                        Log.e("GraphActivity", "쿼리 실패: ", task.getException());
                        CustomToast.showToast(this, "데이터를 불러오지 못했습니다.", R.drawable.msg, 3000);
                    }
                });
    }
}
