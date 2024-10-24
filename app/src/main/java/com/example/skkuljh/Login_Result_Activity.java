package com.example.skkuljh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Login_Result_Activity extends AppCompatActivity {

    TextView Textview_get;
    private RecyclerView mRecycleview;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Button self, graph, find_hos;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);

        // UI 요소 초기화
        Textview_get = findViewById(R.id.Textview_get);
        mRecycleview = findViewById(R.id.my_recycle_view);
        ImageView backButton = findViewById(R.id.back_button_login);

        self = findViewById(R.id.self);
        graph = findViewById(R.id.graph);
        find_hos = findViewById(R.id.findhos);

        // LayoutManager 설정
        mLayoutManager = new LinearLayoutManager(this);
        mRecycleview.setLayoutManager(mLayoutManager);

        // Intent로부터 이메일 및 패스워드 가져오기 (null 확인)
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");

            if (email != null) {
                Textview_get.setText("환영합니다!\n" + email + " 님!");
            } else {
                Textview_get.setText("이메일 정보를 가져올 수 없습니다.");
            }
        } else {
            Textview_get.setText("데이터 전달에 실패했습니다.");
        }

        // RecyclerView 설정
        mRecycleview.setHasFixedSize(true);
        queue = Volley.newRequestQueue(this);

        backButton.setOnClickListener(v -> onBackPressed()); // onBackPressed 호출

        // 뉴스 가져오기 메서드 호출
        getNews();

        self.setOnClickListener(v -> {
            Intent intent1 = new Intent(Login_Result_Activity.this, GDS.class);
            startActivity(intent1);
        });

        graph.setOnClickListener(v -> {
            Intent intent2 = new Intent(Login_Result_Activity.this, GraphActivity.class);
            startActivity(intent2);
        });

        find_hos.setOnClickListener(v -> {
            Intent intent3 = new Intent(Login_Result_Activity.this, Map.class);
            startActivity(intent3);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Firebase에서 로그아웃 처리
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Login_Result_Activity.this, MainActivity.class); // MainActivity로 인텐트
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // 스택 초기화
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    public void getNews() {
        String url = "https://openapi.naver.com/v1/search/news.json?query=정신건강정보&display=10";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("items");

                        List<NewsData> news = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            NewsData newsData = new NewsData();
                            newsData.setTitle(obj.getString("title"));
                            newsData.setDespcription(obj.getString("description"));
                            newsData.setUrl(obj.getString("link"));  // 링크 저장

                            news.add(newsData);
                        }

                        mAdapter = new MyAdapter(news, Login_Result_Activity.this);
                        mRecycleview.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            Log.e("NEWS_ERROR", error.toString());
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String, String> params = new HashMap<>();
                params.put("X-Naver-Client-Id", "RWxC6IbaAPsnZ0Q1keo2");
                params.put("X-Naver-Client-Secret", "9eZxUmQ1mO");
                return params;
            }
        };

        queue.add(stringRequest);
    }
}