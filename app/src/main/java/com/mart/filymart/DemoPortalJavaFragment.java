package com.mart.filymart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevinomyonga.pesapaldroid.PesapalDroidFragment;

/**
 * Created by Kevin Omyonga on 12/15/2015.
 */
public class DemoPortalJavaFragment extends Fragment {

    public DemoPortalJavaFragment() {}

    public static DemoPortalJavaFragment newInstance(String fname, String lname, String email,
                                                     String phone, String desc, String amount) {
        DemoPortalJavaFragment f = new DemoPortalJavaFragment();
        Bundle args = new Bundle();
        args.putString("fname", fname);
        args.putString("lname", lname);
        args.putString("email", email);
        args.putString("phone", phone);
        args.putString("desc", desc);
        args.putString("amount", amount);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_java, container, false);

        Bundle args = getArguments();

        PesapalDroidFragment pesapalDroidFragment = new PesapalDroidFragment();
        pesapalDroidFragment.setConsumerKey(getString(R.string.consumer_key));
        pesapalDroidFragment.setConsumerSecret(getString(R.string.consumer_secret));
        pesapalDroidFragment.setDemoEnabled(false);
        pesapalDroidFragment.setMobileEnabled(true);

        //Pass the buyer details
        pesapalDroidFragment.setfName(args.getString("fname"));
        pesapalDroidFragment.setlName(args.getString("lname"));
        pesapalDroidFragment.setEmail(args.getString("email"));
        pesapalDroidFragment.setPhone(args.getString("phone"));
        pesapalDroidFragment.setDesc(args.getString("desc"));
        pesapalDroidFragment.setAmount(args.getString("amount"));

        // Insert the fragment by replacing any existing fragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.content_frame, pesapalDroidFragment)
                .commit();

        return view;
    }
}
