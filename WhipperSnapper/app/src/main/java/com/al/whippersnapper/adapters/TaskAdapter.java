package com.al.whippersnapper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.al.whippersnapper.R;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;

import java.util.ArrayList;

/**
 * Created by Al on 3/15/2015.
 */
public class TaskAdapter extends ArrayAdapter<ParseWSUser> {
    public TaskAdapter(Context context, ArrayList<ParseWSUser> users) {
        super(context, 0, users);
    }

    private static class ViewHolder {
        ImageView ivFeatureSeniorPhoto;
        TextView tvFeatureDetails;
        TextView tvFeatureSeniorName;
        TextView tvFeatureTaskType;
        TextView tvFeaturePostedOn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParseWSUser user = getItem(position);

        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task_summary, parent, false);
            vh.ivFeatureSeniorPhoto = (ImageView) convertView.findViewById(R.id.ivFeatureSeniorPhoto);
            vh.tvFeatureDetails = (TextView) convertView.findViewById(R.id.tvFeatureDetails);
            vh.tvFeatureSeniorName = (TextView) convertView.findViewById(R.id.tvFeatureSeniorName);
            vh.tvFeatureTaskType = (TextView) convertView.findViewById(R.id.tvFeatureTaskType);
            vh.tvFeaturePostedOn = (TextView) convertView.findViewById(R.id.tvFeaturePostedOn);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        //vh.ivFeatureSeniorPhoto.setImageBitmap(); // TODO - handle photos
        vh.tvFeatureDetails.setText(user.getTaskDetails());
        vh.tvFeatureTaskType.setText(user.getTaskType());
        vh.tvFeatureSeniorName.setText(Util.getAnonymizedName(user.getFullName()));
        vh.tvFeaturePostedOn.setText(Util.getRelativeTimeAgo(user.getTaskPostedOn().toString()));

        return convertView;
    }
}
