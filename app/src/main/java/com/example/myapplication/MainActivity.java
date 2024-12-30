package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // 장소 데이터
    private final Map<String, LatLng> places = new HashMap<String, LatLng>() {{
        put("카이마루", new LatLng(36.3739, 127.3592));
        put("장영신 학생회관", new LatLng(36.3733, 127.3605));
        put("서측식당", new LatLng(36.3669, 127.3605));
        put("태울관", new LatLng(36.373, 127.36));
        put("정문술 빌딩", new LatLng(36.3712, 127.3623));
        put("매점 건물", new LatLng(36.3741, 127.3598));
        put("교직원회관", new LatLng(36.3694, 127.3634));
        put("세종관", new LatLng(36.3711, 127.367));
        put("희망/다솜관", new LatLng(36.3683, 127.3569));
        put("나들/여울관", new LatLng(36.3671, 127.3572));
        put("미르/나래관", new LatLng(36.3703, 127.3558));
    }};

    // 장소 상세 정보
    private final Map<String, String> placeDetails = new HashMap<String, String>() {{
        put("카이마루", "별리달리, 더큰식탁, 리틀하노이, 오니기리와 이규동, 웰차이, 캠토토스트, 중앙급식");
        put("장영신 학생회관", "퀴즈노스");
        put("서측식당", "서맛골, 대덕동네 피자, BHC");
        put("태울관", "제순식당, 역전우동, 인생설렁탕");
        put("정문술 빌딩", "서브웨이");
        put("매점 건물", "풀빛마루, 매점");
        put("교직원회관", "동맛골, 패컬티 클럽");
        put("세종관", "매점");
        put("희망/다솜관", "매점");
        put("나들/여울관", "매점");
        put("미르/나래관", "매점");
    }};
    //api 초기화
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Google Map 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 위치 클라이언트 초기화
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //이게 반드시 있어야함.ㅇㅇ 진짜 없으면 인생망함
        showCurrentLocation();
    }

    // 지도 안 위치나 마커 정보 초기화
    private final List<Marker> markerList = new ArrayList<>();
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        // 권한 요청 및 현재 위치 가져오기
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 지도에 내 위치 알려주기
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        // 커스텀 정보창 어댑터 설정
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // 장소 마커 추가
        for (Map.Entry<String, LatLng> entry : places.entrySet()) {
            String placeName = entry.getKey();
            LatLng location = entry.getValue();
            String snippet = placeDetails.get(placeName);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(placeName)
                    .snippet(snippet));
            markerList.add(marker);
        }

        // 정보 창 클릭 리스너 추가
        mMap.setOnInfoWindowClickListener(marker -> {
            Toast.makeText(this, marker.getTitle() + " 클릭됨", Toast.LENGTH_SHORT).show();
        });
    }

    private void showCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                // 각 장소와의 거리 계산
                calculateDistances(location);
            } else {
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDistances(Location currentLocation) {
        for (Map.Entry<String, LatLng> entry : places.entrySet()) {
            Location placeLocation = new Location("");
            placeLocation.setLatitude(entry.getValue().latitude);
            placeLocation.setLongitude(entry.getValue().longitude);

            float distance = currentLocation.distanceTo(placeLocation); // 미터 단위 거리 계산

            // 마커 업데이트
            for (Marker marker : markerList) { // 모든 마커를 반복
                if (marker.getTitle().equals(entry.getKey())) { // 타이틀이 일치하는 마커 찾기
                    String updatedSnippet = placeDetails.get(entry.getKey()) + String.format("\n거리: %.2f m", distance);
                    marker.setSnippet(updatedSnippet);
                    marker.showInfoWindow(); // 업데이트 후 정보창 표시
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;

        public CustomInfoWindowAdapter() {
            //layout에 있는 custom_indo_window로 InfoWindowAdapter를 커스텀함.
            mWindow = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_info_window, null);
        }

        private void renderWindowText(Marker marker, View view) {
            ImageView imageView = view.findViewById(R.id.info_window_image);
            TextView title = view.findViewById(R.id.info_window_title);
            TextView snippet = view.findViewById(R.id.info_window_snippet);

            // 제목 및 세부 정보 설정
            title.setText(marker.getTitle());
            snippet.setText(marker.getSnippet());

            // 마커 제목에 따라 이미지 설정
            String placeName = marker.getTitle();
            int imageResId = getImageResource(placeName);
            if (imageResId != 0) {
                imageView.setImageResource(imageResId);
            } else {
                imageView.setImageResource(R.drawable.default_image); // 기본 이미지
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            renderWindowText(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private int getImageResource(String placeName) {
        switch (placeName) {
            case "카이마루":
                return R.drawable.kaimaru;
            case "장영신 학생회관":
                return R.drawable.jangyungsin;
            case "서측식당":
                return R.drawable.west_dining;
            case "태울관":
                return R.drawable.taeul;
            case "정문술 빌딩":
                return R.drawable.jungmun_building;
            case "매점건물":
                return R.drawable.maejum;
            case "교직원회관":
                return R.drawable.professor_castle;
            case "세종관":
                return R.drawable.sejong;
            case "희망/다솜관":
                return R.drawable.hope_dasom;
            case "나들/여울관":
                return R.drawable.nadle_yuul;
            case "미르/나래관":
                return R.drawable.mir_narae;

            default:
                return 0; // 이미지 없음
        }
    }
}
