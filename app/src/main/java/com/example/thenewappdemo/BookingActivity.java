package com.example.thenewappdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.thenewappdemo.adapters.myAdapter;
import com.example.thenewappdemo.models.Model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity {


    FloatingActionButton mCreateRem;
    RecyclerView mRecyclerview;
    ArrayList<Model> dataholder = new ArrayList<Model>();                                               //Array list to add reminders and display in recyclerview
    myAdapter adapter;
    FloatingActionButton booking_backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);




        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mCreateRem = (FloatingActionButton) findViewById(R.id.create_reminder);
//        booking_backBtn.view.findViewById(R.id.booking_back);
        booking_backBtn= (FloatingActionButton) findViewById(R.id.booking_back);

        //Nhận dữ liệu hisUid từ Chat activity
        Intent uid_intent = getIntent();
        String mhisUid = uid_intent.getStringExtra("mhisUid");
//        String mname = uid_intent.getStringExtra("mname");

        //Floating action button to change activity
        mCreateRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
                intent.putExtra("hisUid", mhisUid);
//                intent.putExtra("name", mname);
                startActivity(intent);                                                              //Starts the new activity to add Reminders
            }
        });


        booking_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BookingActivity.this, DashboardActivity.class));
                finish();

            }
        });

        Cursor cursor = new dbManager(getApplicationContext()).readallreminders();                  //Cursor To Load data From the database
        while (cursor.moveToNext()) {
            Model model = new Model(cursor.getString(1), cursor.getString(2), cursor.getString(3));
            dataholder.add(model);
        }

        adapter = new myAdapter(dataholder);
        mRecyclerview.setAdapter(adapter);                                                          //Binds the adapter with recyclerview

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BookingActivity.this, ChatActivity.class));
        finish();                                                                                   //Makes the user to exit from the app
        super.onBackPressed();

    }
}