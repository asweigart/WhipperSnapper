package com.al.whippersnapper.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.al.whippersnapper.R;
import com.al.whippersnapper.activities.FindTaskActivity;
import com.al.whippersnapper.activities.ShowTaskDetailsActivity;
import com.al.whippersnapper.models.ParseWSUser;
import com.al.whippersnapper.utils.Util;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;


public class FindTaskListViewFragment extends Fragment {
    private ListView lvTaskList;


    private OnFragmentInteractionListener mListener;

    public static FindTaskListViewFragment newInstance() {
        FindTaskListViewFragment fragment = new FindTaskListViewFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FindTaskListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find_task_list_view, container, false);

        lvTaskList = (ListView) v.findViewById(R.id.lvTaskList);
        lvTaskList.setAdapter(((FindTaskActivity) getActivity()).getLvAdapter()); // TODO - pretty sure this casting is a bad habit

        lvTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the Show Task Details activity
                ParseWSUser user = ((FindTaskActivity) getActivity()).getLvAdapter().getItem(position);

                // get the task photo's bytes
                byte[] taskPhotoBytes = null;
                try {
                    ParseFile taskPhotoFile = user.getTaskPhoto();
                    if (taskPhotoFile != null) {
                        taskPhotoBytes = taskPhotoFile.getData();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(getActivity(), ShowTaskDetailsActivity.class);
                i.putExtra("seniorName", Util.getAnonymizedName(user.getFullName())); // TODO - use constants instead of "seniorName"
                i.putExtra("taskType", user.getTaskType());
                i.putExtra("taskDetails", user.getTaskDetails());
                i.putExtra("postedOn", Util.getRelativeTimeAgo(user.getTaskPostedOn().toString()));
                i.putExtra("taskPhoto", taskPhotoBytes);
                i.putExtra("seniorUsername", user.getUsername());
                startActivity(i);
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
