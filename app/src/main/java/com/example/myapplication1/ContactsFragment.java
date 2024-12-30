package com.example.myapplication1;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication1.adapters.ContactAdapter;
import com.example.myapplication1.models.Contact;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contacts, container, false);

        // JSON 파일에서 연락처 읽기
        List<Contact> contactList = readContactsFromJson();

        // RecyclerView 초기화
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new ContactAdapter(contactList));

        return view;
    }



    private List<Contact> readContactsFromJson() {
        List<Contact> contactList = new ArrayList<>();
        AssetManager assetManager= getContext().getAssets();
        try {
            InputStream inputStream = assetManager.open("contacts.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String phone = jsonObject.getString("phone");
                contactList.add(new Contact(name, phone));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactList;
    }
}
