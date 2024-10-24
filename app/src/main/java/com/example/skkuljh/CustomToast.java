package com.example.skkuljh;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

    public static void showToast(Context context, String message, int iconResId,int durationInMilliseconds) {
        // 커스텀 토스트 뷰를 인플레이트합니다.
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        // 텍스트 뷰와 이미지 뷰를 가져옵니다.
        TextView text = layout.findViewById(R.id.toast_text);

        // 메시지와 아이콘 설정
        text.setText(message);

        // 토스트 생성
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        final int toastDuration = durationInMilliseconds;

        // 핸들러를 이용하여 커스텀 시간을 설정
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0);

        // 지정된 시간 동안 Toast를 계속 보여줍니다.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel(); // 시간 끝나면 토스트를 취소
            }
        }, toastDuration);
    }
}