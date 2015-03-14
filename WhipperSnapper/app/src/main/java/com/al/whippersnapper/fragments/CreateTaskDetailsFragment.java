package com.al.whippersnapper.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.al.whippersnapper.R;
import com.al.whippersnapper.activities.CreateTaskActivity;

import java.io.ByteArrayOutputStream;



public class CreateTaskDetailsFragment extends Fragment {
    public interface onTaskPhotoClickListener {
        public void onTaskPhotoClick();
    }

    private Spinner spTaskType;
    private EditText etTaskDetails;

    public ImageView getIvTaskPhoto() {
        return ivTaskPhoto;
    }

    private ImageView ivTaskPhoto;
    private onTaskPhotoClickListener taskPhotoClickListener;

    private OnFragmentInteractionListener mListener;


    public static CreateTaskDetailsFragment newInstance() {
        CreateTaskDetailsFragment fragment = new CreateTaskDetailsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CreateTaskDetailsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_create_task_details, container, false);


        spTaskType = (Spinner) v.findViewById(R.id.spTaskType);
        etTaskDetails = (EditText) v.findViewById(R.id.etTaskDetails);
        ivTaskPhoto = (ImageView) v.findViewById(R.id.ivTaskPhoto);

        ivTaskPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskPhotoClickListener.onTaskPhotoClick();
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

        if (activity instanceof CreateTaskActivity) {
            taskPhotoClickListener = (CreateTaskActivity) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
