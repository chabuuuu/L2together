package com.example.thenewappdemo;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


//this class is to take the reminders from the user and inserts into the database
public class ReminderActivity extends AppCompatActivity {

    //FIREBASE
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    String hisUid;
    String myUid;
    String myname;
    String mname;

    String hisImage;



    Button mSubmitbtn, mDatebtn, mTimebtn;
    EditText mTitledit;
    String timeTonotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        mTitledit = (EditText) findViewById(R.id.editTitle);
        mDatebtn = (Button) findViewById(R.id.btnDate);                                             //assigned all the material reference to get and set data
        mTimebtn = (Button) findViewById(R.id.btnTime);
        mSubmitbtn = (Button) findViewById(R.id.btnSubmit);

        //fIREBASE


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //Nhận dữ liệu hisUid
        Intent mhisUid = getIntent();
        hisUid = mhisUid.getStringExtra("hisUid");
//        myname = mhisUid.getStringExtra("name");
        myUid = user.getUid();







        mTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime();                                                                       //when we click on the choose time button it calls the select time method
            }
        });

        mDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }                                        //when we click on the choose date button it calls the select date method
        });

        mSubmitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitledit.getText().toString().trim();                               //access the data from the input field
                String date = mDatebtn.getText().toString().trim();                                 //access the date from the choose date button
                String time = mTimebtn.getText().toString().trim();                                 //access the time from the choose time button

                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter text", Toast.LENGTH_SHORT).show();   //shows the toast if input field is empty
                } else {
                    if (time.equals("time") || date.equals("date")) {                                               //shows toast if date and time are not selected
                        Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                    } else {

                        //Tạo node Chats

                        // Tin nhắn sẽ chứa trong nhánh con message của node chats
                        //Uid sẽ chứa trong sender và receiver
                        myUid = user.getUid();

                        Query mynameQuery = usersDbRef.orderByChild("uid").equalTo(myUid);
                        mynameQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //chech đến khi tìm duoc info
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    //lấy data
                                    String name = ""+ds.child("name").getValue();


                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    String timestamp = String.valueOf(System.currentTimeMillis());
                                    String send_to_hisuid = "Bạn có lịch hẹn học chung từ "+name+". Lời nhắn: "+title;
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("sender", myUid);
                                    hashMap.put("receiver",hisUid);
                                    hashMap.put("title", send_to_hisuid);
                                    hashMap.put("date",date);
                                    hashMap.put("time", time);
                                    hashMap.put("timeTonotify", timeTonotify);
//                        hashMap.put("name", myname);
                                    hashMap.put("list_upload", false);

//                                    databaseReference.child("Bookings").push().setValue(hashMap);
                                    DatabaseReference reference = firebaseDatabase.getReference("Bookings");

                                    reference.child(hisUid).setValue(hashMap);
//                                    mname = name;

                                    System.out.println(send_to_hisuid);

                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }

                        });









                        processinsert(title, date, time);
                        System.out.println(title+" "+date+" "+time);

                    }
                }


            }
        });
    }


    private void processinsert(String title, String date, String time) {
        String result = new dbManager(this).addreminder(title, date, time);                  //inserts the title,date,time into sql lite database
        setAlarm(title, date, time);                                                                //calls the set alarm method to set alarm
        mTitledit.setText("");
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    }

    private void selectTime() {                                                                     //this method performs the time picker task
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeTonotify = i + ":" + i1;
                System.out.println(timeTonotify);
                //temp variable to store the time to set alarm
                mTimebtn.setText(FormatTime(i, i1));                                                //sets the button text as selected time
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate() {                                                                     //this method performs the date picker task
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mDatebtn.setText(day + "-" + (month + 1) + "-" + year);                             //sets the selected date as test for button
            }
        }, year, month, day);
        datePickerDialog.show();
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

        Intent intentBack = new Intent(getApplicationContext(), BookingActivity.class);                //this intent will be called once the setting alarm is complete
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);                                                                  //navigates from adding reminder activity to mainactivity

    }
}