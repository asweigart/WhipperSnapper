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

import java.io.ByteArrayOutputStream;


public class CreateTaskDetailsFragment extends Fragment {

    private Spinner spTaskType;
    private EditText etTaskDetails;
    private ImageView ivTaskPhoto;

    private byte[] photoBytes;

    public static final int TAKE_TASK_PHOTO = 2000;



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





    public void onTaskPhotoClick(View v) {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, TAKE_TASK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        if (requestCode == TAKE_TASK_PHOTO && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        }

        if (photo != null) {
            // depending on if the photo is wider or taller, scale it down proportionally to the ImageView
            if (photo.getWidth() > photo.getHeight()) {
                photo = Bitmap.createScaledBitmap(photo, ivTaskPhoto.getWidth(), (int)(ivTaskPhoto.getHeight() * ((float)(photo.getHeight()) / (float)(photo.getWidth()))), true);
            } else {
                photo = Bitmap.createScaledBitmap(photo, (int)(ivTaskPhoto.getWidth() * ((float)(photo.getWidth()) / (float)(photo.getHeight()))), ivTaskPhoto.getHeight(), true);
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            photoBytes = stream.toByteArray();
            //Log.e("XXXXXXXXXXX", String.valueOf(photoBytes.length)); // confirmed that called createScaledBitmap signifcantly reduces the size.
            ivTaskPhoto.setImageBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        }
    }
}
