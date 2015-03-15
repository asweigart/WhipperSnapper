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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.al.whippersnapper.R;
import com.al.whippersnapper.activities.CreateTaskActivity;

import java.io.ByteArrayOutputStream;



public class CreateTaskDetailsFragment extends Fragment {
    public interface onTaskPhotoClickListener {
        public void onTaskPhotoClick();
    }

    public interface onDoneFromDetailsClickListener {
        public void onDoneFromDetailsClick();
    }

    private Spinner spTaskType;
    private EditText etTaskDetails;

    public ProgressBar getPbInDetailsFrag() {
        return pbInDetailsFrag;
    }

    private ProgressBar pbInDetailsFrag;

    private Button btnDone_FromDetails;

    public ImageView getIvTaskPhoto() {
        return ivTaskPhoto;
    }

    private ImageView ivTaskPhoto;
    private onTaskPhotoClickListener taskPhotoClickListener;
    private onDoneFromDetailsClickListener doneFromDetailsClickListener;

    private OnFragmentInteractionListener mListener;



    public EditText getEtTaskDetails() {
        return etTaskDetails;
    }

    public Spinner getSpTaskType() {
        return spTaskType;
    }

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

    public Button getBtnDone_FromDetails() {
        return btnDone_FromDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_task_details, container, false);


        spTaskType = (Spinner) v.findViewById(R.id.spTaskType);
        etTaskDetails = (EditText) v.findViewById(R.id.etTaskDetails);

        // set up click listener for photo
        ivTaskPhoto = (ImageView) v.findViewById(R.id.ivTaskPhoto);
        ivTaskPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskPhotoClickListener.onTaskPhotoClick();
            }
        });

        // set up click listener for All Done button.
        btnDone_FromDetails = (Button) v.findViewById(R.id.btnDone_FromDetails);
        btnDone_FromDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneFromDetailsClickListener.onDoneFromDetailsClick();
            }
        });

        pbInDetailsFrag = (ProgressBar) v.findViewById(R.id.pbInDetailsFrag);

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
            doneFromDetailsClickListener = (CreateTaskActivity) activity;
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
