package com.mezyapps.bill_app.view.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mezyapps.bill_app.R;

public class NotificationListFragment extends Fragment {

    private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_notification_list, container, false);
        mContext=getActivity();
        find_View_IDS(view);
        events();
        return view;
    }

    private void find_View_IDS(View view) {

    }

    private void events() {

    }
}
