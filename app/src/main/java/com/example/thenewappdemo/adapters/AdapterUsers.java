package com.example.thenewappdemo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thenewappdemo.ChatActivity;
import com.example.thenewappdemo.R;
import com.example.thenewappdemo.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUser> userList;
    //constructor


    public AdapterUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //inflate layout row_user.xml

        View view = LayoutInflater.from(context).inflate(R.layout.row_users,viewGroup, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        //get data
        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
//        String userEmail = userList.get(i).getEmail();
        final String userEmail = userList.get(i).getEmail(); //new

        String hisUID = userList.get(i).getUid();



        //new
        String userPhone = userList.get(i).getPhone();

        //set data
        myHolder.mNameTv.setText(userName);
        myHolder.mEmailTv.setText(userEmail);

        //new
        myHolder.mPhoneTv.setText(userPhone);
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_default_img).into(myHolder.mAvatarIv);


        }
        catch (Exception e){

        }

        //handle item click

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, ""+userEmail, Toast.LENGTH_SHORT).show();
                //Click vào user để bắt đầu activity chat
                // Dùng Uid để xác định người muốn gửi tin nhắn
                //Gửi uid đến ChatActivity

                Intent  intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUID);
                context.startActivity(intent);



            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv, mPhoneTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init view

            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            //new
            mPhoneTv = itemView.findViewById(R.id.phoneTv);

//            mNameTv.findViewById(R.id.nameTv);
//            mEmailTv.findViewById(R.id.emailTv);
        }
    }
}
