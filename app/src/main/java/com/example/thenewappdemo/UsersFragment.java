package com.example.thenewappdemo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.thenewappdemo.adapters.AdapterUsers;
import com.example.thenewappdemo.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {
    private Button startChatGpt;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;

    FirebaseAuth firebaseAuth;



    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment



        View view = inflater.inflate(R.layout.fragment_users, container, false);


        firebaseAuth = FirebaseAuth.getInstance();



        recyclerView = view.findViewById(R.id.users_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));




//        mStartChatgpt= view.findViewById(R.id.start_chatgptBtn);

        //init user list

        userList = new ArrayList<>();
//         getAllUsers();


        startChatGpt = view.findViewById(R.id.start_chatgptBtn);
        startChatGpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChatGptActivity.class));
            }
        });


        return view;









    }





//    private void getAllUsers() {
//        // Lấy user hiện giờ
//
//        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//        // Lấy đường dẫn Users
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//
//        //LẤY HẾT DỮ LIỆU TỪ PATH "USERS"
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                    ModelUser modelUser = ds.getValue(ModelUser.class);
//
//                    // Lấy mọi dữ liệu từ Users trừ user đang đăng nhập này nè
//
//                    if(!modelUser.getUid().equals(fUser.getUid())){
//                        userList.add(modelUser);
//                    }
//                    //adapter
//
//                    adapterUsers = new AdapterUsers(getActivity(), userList);
//
//                    //set adapter to recycle view
//
//                    recyclerView.setAdapter(adapterUsers);
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//    }

    private void searchUsers(String query) {

        System.out.println(query);



        // Lấy user hiện giờ

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        // Lấy đường dẫn Users
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        query = fUser.getEmail();


        //LẤY HẾT DỮ LIỆU TỪ PATH "USERS"
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    // Lấy mọi dữ liệu serach từ Users trừ user đang đăng nhập này nè

                    if(!modelUser.getUid().equals(fUser.getUid())){

                        //tìm kiếm theo query đưa vào

//                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
//                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase()) ){
                        if (modelUser.getPhone().toLowerCase().contains(query)){
                            userList.add(modelUser);
                            System.out.println(query);


                        }


//                        userList.add(modelUser);
                    }
                    //adapter

                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();

                    //set adapter to recycle view

                    recyclerView.setAdapter(adapterUsers);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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




        FirebaseAuth firebaseAuth;
        FirebaseUser user;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;



        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();




        String uid = user.getUid(); //ok


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");


//        String phone = ""+ databaseReference.child(uid).child("phone").get();
//        String phone ="software engineer";
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // kiểm tra đến khi gặp đúng thông tin của user

                for (DataSnapshot ds : snapshot.getChildren()){
                    //LẤY DATA
                    String phone = ""+ ds.child("phone").getValue();
                    searchUsers(phone.toLowerCase());




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








        

//        String query;
//        query = FbUser.getPhoneNumber();
//        searchUsers(query.toLowerCase());

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }





    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        //search view

        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

//        Search listener

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                // Thực hiện khi user nhấn nút search
//
//                //query ko rỗng thì search:
//                if (!TextUtils.isEmpty(s.trim())){
//
//                    searchUsers(s);
//
//                }else{
//                    //Không tìm thấy query, search tất cả user
//                    getAllUsers();
//
//
//
//                }
//
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                if (!TextUtils.isEmpty(s.trim())){
//
//                    searchUsers(s);
//
//                }else{
//                    //Không tìm thấy query, search tất cả user
//                    getAllUsers();
//
//
//
//                }
//
//                return false;
//            }
//        });

        // Lấy user hiện giờ

//        getAllUsers();
//
//        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//        // Lấy đường dẫn Users
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//
//        String query;
//        query = fUser.getEmail();
//        searchUsers(query.toLowerCase());




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

//    @Override
//    public void onStart() {
//        //check khi app startup
//        getAllUsers();
//
//        super.onStart();
//    }
}