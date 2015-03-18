package com.al.whippersnapper.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
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
import com.al.whippersnapper.activities.ChatActivity;
import com.al.whippersnapper.models.ChatMessage;

public class ChatListAdapter extends ArrayAdapter<ChatMessage> {
    private String thisUsername;
    private byte[] thisUserPhoto;
    private byte[] otherUserPhoto;

    public ChatListAdapter(Context context, String thisUsername, List<ChatMessage> messages, byte[] thisUserPhoto, byte[] otherUserPhoto) {
        super(context, 0, messages);
        this.thisUsername = thisUsername;
        this.thisUserPhoto = thisUserPhoto;
        this.otherUserPhoto = otherUserPhoto;
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

        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (message.getUser() != null) {
            if (message.getUser().equals("")) { // TODO for some reason, after I added the final task address message, sometimes this value would be null.
                // system chat messages have a blank username, don't show either photo
                holder.imageRight.setVisibility(View.GONE);
                holder.imageLeft.setVisibility(View.GONE);
                holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            } else if (message.getUser().equals(thisUsername)) {
                // message came from this user
                holder.imageRight.setVisibility(View.VISIBLE);
                holder.imageLeft.setVisibility(View.GONE);
                holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                //if (thisUserPhoto != null)
                    holder.imageRight.setImageBitmap(BitmapFactory.decodeByteArray(thisUserPhoto, 0, thisUserPhoto.length));
            } else {
                // message came from other user
                holder.imageLeft.setVisibility(View.VISIBLE);
                holder.imageRight.setVisibility(View.GONE);
                holder.text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                //if (otherUserPhoto != null)
                    holder.imageLeft.setImageBitmap(BitmapFactory.decodeByteArray(otherUserPhoto, 0, otherUserPhoto.length));
            }

            holder.text.setText(message.getMessage());
        } else {
            Log.e("XXXXXXXXX", "message was null in ChatListAdapter.java");
        }
        return convertView;
    }


    final class ViewHolder {
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView text;
    }

}