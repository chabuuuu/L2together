package com.example.thenewappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    //view
    EditText mEmailEt, mPasswordEt;
    Button mRegisterBtn;
    TextView mHaveAccountTv;
    ProgressDialog progressDialog;


    //fire base

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tạo tài khoản");
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        //init
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordlEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký tài khoản, bạn chờ chút nhé...");

        //handle register click

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //bao loi vi khong giong dinh dang email
                    mEmailEt.setError("Email không hợp lệ");
                    mEmailEt.setFocusable(true);
                }
                else if(password.length() < 6){
                    //Bao loi do dai password
                    mPasswordEt.setError("Mật khẩu cần có ít nhất 6 kí tự");
                    mPasswordEt.setFocusable(true);
                }
                else {
                    registerUser (email, password);
                }
            }
        });


        //handle cái tôi đã có tài khoản

        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void registerUser(String email, String password) {
        // email va mat khau da hop le, bay gio se dang ki tai khoan
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();


                            //Lấy email và uid gửi lên database

                            String email = user.getEmail();
                            String uid = user.getUid();

                            //tạo hashmap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //đưa dữ liệu vào hash map
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", "");
                            hashMap.put("phone", "");
                            hashMap.put("image", "");


                            //database get instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
//                             path for user to database name users
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);



                            Toast.makeText(RegisterActivity.this, "Đăng ký hoàn tất cho \n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, NganhActivity.class));
                            finish();


                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Xác thực thất bại",Toast.LENGTH_SHORT).show();
                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
// quay lai hanh dong truoc do

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}