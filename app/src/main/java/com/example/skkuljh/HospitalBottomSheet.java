package com.example.skkuljh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.net.URI;

public class HospitalBottomSheet extends BottomSheetDialogFragment {
    private HosData hosData;

    public HospitalBottomSheet(HosData hosData){
        this.hosData = hosData;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hosinfo, container, false);

        TextView hospitalName = view.findViewById(R.id.hos_name);
        TextView hospitalAddress = view.findViewById(R.id.hos_address);
        Button hospitalPhone = view.findViewById(R.id.hos_number);

        TextView hospitalAddress1 = view.findViewById(R.id.hos_address1);
        TextView hospitalAddress2 = view.findViewById(R.id.hos_address2);

        Button hospitalWebsite = view.findViewById(R.id.hos_HMPG);

        hospitalName.setText(hosData.getINST_NM());
        hospitalAddress.setText(hosData.getSIGUN_NM());
        hospitalPhone.setText(hosData.getTELNO_INFO());
        hospitalAddress1.setText(hosData.getREFINE_LOTNO_ADDR());
        hospitalAddress2.setText(hosData.getREFINE_ROADNM_ADDR());
        hospitalWebsite.setText(hosData.getHMPG_URL());

        hospitalPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 전화번호를 URI 형식으로 변환
                String phoneNumber = hosData.getTELNO_INFO();

                // 전화번호의 포맷을 확인하여 필요한 경우 '-'를 제거
                phoneNumber = phoneNumber.replace("-", "");

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));  // "tel:" 스킴을 추가
                startActivity(intent);
            }
        });

        hospitalWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈페이지 URL로 이동
                String url = hosData.getHMPG_URL();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;  // URL 앞에 프로토콜 추가
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        return view;
    }
}
