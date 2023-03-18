package com.example.thenewappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thenewappdemo.adapters.AdapterChat;
import com.example.thenewappdemo.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {




    //view from xml


    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;
    Button mbookingBtn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    //check xem người kia đã seen hay chưa

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;



    String hisUid;
    String myUid;

    String hisImage;
    String mname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle("");
    recyclerView = findViewById(R.id.chat_recycleView);
    profileIv = findViewById(R.id.profileIv);
    nameTv = findViewById(R.id.nameTv);
    userStatusTv = findViewById(R.id.userStatusTv);
    messageEt = findViewById(R.id.messageEt);
    sendBtn = findViewById(R.id.sendBtn);
    mbookingBtn = findViewById(R.id.bookingBtn);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        //properties recycle view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


    //Nhận UID từ userlist Adapter Users
        //Ứng dụng để nhận uid làm tính năng đặt lịch
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");





    firebaseAuth = FirebaseAuth.getInstance();
    firebaseDatabase = FirebaseDatabase.getInstance();
    usersDbRef = firebaseDatabase.getReference("Users");




        mbookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mhisUid = new Intent(ChatActivity.this, BookingActivity.class);
                mhisUid.putExtra("mhisUid",hisUid);
//                mhisUid.putExtra("mname", mname);
                startActivity(mhisUid);



//                startActivity(new Intent(ChatActivity.this, BookingActivity.class));
                finish();

            }
        });







        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //chech đến khi tìm duoc info
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //lấy data
                    String name = ""+ds.child("name").getValue();


                    hisImage =""+ds.child("image").getValue();
                    nameTv.setText(name);
                    try {// tìm thấy image, bắn image qua image view
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_default_img).into(profileIv);

                    }
                    catch (Exception e){
                        //khong tim thay thi set default image
                        Picasso.get().load("https://img.freepik.com/premium-vector/man-avatar-profile-picture-vector-illustration_268834-538.jpg").into(profileIv);
                    }

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        //click button để send message
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEt.getText().toString().trim();

                //chech xem tin nhắn có empty hay không
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Nhắn như thế thì làm sao mà gửi đây", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendMessage(message);
                }
            }
        });

        readMessages();
        seenMessages();


    }

    private void seenMessages() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid)&& chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    //adapter
                    adapterChat = new AdapterChat(ChatActivity.this, chatList, hisImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recycle view
                    recyclerView.setAdapter(adapterChat);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {

        //Tạo node Chats

        // Tin nhắn sẽ chứa trong nhánh con message của node chats
        //Uid sẽ chứa trong sender và receiver

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        //reset lại khung nhập tin nhắn sau khi gửi tin
        messageEt.setText("");

    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            myUid = user.getUid();

            //da dang nhap
            //show email
//            mProfileTv.setText(user.getEmail());
        }
        else {
            //chua dang nhap
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //hide search view

        menu.findItem(R.id.action_search).setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id  = item.getItemId();
        if (id ==  R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }


        return super.onOptionsItemSelected(item);
    }
}