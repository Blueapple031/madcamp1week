package com.example.myapplication1;

import android.animation.ObjectAnimator;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication1.adapters.ContactAdapter;
import com.example.myapplication1.models.Contact;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactsFragment extends Fragment {

    private ContactAdapter contactAdapter;
    private List<Contact> originalContactList;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contacts, container, false);

        // JSON 파일에서 연락처 읽기
        originalContactList = readContactsFromJson();
        List<Contact> contactList = new ArrayList<>(originalContactList);

        // RecyclerView 초기화
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactAdapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(contactAdapter);

        //검색기능
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().toLowerCase();
            List<Contact> filteredList = new ArrayList<>();
            for (Contact contact : originalContactList) {
                if (contact.getName().toLowerCase().contains(query) ||
                        contact.getPhone().toLowerCase().contains(query)) {
                    filteredList.add(contact);
                }
            }
            contactAdapter.updateList(filteredList);
        });

        //정렬기능
        Button sortButton = view.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(v -> {
            Collections.sort(contactList, (c1, c2) -> c1.getPhone().compareTo(c2.getPhone()));
            contactAdapter.updateList(contactList);
        });

        return view;
    }

    private List<Contact> readContactsFromJson() {
        List<Contact> contactList = new ArrayList<>();
        AssetManager assetManager= getContext().getAssets();
        try {
            InputStream inputStream = assetManager.open("menus.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("menu");
                String phone = jsonObject.getString("restaurant");
                JSONArray scores = jsonObject.getJSONArray("scores");
                String cost = scores.getString(0) + "00";

                contactList.add(new Contact(name, cost, phone));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactList;
    }
}
