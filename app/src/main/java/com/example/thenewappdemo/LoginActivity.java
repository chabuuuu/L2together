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

public class LoginActivity extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt;
    TextView notHaveAccountTv;
    Button mLoginBtn;

    private FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đăng nhập");
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordlEt);
        notHaveAccountTv = findViewById(R.id.nothave_accountTv);
        mLoginBtn = findViewById(R.id.loginBtn);


        //click nút login
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String email = mEmailEt.getText().toString();
            String password = mPasswordEt.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                mEmailEt.setError("Email không hợp lệ");
                mEmailEt.setFocusable(true);
            }
            else{
                loginUser(email, password);
            }
            }
        });

        //click nút k có tài khoản

        notHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Đang đăng nhập chờ tí nhé");

    }

    // hàm đăng nhập

    private void loginUser(String email, String password) {
        pd.show();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();

                            //đăng nhập thành công

                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}