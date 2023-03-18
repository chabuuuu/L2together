package com.example.thenewappdemo.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thenewappdemo.R;
import com.example.thenewappdemo.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//view holder class

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;
    FirebaseUser fUser;





    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i==MSG_TYPE_RIGHT){
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right,viewGroup,false);
        return new MyHolder(view);
        }else {

            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left,viewGroup,false);
            return new MyHolder(view);

        }




    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        String message = chatList.get(i).getMessage();
        String timeStamp = chatList.get(i).getTimestamp();

//convert lại calendar
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

        //set data

        myHolder.messageTv.setText(message);
//        System.out.println(message);
        myHolder.timeTv.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).into(myHolder.profileIv);

        }catch (Exception e){

        }

        if (i==chatList.size() - 1 ){
            if (chatList.get(i).isSeen()){
                myHolder.isSeenTv.setText("Đã xem");
            }else {
                myHolder.isSeenTv.setText("Đã gửi");
            }

            System.out.println(chatList.get(i).isSeen());



        }else{
            myHolder.isSeenTv.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // get user đang đăng nhập này
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{
        //views
        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }

}
