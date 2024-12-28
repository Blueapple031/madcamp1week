package com.example.myapplication1;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
public class FrameActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        Button btnHome = findViewById(R.id.home_03); //button을 정의하는 코드
        Button btnMenu = findViewById(R.id.menu_03);
        Button btnnubzukki = findViewById(R.id.nubzukki);
        Button btnContacts = findViewById(R.id.contacts);
        Button btnGallery = findViewById(R.id.gallery);

        // Home 버튼 클릭 시 HomeActivity로 이동
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrameActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrameActivity.this, HomeActivity.class); //Menu 만들면서 넣어야 함!!
                startActivity(intent);
            }
        });
        btnnubzukki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrameActivity.this, QuestionActivity.class); //첫 페이지 만들면서 넣어야 함!!!
                startActivity(intent);
            }
        });
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrameActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrameActivity.this, HomeActivity.class); //Gallery 합치면서 넣어야 함!!!
                startActivity(intent);
            }
        });
    }
}
