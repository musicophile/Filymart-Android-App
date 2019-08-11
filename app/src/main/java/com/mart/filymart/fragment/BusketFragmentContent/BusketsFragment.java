package com.mart.filymart.fragment.BusketFragmentContent;

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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mart.filymart.CheckAddressContent.CheckAddressActivity;
import com.mart.filymart.JSONParser;
import com.mart.filymart.LoginActivity;
import com.mart.filymart.Orders;
import com.mart.filymart.OrdersAdapter;
import com.mart.filymart.R;
import com.mart.filymart.RegisterActivity;
import com.mart.filymart.TransparentProgressDialog;
import com.mart.filymart.fragment.BusketFragmentContent.model.OrderModel;
import com.mart.filymart.fragment.BusketFragmentContent.presenter.BusketFragmentInterpreter;
import com.mart.filymart.fragment.BusketFragmentContent.presenter.IBusketFragmentPresenter;
import com.mart.filymart.fragment.BusketFragmentContent.view.IBusketFragmentView;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class BusketsFragment extends Fragment implements IBusketFragmentView {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private ConstraintLayout MConstraint, Guest, noOrder;
    private List<Orders> albumList;
    private Button pProceed, btnLogin, btnRegister;
    private TextView PriceSum;
    private SessionManager session;
    private SQLiteHandler db;
    String uid;
    JSONParser jsonParser = new JSONParser();
    private IBusketFragmentPresenter iBusketFragmentPresenter;

    private ProgressDialog pDialog;

    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;

    public BusketsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_busket, container, false);

        MConstraint = view.findViewById(R.id.ConstraintLayoutForOrder);
        Guest = view.findViewById(R.id.ConstraintLayoutForGuest);
        btnLogin = view.findViewById(R.id.btnLoginGuest);
        btnRegister = view.findViewById(R.id.btnRegisterGuest);
        PriceSum = view.findViewById(R.id.tvPriceSum);
        recyclerView = view.findViewById(R.id.recycler_view);
        noOrder = view.findViewById(R.id.noOrder);


        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        albumList = new ArrayList<>();

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


        //intialize linear layout manager vertically
        LinearLayoutManager linearVertical = new LinearLayoutManager(getContext());

        //LinearLayoutManager
        recyclerView.setLayoutManager(linearVertical);

        pProceed = view.findViewById(R.id.proceed);
        db = new SQLiteHandler(getContext());
        session = new SessionManager(getContext());
        iBusketFragmentPresenter = new BusketFragmentInterpreter(this);

        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");
        noOrder.setVisibility(View.INVISIBLE);

        if (user_id == null){
            MConstraint.setVisibility(View.INVISIBLE);
            Guest.setVisibility(View.VISIBLE);
        }else{
            MConstraint.setVisibility(View.VISIBLE);
            Guest.setVisibility(View.INVISIBLE);
            if (isNetworkAvailable()){
                pd.show();
                h.postDelayed(r,5000);
                iBusketFragmentPresenter.loadData(user_id);
            }else{
                Toast.makeText(getContext(),
                        "Check Your Network Connection", Toast.LENGTH_LONG)
                        .show();
            }



        }
        pProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CheckAddressActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();


            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);
                Objects.requireNonNull(getActivity()).finish();


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
    public void loadDatas(ArrayList<OrderModel> info, String uid) {
        adapter = new OrdersAdapter(BusketsFragment.this, info);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        PriceSum.setText(uid);
    if (uid == "0"){
    noOrder.setVisibility(View.VISIBLE);
    }

    }



}