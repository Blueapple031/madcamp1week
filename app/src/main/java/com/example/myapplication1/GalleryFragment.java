package com.example.myapplication1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication1.adapters.GalleryAdapter;
import com.example.myapplication1.models.GalleryItem;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_gallery, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2열 그리드

        List<GalleryItem> galleryItems = new ArrayList<>();
        galleryItems.add(new GalleryItem(R.drawable.kaimaru, "학사식당 카이마루(N11)"));
        galleryItems.add(new GalleryItem(R.drawable.taeul, "태울관(N13)"));
        galleryItems.add(new GalleryItem(R.drawable.maejum, "북측 학생회관(N12)"));
        galleryItems.add(new GalleryItem(R.drawable.jangyungsin, "장영신 학생회관(N13-1)"));
        galleryItems.add(new GalleryItem(R.drawable.hope_dasom, "희망/다솜관(W4-3, 4)"));
        galleryItems.add(new GalleryItem(R.drawable.jungmun_building, "정문술빌딩(E16)"));
        galleryItems.add(new GalleryItem(R.drawable.mir_narae, "미르/나래관(W6)"));
        galleryItems.add(new GalleryItem(R.drawable.nadle_yuul, "나들/여울관(W4-1, 2)"));
        galleryItems.add(new GalleryItem(R.drawable.professor_castle, "교직원회관(E5)"));
        galleryItems.add(new GalleryItem(R.drawable.sejong, "세종관(E8)"));
        galleryItems.add(new GalleryItem(R.drawable.west_dining, "서측 학생회관(W2)"));

        galleryAdapter = new GalleryAdapter(galleryItems);
        recyclerView.setAdapter(galleryAdapter);

        return view;
    }
}
