package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // 장소 데이터
    private final Map<String, LatLng> places = new HashMap<String, LatLng>() {{
        put("카이마루", new LatLng(36.3739, 127.3592));
        put("장영신 학생회관", new LatLng(36.3733, 127.3605));
        put("동측식당", new LatLng(36.3691, 127.3638));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Google Map 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 위치 클라이언트 초기화
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        // 권한 요청 및 현재 위치 가져오기
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        // 장소 마커 추가
        for (Map.Entry<String, LatLng> entry : places.entrySet()) {
            mMap.addMarker(new MarkerOptions().position(entry.getValue()).title(entry.getKey()));
        }
    }

    private void showCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(currentLocation)
                        .title("현재 위치");

                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                // 각 장소와의 거리 계산
                calculateDistances(location);
            } else {
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDistances(Location currentLocation) {
        StringBuilder distancesMessage = new StringBuilder("각 장소와의 거리:\n");

        for (Map.Entry<String, LatLng> entry : places.entrySet()) {
            Location placeLocation = new Location("");
            placeLocation.setLatitude(entry.getValue().latitude);
            placeLocation.setLongitude(entry.getValue().longitude);

            float distance = currentLocation.distanceTo(placeLocation); // 미터 단위 거리 계산

            // 거리 정보를 문자열로 추가
            distancesMessage.append(entry.getKey()).append(" - ").append(String.format("%.2f", distance)).append("m\n");
        }

        // 거리 정보 출력
        Toast.makeText(this, distancesMessage.toString(), Toast.LENGTH_LONG).show();
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
}
