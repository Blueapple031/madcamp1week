package com.example.myapplication1;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;

public class FrameActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        ImageButton btnHome = findViewById(R.id.home_03); //button을 정의하는 코드
        ImageButton btnMenu = findViewById(R.id.menu_03);
        ImageButton btnnubzukki = findViewById(R.id.nubzukki);
        AnimationDrawable animation = (AnimationDrawable) btnnubzukki.getDrawable();
        animation.start();
        ImageButton btnContacts = findViewById(R.id.contacts);
        ImageButton btnGallery = findViewById(R.id.gallery);

        if (savedInstanceState == null) {
            replaceFragment(new MapFragment());
        }

    //Home 버튼 클릭 시 mapActivity로 이동
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new MapFragment());
            }
        });
        btnMenu.setOnClickListener(view -> showPopupMenu(view));
        btnnubzukki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HaksikFragment haksikFragment = new HaksikFragment();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_frombottom,
                        R.anim.slide_out_toup,
                        R.anim.slide_in_left,
                        R.anim.slide_out_toup
                );
                transaction.replace(R.id.fragment_container, haksikFragment);
                transaction.commit();
            }
        });
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ContactsFragment());
            }
        });
//        btnGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FrameActivity.this, HomeActivity.class); //Gallery 합치면서 넣어야 함!!!
//                startActivity(intent);
//            }
//        });
    }
    // Fragment 전환 함수
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment) // frameContent는 FrameLayout ID
                .addToBackStack(null) // 뒤로 가기 기능 지원
                .commit();
    }

    private void showPopupMenu(View view) {
        // PopupMenu 생성
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

        // 메뉴 클릭 이벤트 처리
//        popupMenu.setOnMenuItemClickListener(menuItem -> {
//            switch (menuItem.getItemId()) {
//                case R.id.action_one:
//                    Toast.makeText(this, "Action 1 clicked", Toast.LENGTH_SHORT).show();
//                    return true;
//                case R.id.action_two:
//                    Toast.makeText(this, "Action 2 clicked", Toast.LENGTH_SHORT).show();
//                    return true;
//                default:
//                    return false;
//            }
//        });

        // 메뉴 표시
        popupMenu.show();
    }
}
