package com.example.myapplication1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication1.adapters.ContactAdapter;
import com.example.myapplication1.models.Contact;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // JSON 파일에서 연락처 읽기
        List<Contact> contactList = readContactsFromJson();

        // RecyclerView 초기화
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ContactAdapter(contactList));
    }

    private List<Contact> readContactsFromJson() {
        List<Contact> contactList = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("contacts.json");
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