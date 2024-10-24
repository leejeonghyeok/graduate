package com.example.skkuljh;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Map extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource locationSource;
    private NaverMap mNaverMap;

    private RequestQueue queue;
    private List<HosData> hosDataList;
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ImageView backButton = findViewById(R.id.back_button_map);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        NaverMapOptions naverMapOptions = new NaverMapOptions().mapType(NaverMap.MapType.Basic).enabledLayerGroups(NaverMap.LAYER_GROUP_BUILDING,NaverMap.LAYER_GROUP_TRANSIT).compassEnabled(true).scaleBarEnabled(true).locationButtonEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);

        if(mapFragment == null){
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }
    }
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap=naverMap;

        mNaverMap.setIndoorEnabled(true);
        mNaverMap.setSymbolScale(1);

        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
        mNaverMap.setLocationSource(locationSource);

        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
        mNaverMap.setLocationSource(locationSource);

        // 위치 추적 모드 설정 (권한 요청 후 처리)
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        mNaverMap.setMinZoom(5.0);
        mNaverMap.setMaxZoom(20.0);

        mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        UiSettings uiSettings =mNaverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        markerMaker();
    }

    private void markerMaker(){
        String url = "https://openapi.gg.go.kr/Ggmindmedinst?KEY=5f42e69446204d3d9e832f16af0fb5fb&SIGUN_CD=41110&TYPE=JSON";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON_RESPONSE", "ddd"+response.toString());
                        try {
                            List<HosData> stores = parseJSON(response);

                            addMarkers(stores);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(NaverMapActivity.this, "데이터 파싱 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(NaverMapActivity.this, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
            }
        });

        // 요청을 큐에 추가
        queue.add(jsonObjectRequest);
    }

    private List<HosData> parseJSON(JSONObject jsonObject) throws JSONException {
        List<HosData> hosDataList = new ArrayList<>();

        // "Ggmindmedinst" 배열에서 "row" 부분을 가져옴
        JSONArray ggMindmedinstArray = jsonObject.getJSONArray("Ggmindmedinst");
        JSONObject rowObject = ggMindmedinstArray.getJSONObject(1); // row는 두 번째 객체임
        JSONArray rowArray = rowObject.getJSONArray("row");

        for (int i = 0; i < rowArray.length(); i++) {
            JSONObject hosObject = rowArray.getJSONObject(i);

            HosData hosData = new HosData();
            hosData.setSIGUN_CD(hosObject.getString("SIGUN_CD"));
            hosData.setSIGUN_NM(hosObject.getString("SIGUN_NM"));
            hosData.setINST_NM(hosObject.getString("INST_NM"));
            hosData.setTELNO_INFO(hosObject.getString("TELNO_INFO"));
            hosData.setHMPG_URL(hosObject.getString("HMPG_URL"));
            hosData.setREFINE_LOTNO_ADDR(hosObject.getString("REFINE_LOTNO_ADDR"));
            hosData.setREFINE_ROADNM_ADDR(hosObject.getString("REFINE_ROADNM_ADDR"));
            hosData.setREFINE_ZIP_CD(hosObject.getString("REFINE_ZIP_CD"));
            hosData.setREFINE_WGS84_LOGT(hosObject.getDouble("REFINE_WGS84_LOGT"));
            hosData.setREFINE_WGS84_LAT(hosObject.getDouble("REFINE_WGS84_LAT"));

            Log.d("JSON_PARSING", "병원 이름: " + hosData.getINST_NM() + ", 경도: " + hosData.getREFINE_WGS84_LOGT() + ", 위도: " + hosData.getREFINE_WGS84_LAT());

            // HosData 객체를 리스트에 추가
            hosDataList.add(hosData);
        }

        return hosDataList;
    }

    private void addMarkers(List<HosData> hosDataList) {
        for (HosData hosData : hosDataList) {
            // 마커 설정
            Marker marker = new Marker();
            marker.setPosition(new LatLng(hosData.getREFINE_WGS84_LAT(), hosData.getREFINE_WGS84_LOGT()));
            marker.setCaptionText(hosData.getINST_NM());
            marker.setMap(mNaverMap);

            //마커 클릭 리스너 추가
            marker.setOnClickListener(overlay -> {
                // BottomSheet 생성 후 보여줌
                HospitalBottomSheet bottomSheet = new HospitalBottomSheet(hosData);
                bottomSheet.show(getSupportFragmentManager(), "hospitalBottomSheet");
                return true;
            });
        }
    }
}