package com.example.thenewappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;





public class DashboardActivity extends AppCompatActivity {
    protected OnBackPressedListener onBackPressedListener;

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    String timeTonotify;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String myUid;

    //view

    TextView mProfileTv;

    //Back listener

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);



        //setbat dau
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //init

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Bookings");
        user = firebaseAuth.getCurrentUser();

//        myUid = user.getUid();

        //tìm ra user theo email để lấy thông tin
        //SỬ DỤNG LẠI ĐỂ LÀM TÍNH NĂNG TÌM NGƯỜI CÙNG NGHÀNH

        checkUserStatus();

        Query query = databaseReference.orderByChild("receiver").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // kiểm tra đến khi gặp đúng thông tin của user



                for (DataSnapshot ds : snapshot.getChildren()){
                    //LẤY DATA

                    String title = ""+ ds.child("title").getValue();
                    String date = ""+ ds.child("date").getValue();
                    String time = ""+ ds.child("time").getValue();
                    timeTonotify = "" +ds.child("timeTonotify").getValue();



                    Boolean list_upload = (Boolean) ds.child("list_upload").getValue();
                    System.out.println("ĐÂY LÀ GIÁ TRỊ: "+list_upload);
                    System.out.println(date + time);
                    if (list_upload != true){
                        System.out.println("Lỗi lệnh này đã chạy");
                        list_upload = true;
                        HashMap<String,Object> result = new HashMap<>();
                        //đưa key và giả trị của nó vào hash map (vd: key là name, value là giá trị của nó)

                        result.put("list_upload", true);


                        //code đưa giá trị vào databse
                        databaseReference.child(user.getUid()).updateChildren(result);

//                        if ((check_date != date) && (check_time != time) && (check_title != title) ){
//                            check_date = date;
//                            check_time = time;
//                            check_title = title;

                        System.out.println(date + time);
                            processinsert(title, date, time);
                        System.out.println("Date và time đã chạy");
                        System.out.println(date + time);
//                        }




//                        startActivity(new Intent(DashboardActivity.this, BookingActivity.class));


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        //
        BottomNavigationView navigationView = findViewById(R.id.navigation);
//        navigationView.setOnItemSelectedListener(selectedListener);

        actionBar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();


        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                        case R.id.navi_home:
                            actionBar.setTitle("Home");
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            break;
//                            return true;

                        case R.id.navi_users:
                            actionBar.setTitle("Users");
                            UsersFragment fragment3 = new UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, fragment3, "");
                            ft3.commit();
                            break;

                    case R.id.navi_chat:
                        actionBar.setTitle("Chat");
                        ChatListFragment fragment4 = new ChatListFragment();
                        FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                        ft4.replace(R.id.content, fragment4, "");
                        ft4.commit();
                        break;


//                            return true;

                        case R.id.navi_profile:
                            actionBar.setTitle("Profile");
                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            break;
                }

                return true;
            }
        }

        );



        //init views

//        mProfileTv = findViewById(R.id.profileTv);

    }


    private void processinsert(String title, String date, String time) {
        String result = new dbManager(this).addreminder(title, date, time);                  //inserts the title,date,time into sql lite database
        setAlarm(title, date, time);                                                                //calls the set alarm method to set alarm
//        mTitledit.setText("");
//        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Một người nào đó đã đặt lịch hẹn với bạn! Vào kiểm tra ngay", Toast.LENGTH_SHORT).show();
    }
    public String FormatTime(int hour, int minute) {                                                //this method converts the time into 12hr format and assigns am or pm

        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }


        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }


        return time;
    }

    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("event", text);                                                       //sending data to alarm class to create channel and notification
        intent.putExtra("time", date);
        intent.putExtra("date", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateandtime = date + " " + timeTonotify;
        System.out.println(timeTonotify);
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Intent intentBack = new Intent(getApplicationContext(), BookingActivity.class);                //this intent will be called once the setting alarm is complete
//        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intentBack);                                                                  //navigates from adding reminder activity to mainactivity

    }




    //loi code

//    private BottomNavigationView.OnItemSelectedListener  selectedListener =
//            new NavigationBarView.OnItemReselectedListener() {
//                @Override
//                public boolean OnItemSelected(@NonNull MenuItem menuItem) {
//                 switch (menuItem.getItemId()){
//                     case R.id.navi_home: {
//                         actionBar.setTitle("Home");
//                         HomeFragment fragment1 = new HomeFragment();
//                         FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//                         ft1.replace(R.id.content, fragment1, "");
//                         ft1.commit();
//
//                         return true;
//                     }
//                     case R.id.navi_users: {
//                         actionBar.setTitle("Users");
//                         UsersFragment fragment3 = new UsersFragment();
//                         FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
//                         ft3.replace(R.id.content, fragment3, "");
//                         ft3.commit();
//
//
//                         return true;
//                     }
//                     case R.id.navi_profile: {
//                         actionBar.setTitle("Profile");
//                         ProfileFragment fragment2 = new ProfileFragment();
//                         FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
//                         ft2.replace(R.id.content, fragment2, "");
//                         ft2.commit();
//
//
//                         return true;
//                     }
//                 }
//                    return false;
//                }
//            };

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            //da dang nhap
            //show email
//            mProfileTv.setText(user.getEmail());
        }
        else {
            //chua dang nhap
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        //check khi app startup
        checkUserStatus();
        super.onStart();
    }








    }




