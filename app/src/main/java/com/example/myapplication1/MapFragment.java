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
import com.example.myapplication1.repository.BuildingRepository;
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
    private final List<Marker> markerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        showCurrentLocation();
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

        List<Building> buildings = BuildingRepository.getInstance().getBuildings();
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
        // BuildingRepository를 통해 모든 건물의 거리 업데이트
        BuildingRepository.getInstance().updateDistances(currentLocation);

        // 마커에 거리 정보 반영
        List<Building> buildings = BuildingRepository.getInstance().getBuildings();
        for (Building building : buildings) {
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

            Building building = BuildingRepository.getInstance().getBuildingByName(marker.getTitle());
            if (building != null) {
                imageView.setImageResource(building.getImageResource());
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
}

