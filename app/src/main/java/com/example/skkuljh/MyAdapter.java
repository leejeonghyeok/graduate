package com.example.skkuljh;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<NewsData> mDataSet;
    private Context context;  // Context를 저장합니다.

    // ViewHolder 클래스
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Textview_title;
        public TextView Textview_content;

        public MyViewHolder(View v) {
            super(v);
            Textview_title = v.findViewById(R.id.TextView_title);
            Textview_content = v.findViewById(R.id.TextView_content);
        }
    }

    // 생성자: 데이터와 Context 초기화
    public MyAdapter(List<NewsData> myDataSet, Context context) {
        this.mDataSet = myDataSet;
        this.context = context;
        Fresco.initialize(context);  // Fresco 초기화
    }

    // 각 항목의 레이아웃을 인플레이트
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_news, parent, false);  // row_news 레이아웃 인플레이트
        return new MyViewHolder(v);
    }

    // 데이터와 View를 바인딩
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        NewsData news = mDataSet.get(position);

        String title = news.getTitle()
                .replace("<b>", "")
                .replace("</b>", "")
                .replace("&nbsp;", " ")
                .replace("&quot;", "\"")  // &quot; -> "
                .replace("&amp;", "&")    // &amp; -> &
                .replace("&lt;", "<")     // &lt; -> <
                .replace("&gt;", ">")     // &gt; -> >
                .replace("<br>", "")
                .replace("</br>", "");

        String description = news.getDespcription()
                .replace("<b>", "")
                .replace("</b>", "")
                .replace("&nbsp;", " ")
                .replace("&quot;", "\"")  // &quot; -> "
                .replace("&amp;", "&")    // &amp; -> &
                .replace("&lt;", "<")     // &lt; -> <
                .replace("&gt;", ">")     // &gt; -> >
                .replace("<br>", "")
                .replace("</br>", "");
        viewHolder.Textview_title.setText(title);
        viewHolder.Textview_content.setText(description);


        // **기사 항목 클릭 시 브라우저로 이동**
        viewHolder.itemView.setOnClickListener(v -> {
            String articleUrl = news.getUrl();  // NewsData 객체에 저장된 URL 사용
            if (articleUrl != null && !articleUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}