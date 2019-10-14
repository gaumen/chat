package com.onebond.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {
    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapshotList;
    private ChildEventListener mListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mSnapshotList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref,String name){
        mActivity=activity;
        mDisplayName=name;
        mDatabaseReference=ref.child("messages");
        mDatabaseReference.addChildEventListener(mListener);
        mSnapshotList=new ArrayList<>();
    }

    //INNER CLASS
    static class ViewHolder{
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;

    }

    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public InstanceMessage getItem(int position) {
        DataSnapshot snapshot=mSnapshotList.get(position);
        return snapshot.getValue(InstanceMessage.class); //CONVERTS JSON INTO IM OBJECT
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //GET THE FIELDS OF XML FILE
            convertView=inflater.inflate(R.layout.chat_msg_row,parent,false);   //INIT THEM WITH STYLING OF OTHER XML FILE
            final ViewHolder holder=new ViewHolder();   //STATIC INNER CLASSES CAN ACCESS ENCLOSING CLASS FIELDS VIA ITS OBJECT
            holder.authorName=(TextView)convertView.findViewById(R.id.author);
            holder.body=(TextView)convertView.findViewById(R.id.message);
            holder.params=(LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            convertView.setTag(holder);
        }

        final InstanceMessage message=getItem(position);
        final ViewHolder holder=(ViewHolder)convertView.getTag();

        boolean isMe=message.getAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe,holder);

        String author=message.getAuthor();
        holder.authorName.setText(author);

        String msg=message.getMessage();
        holder.body.setText(msg);

        return convertView;
    }

    private void setChatRowAppearance(boolean isItMe,ViewHolder holder){
        if(isItMe){
            holder.params.gravity=Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }
        else{
            holder.params.gravity=Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }
        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);

    }

    public void cleanUp(){
        mDatabaseReference.removeEventListener(mListener);
    }
}
