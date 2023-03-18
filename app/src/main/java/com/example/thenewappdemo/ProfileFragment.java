package com.example.thenewappdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    //khai báo user

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    ImageView avatarIv;
    TextView nameTv, emailTv, phoneTv;
    FloatingActionButton fab;
    ProgressDialog pd;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");


        //init view

        avatarIv = view.findViewById(R.id.avatarIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        phoneTv = view.findViewById(R.id.phoneTv);

        fab = view.findViewById(R.id.fab);
        pd = new ProgressDialog(getActivity());



        //tìm ra user theo email để lấy thông tin
        //SỬ DỤNG LẠI ĐỂ LÀM TÍNH NĂNG TÌM NGƯỜI CÙNG NGHÀNH

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // kiểm tra đến khi gặp đúng thông tin của user

                for (DataSnapshot ds : snapshot.getChildren()){
                    //LẤY DATA

                    String name = ""+ ds.child("name").getValue();
                    String email = ""+ ds.child("email").getValue();
                    String phone = ""+ ds.child("phone").getValue();
                    String image = ""+ ds.child("image").getValue();


                    //Hiện thông tin

                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    try {
                        Picasso.get().load(image).into(avatarIv);

                    }

                    //ảnn avatar mặc định

                    catch (Exception e){
                        Picasso.get().load("https://img.freepik.com/premium-vector/man-avatar-profile-picture-vector-illustration_268834-538.jpg").into(avatarIv);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Nút nổi đc nhất thì:
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                showEditProfileDialog();


            }
        });






        return view;
    }

    private void showEditProfileDialog() {
        String options[] = {"Đổi tên", "Đổi ngành học của bạn"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hãy chọn đi");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //edit name
                    pd.setMessage("Đang đổi tên tài khoản");

                    //gọi hàm edit tên
                    showNamePhoneUpdateDialog("name");


                }
                else if (which == 1){
                    //edit nganh hoc
                    pd.setMessage("Đang đổi ngành học của bạn");

                    // gọi hàm đổi ngành
                    //sẽ update sau
//                    showNamePhoneUpdateDialog("phone");

                    //Chuyển sang màn hình chọn ngành

                    startActivity(new Intent(getActivity(),NganhActivity.class));




                }

            }
        });
        builder.create().show();


    }

    // Tạo khung nhập dữ liệu cần chỉnh sửa

    private void showNamePhoneUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Nhập " + key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //thêm nút cập nhật

        builder.setPositiveButton("Câp nhật", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // input text

                String value = editText.getText().toString().trim();
                //kiểm tra xem  người dùng có nhập gì hay không

                if (!TextUtils.isEmpty(value)){
                    pd.show();

                    //Đưa phần tử nào đó vào trong database
                    //ỨNG DỤNG LẠI CODE NÀY ĐỂ LÀM TÍNH NĂNG CHỌN MENU NGÀNH HỌC

                    HashMap<String,Object> result = new HashMap<>();
                    //đưa key và giả trị của nó vào hash map (vd: key là name, value là giá trị của nó)

                    result.put(key, value);

                    //code đưa giá trị vào databse
                    databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Đã cập nhật tên bạn", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


                }

            }
        });

        //thêm nút cancel

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }

        });
        builder.create().show();





    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            //da dang nhap
            //show email
//            mProfileTv.setText(user.getEmail());
        }
        else {
            //chua dang nhap
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {



        setHasOptionsMenu(true); // to show menu option in fragment

//        startActivity(new Intent(DashboardActivity.this,MainActivity.class));




        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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