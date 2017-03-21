package com.pasta.mensadd.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.MealListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.networking.LoadMealsCallback;
import com.pasta.mensadd.networking.NetworkController;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class LargeImageFragment extends Fragment {
    public Bitmap mMealImage = null;
    public final static String TAG_MEAL_IMG = "meal_img";

    public LargeImageFragment() {}

    public static LargeImageFragment newInstance(Bitmap image) {
        LargeImageFragment fragment = new LargeImageFragment();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Bundle args = new Bundle();
        args.putByteArray(TAG_MEAL_IMG, byteArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            byte[] byteArray = getArguments().getByteArray(TAG_MEAL_IMG);
            mMealImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_large_image, container, false);
        ImageView largeImage = (ImageView) view.findViewById(R.id.largeMealImage);
        largeImage.setImageBitmap(mMealImage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentController.hideLargeImageFragment(getFragmentManager());
            }
        });
        return view;
    }
}
