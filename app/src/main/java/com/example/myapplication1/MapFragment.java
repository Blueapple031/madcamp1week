package com.example.myapplication1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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

    private final List<Marker> markerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

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

        mMap.setOnInfoWindowClickListener(marker -> {
            Toast.makeText(requireContext(), marker.getTitle() + " 클릭됨", Toast.LENGTH_SHORT).show();
        });

        showCurrentLocation();
    }

    private void showCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                calculateDistances(location);
            } else {
                Toast.makeText(requireContext(), "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateDistances(Location currentLocation) {
        for (Map.Entry<String, LatLng> entry : places.entrySet()) {
            Location placeLocation = new Location("");
            placeLocation.setLatitude(entry.getValue().latitude);
            placeLocation.setLongitude(entry.getValue().longitude);

            float distance = currentLocation.distanceTo(placeLocation);

            for (Marker marker : markerList) {
                if (marker.getTitle().equals(entry.getKey())) {
                    String updatedSnippet = placeDetails.get(entry.getKey()) + String.format("\n거리: %.2f m", distance);
                    marker.setSnippet(updatedSnippet);
                    marker.showInfoWindow();
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;

        public CustomInfoWindowAdapter() {
            mWindow = LayoutInflater.from(requireContext()).inflate(R.layout.custom_info_window, null);
        }

        private void renderWindowText(Marker marker, View view) {
            ImageView imageView = view.findViewById(R.id.info_window_image);
            TextView title = view.findViewById(R.id.info_window_title);
            TextView snippet = view.findViewById(R.id.info_window_snippet);

            title.setText(marker.getTitle());
            snippet.setText(marker.getSnippet());

            String placeName = marker.getTitle();
            int imageResId = getImageResource(placeName);
            if (imageResId != 0) {
                imageView.setImageResource(imageResId);
            } else {
                imageView.setImageResource(R.drawable.default_image);
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
            case "매점 건물":
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
                return 0;
        }
    }
}
