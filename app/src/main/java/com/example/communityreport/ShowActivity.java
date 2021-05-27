package com.example.communityreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Model> list;

    private MyAdapter adapter;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show2);

//        btnSignOut.setOnClickListener {
//            firebaseAuth.signOut()
//            startActivity(Intent(this, AdminSignUp::class.java))
//            toast("signed out")
//            finish()
//        }
        final Button btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent myIntent = new Intent(ShowActivity.this, AdminSignUp.class);
                ShowActivity.this.startActivity(myIntent);
                Toast.makeText(getApplicationContext(),"Signed Out",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new MyAdapter(this , list);
        recyclerView.setAdapter(adapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Model model = dataSnapshot.getValue(Model.class);
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}