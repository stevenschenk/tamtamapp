package com.example.steven.tamtam.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.steven.tamtam.Models.User;
import com.example.steven.tamtam.NoSwipeViewPager;
import com.example.steven.tamtam.R;
import com.example.steven.tamtam.RegisterActivity;

/**
 * Created by cees-jan on 6/21/16.
 */
public class RegisterDescriptionFragment extends Fragment {

    User user;
    Button btnPrev;
    Button btnNext;
    EditText etDescription;
    View inflatedView;
    NoSwipeViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = ((RegisterActivity) getActivity()).getUser();
        inflatedView = inflater.inflate(R.layout.fragment_register_description, container, false);
        pager = ((RegisterActivity) getActivity()).getmViewPager();
        btnPrev = (Button) inflatedView.findViewById(R.id.btnPrevious);
        btnNext = (Button) inflatedView.findViewById(R.id.btnNext);
        etGamertag = (EditText) inflatedView.findViewById(R.id.etGamertag);

        btnPrev.setOnClickListener(btnPrevListener);
        btnNext.setOnClickListener(btnNextListener);


        return inflatedView;

    }


    View.OnClickListener btnPrevListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            pager.setCurrentItem(pager.getCurrentItem()-1);

        }
    };

    View.OnClickListener btnNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            user.setDescription(etDescription.getText().toString());
            pager.setCurrentItem(pager.getCurrentItem()+1);
        }
    };
}
