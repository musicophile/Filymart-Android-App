package com.mart.filymart.fragment.SettingsFragmentContent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mart.filymart.CheckAddressContent.model.AddressModel;
import com.mart.filymart.InfoActivity;
import com.mart.filymart.JSONParser;
import com.mart.filymart.LoginActivity;
import com.mart.filymart.R;
import com.mart.filymart.RegisterActivity;
import com.mart.filymart.TransparentProgressDialog;
import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.fragment.SettingsFragmentContent.presenter.ISettingsFragmentPresenter;
import com.mart.filymart.fragment.SettingsFragmentContent.presenter.SettingsFragmentPresenter;
import com.mart.filymart.fragment.SettingsFragmentContent.view.ISettingsFragmentView;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SettingsFragment extends Fragment implements ISettingsFragmentView {

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private TextView CustomerName, CustomerEmail, CustomerPhone;
    JSONParser jsonParser = new JSONParser();
    private List<AddressModel> addressModelList;
    String name, email, phone;
    private ConstraintLayout MConstraint, Guest;
    private ImageButton editInfo, btnDelete;
    private ISettingsFragmentPresenter iSettingsFragmentPresenter;
    private TransparentProgressDialog pd;
    private Handler h;
    private Button pProceed, btnLogin, btnRegister;
    private Runnable r;


    public SettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        CustomerName = view.findViewById(R.id.tvCustomerName);
        CustomerEmail = view.findViewById(R.id.tvCustomerEmail);
        CustomerPhone = view.findViewById(R.id.tvCustomerPhone);
        editInfo = view.findViewById(R.id.btnEditInfo);
        btnDelete = view.findViewById(R.id.btnDelete);
        MConstraint = view.findViewById(R.id.ForSettings);
        Guest = view.findViewById(R.id.ForGuest);
        btnLogin = view.findViewById(R.id.btnLoginGuest);
        btnRegister = view.findViewById(R.id.btnRegisterGuest);


        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InfoActivity.class);
                startActivity(intent);
            }
        });
        h = new Handler();
        pd = new TransparentProgressDialog(getContext(), R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };
        addressModelList = new ArrayList<>();
        LinearLayoutManager linearVertical = new LinearLayoutManager(getContext());
        db = new SQLiteHandler(getContext());
        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");

        iSettingsFragmentPresenter = new SettingsFragmentPresenter(this);

        if (isNetworkAvailable()){
            pd.show();
            h.postDelayed(r,5000);

            iSettingsFragmentPresenter.loadData(user_id);
        }else{
    Toast.makeText(getContext(),
            "Check Your Network Connection", Toast.LENGTH_LONG)
            .show();
}
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                h.postDelayed(r,5000);
                iSettingsFragmentPresenter.loadDataDelete(user_id);
            }
        });
        if (user_id == null){
            MConstraint.setVisibility(View.INVISIBLE);
            Guest.setVisibility(View.VISIBLE);
        }else{
            MConstraint.setVisibility(View.VISIBLE);
            Guest.setVisibility(View.INVISIBLE);}

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        super.onDestroy();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void loadDatas(ArrayList<AddressModel> info, String name, String email, String phone) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        CustomerName.setText(name);
        CustomerEmail.setText(email);
        CustomerPhone.setText(phone);
    }

    @Override
    public void loadData() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        db.deleteUsers();
        Intent intent1 = new Intent(getContext(), HomeActivity.class);
        intent1.putExtra("fragment",4);
        startActivity(intent1);
    }


}

