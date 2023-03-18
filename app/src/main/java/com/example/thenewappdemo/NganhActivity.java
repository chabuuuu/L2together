package com.example.thenewappdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NganhActivity extends AppCompatActivity {

    String[] items = {"Khoa học máy tính","Kỹ thuật phần mềm", "Trí tuệ nhân tạo",
            "Công nghệ thông tin", "Kỹ thuật máy tính", "An toàn thông tin", "Hệ thống thông tin",
            "Kỹ thuật cơ điện tử", "Kỹ thuật điện", "Kỹ thuật ô tô",
            "Logistics và Quản lý chuỗi cung ứng",
            "Kế toán", "Tài chính ngân hàng", "Kiểm toán",
            "Kiến trúc", "Thiết kế đồ họa", "Toán tin", "Y khoa", "Y học cổ truyền", "Dược học","Chưa là sinh viên"

    };

    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    Button mRegisterNganh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nganh);

        mRegisterNganh = findViewById(R.id.register_nganh);


        //Khai báo database
        FirebaseAuth firebaseAuth;
        FirebaseUser user;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;


        autoCompleteTxt = findViewById(R.id.auto_complete_txt);

        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,items);
        autoCompleteTxt.setAdapter(adapterItems);


        //Gán giá trị database
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                HashMap<String,Object> result = new HashMap<>();
                //đưa key và giả trị của nó vào hash map (vd: key là name, value là giá trị của nó)

                result.put("phone", item);

                if (item == "Chưa là sinh viên"){
                    result.put("phone", "");

                    databaseReference.child(user.getUid()).updateChildren(result);
                    Toast.makeText(getApplicationContext(), "Từ giờ bạn có thể khám phá mọi người ở mọi ngành khác nhau", Toast.LENGTH_SHORT).show();
                }else{
                    databaseReference.child(user.getUid()).updateChildren(result);

                    Toast.makeText(getApplicationContext(), "Đã chọn ngành học: "+item, Toast.LENGTH_SHORT).show();
                }



            }
        });


        mRegisterNganh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NganhActivity.this, DashboardActivity.class));
            }
        });

    }
}