package com.example.myapplication1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication1.adapters.ImageAdapter;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Grid 형식 레이아웃 매니저 설정
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // 이미지 배열
        int[] imageIds = {
                R.drawable.image1,
                R.drawable.image2,
                R.drawable.image3,
                R.drawable.image4,
                R.drawable.image5,
                R.drawable.image6,
                R.drawable.image7,
                R.drawable.image8,
                R.drawable.image9,
                R.drawable.image10,
                R.drawable.image11,
        };

        // Glide를 사용하는 어댑터 연결
        ImageAdapter adapter = new ImageAdapter(this, imageIds);
        recyclerView.setAdapter(adapter);
    }
}
