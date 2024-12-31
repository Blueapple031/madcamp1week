package com.example.myapplication1.repository;

import android.location.Location;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication1.ResultFragment;
import com.example.myapplication1.models.Building;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class BuildingRepository {
    private static BuildingRepository instance;
    private final List<Building> buildings;

    private BuildingRepository() {
        buildings = new ArrayList<>();
        initializeBuildings();
    }

    public static BuildingRepository getInstance() {
        if (instance == null) {
            instance = new BuildingRepository();
        }
        return instance;
    }

    private void initializeBuildings() {
        buildings.add(new Building("카이마루", new LatLng(36.3739, 127.3592), "별리달리, 더큰식탁, 리틀하노이, 오니기리와 이규동, 웰차이, 캠토토스트, 중앙급식", R.drawable.kaimaru));
        buildings.add(new Building("장영신학생회관", new LatLng(36.3733, 127.3605), "퀴즈노스", R.drawable.jangyungsin));
        buildings.add(new Building("서측식당", new LatLng(36.3669, 127.3605), "서맛골, 대덕동네 피자, BHC", R.drawable.west_dining));
        buildings.add(new Building("태울관", new LatLng(36.373, 127.36), "제순식당, 역전우동, 인생설렁탕", R.drawable.taeul));
        buildings.add(new Building("정문술빌딩", new LatLng(36.3712, 127.3623), "서브웨이", R.drawable.jungmun_building));
        buildings.add(new Building("매점건물", new LatLng(36.3741, 127.3598), "풀빛마루, 매점", R.drawable.maejum));
        buildings.add(new Building("교직원회관", new LatLng(36.3694, 127.3634), "동맛골, 패컬티 클럽", R.drawable.professor_castle));
        buildings.add(new Building("세종관", new LatLng(36.3711, 127.367), "매점", R.drawable.sejong));
        buildings.add(new Building("희망/다솜관", new LatLng(36.3683, 127.3569), "매점", R.drawable.hope_dasom));
        buildings.add(new Building("나들/여울관", new LatLng(36.3671, 127.3572), "매점", R.drawable.nadle_yuul));
        buildings.add(new Building("미르/나래관", new LatLng(36.3703, 127.3558), "매점", R.drawable.mir_narae));
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(buildings); // 데이터 보호를 위해 복사본 반환
    }

    public Building getBuildingByName(String name) {
        for (Building building : buildings) {
            if (building.getName().equals(name)) {
                return building;
            }
        }
        return null; // 해당 이름의 건물이 없을 경우 null 반환
    }
    public void updateDistances(Location currentLocation) {
        for (Building building : buildings) {
            Location buildingLocation = new Location("");
            buildingLocation.setLatitude(building.getLocation().latitude);
            buildingLocation.setLongitude(building.getLocation().longitude);

            float distance = currentLocation.distanceTo(buildingLocation);
            building.setDistance(distance); // 각 건물의 거리 업데이트
        }
    }
    public double getDistanceToBuilding(String buildingName) {
        Building building = getBuildingByName(buildingName);
        if (building != null) {
            return building.getDistance();
        }
        return Double.MAX_VALUE; // 건물이 없으면 큰 값 반환
    }
}
