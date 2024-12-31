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
import com.example.myapplication1.models.Building;
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
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private final List<Building> buildings = Arrays.asList(
            new Building("카이마루", new LatLng(36.3739, 127.3592), "별리달리, 더큰식탁, 리틀하노이, 오니기리와 이규동, 웰차이, 캠토토스트, 중앙급식", R.drawable.kaimaru),
            new Building("장영신 학생회관", new LatLng(36.3733, 127.3605), "퀴즈노스", R.drawable.jangyungsin),
            new Building("서측식당", new LatLng(36.3669, 127.3605), "서맛골, 대덕동네 피자, BHC", R.drawable.west_dining),
            new Building("태울관", new LatLng(36.373, 127.36), "제순식당, 역전우동, 인생설렁탕", R.drawable.taeul),
            new Building("정문술 빌딩", new LatLng(36.3712, 127.3623), "서브웨이", R.drawable.jungmun_building),
            new Building("매점 건물", new LatLng(36.3741, 127.3598), "풀빛마루, 매점", R.drawable.maejum),
            new Building("교직원회관", new LatLng(36.3694, 127.3634), "동맛골, 패컬티 클럽", R.drawable.professor_castle),
            new Building("세종관", new LatLng(36.3711, 127.367), "매점", R.drawable.sejong),
            new Building("희망/다솜관", new LatLng(36.3683, 127.3569), "매점", R.drawable.hope_dasom),
            new Building("나들/여울관", new LatLng(36.3671, 127.3572), "매점", R.drawable.nadle_yuul),
            new Building("미르/나래관", new LatLng(36.3703, 127.3558), "매점", R.drawable.mir_narae)
    );

    private final List<Marker> markerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

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

        for (Building building : buildings) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(building.getLocation())
                    .title(building.getName())
                    .snippet(building.getDetails()));
            markerList.add(marker);
        }

        mMap.setOnInfoWindowClickListener(marker ->
                Toast.makeText(requireContext(), marker.getTitle() + " 클릭됨", Toast.LENGTH_SHORT).show()
        );

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
        for (Building building : buildings) {
            Location buildingLocation = new Location("");
            buildingLocation.setLatitude(building.getLocation().latitude);
            buildingLocation.setLongitude(building.getLocation().longitude);

            float distance = currentLocation.distanceTo(buildingLocation);
            building.setDistance(distance);

            for (Marker marker : markerList) {
                if (marker.getTitle().equals(building.getName())) {
                    marker.setSnippet(building.getUpdatedDetails());
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
            imageView.setImageResource(imageResId != 0 ? imageResId : R.drawable.default_image);
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
        for (Building building : buildings) {
            if (building.getName().equals(placeName)) {
                return building.getImageResource();
            }
        }
        return R.drawable.default_image;
    }
}
