package com.example.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HaksikFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_haksik, container, false);

        ImageButton startButton = view.findViewById(R.id.startquestion);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionFragment questionFragment = new QuestionFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,   // 뒤로 가기 시 새 Fragment가 나타날 때
                        R.anim.slide_out_right  // 뒤로 가기 시 기존 Fragment가 사라질 때
                );
                transaction.replace(R.id.fragment_container, questionFragment);
                transaction.commit();
            }
        });
        return view;
    }
}
