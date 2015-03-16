package com.al.whippersnapper.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import com.al.whippersnapper.R;
import com.al.whippersnapper.models.ChatMessage;

public class ChatListAdapter extends ArrayAdapter<ChatMessage> {
    private String thisUsername;

    public ChatListAdapter(Context context, String thisUsername, List<ChatMessage> messages) {
        super(context, 0, messages);
        this.thisUsername = thisUsername;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.item_chat, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.imageLeft = (ImageView)convertView.findViewById(R.id.ivOtherUserChatPhoto);
            holder.imageRight = (ImageView)convertView.findViewById(R.id.ivThisUserChatPhoto);
            holder.text = (TextView)convertView.findViewById(R.id.tvChatText);
            convertView.setTag(holder);
        }
        final ChatMessage message = (ChatMessage)getItem(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag();


        final boolean isMe = message.getUser().equals(thisUsername);
        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (isMe) {
            holder.imageRight.setVisibility(View.VISIBLE);
            holder.imageLeft.setVisibility(View.GONE);
            holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        } else {
            holder.imageLeft.setVisibility(View.VISIBLE);
            holder.imageRight.setVisibility(View.GONE);
            holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }
        final ImageView profileView = isMe ? holder.imageRight : holder.imageLeft;
        //Picasso.with(getContext()).load("http://i.imgur.com/mRsY9dN.png").into(profileView); // TODO - populate with user's photo
        holder.text.setText(message.getMessage());
        return convertView;
    }


    final class ViewHolder {
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView text;
    }

}