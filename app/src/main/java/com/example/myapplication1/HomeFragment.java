package com.example.myapplication1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        Button btnContacts = view.findViewById(R.id.contactsbutton); //button을 정의하는 코드

        // Contacts 버튼 클릭 시 ContactsActivity로 이동
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FragmentManager를 사용하여 Fragment 전환
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new QuestionFragment()); // `R.id.fragment_container`는 FrameActivity의 컨테이너 ID
                transaction.addToBackStack(null); // 백스택에 추가하여 뒤로 가기 버튼 사용 가능
                transaction.commit();
            }
        });
        return view;
    }
}
