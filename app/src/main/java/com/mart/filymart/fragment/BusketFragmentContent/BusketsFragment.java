package com.mart.filymart.fragment.BusketFragmentContent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class BusketsFragment extends Fragment implements IBusketFragmentView {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private ConstraintLayout MConstraint, Guest, noOrder,giftbuttonlayout,productbuttonlayout;
    private List<Orders> albumList;
    private Button pProceed, btnLogin, btnRegister,gProceed;
    private TextView PriceSum;
    private SessionManager session;
    private SQLiteHandler db;
    AlertDialog alertDialog;
    String uid;
    JSONParser jsonParser = new JSONParser();
    private IBusketFragmentPresenter iBusketFragmentPresenter;
    private TextView text_p,text_g;

    private ProgressDialog pDialog;

    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;
    private SharedPreferences pref;

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
        text_g = view.findViewById(R.id.giftText);
        text_p = view.findViewById(R.id.producttext);
        giftbuttonlayout = view.findViewById(R.id.giftbutton);
        productbuttonlayout = view.findViewById(R.id.productbutton);
        gProceed = view.findViewById(R.id.proceed_g);
        noOrder = view.findViewById(R.id.noOrder);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        albumList = new ArrayList<>();
        pProceed = view.findViewById(R.id.proceed);
        db = new SQLiteHandler(getContext());
        session = new SessionManager(getContext());
        pref = getContext().getSharedPreferences("filymart", 0);


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


        iBusketFragmentPresenter = new BusketFragmentInterpreter(this);

        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");
        noOrder.setVisibility(View.INVISIBLE);
        Guest.setVisibility(View.INVISIBLE);
        new AlertDialog.Builder(getContext())
                .setTitle("Choose One")
                .setMessage("Choose Goods Busket or Gift Card Busket")
                .setPositiveButton("Product", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (user_id == null){
                            MConstraint.setVisibility(View.INVISIBLE);
                            Guest.setVisibility(View.VISIBLE);
                            recyclerView = view.findViewById(R.id.recycler_view);
                            //intialize linear layout manager vertically
                            LinearLayoutManager linearVertical = new LinearLayoutManager(getContext());

                            //LinearLayoutManager
                            recyclerView.setLayoutManager(linearVertical);
                        }else
                        {
                            MConstraint.setVisibility(View.VISIBLE);
                            Guest.setVisibility(View.INVISIBLE);
                            productbuttonlayout.setVisibility(View.VISIBLE);
                            text_p.setVisibility(View.VISIBLE);
                            recyclerView = view.findViewById(R.id.recycler_view);
                            //intialize linear layout manager vertically
                            LinearLayoutManager linearVertical = new LinearLayoutManager(getContext());

                            //LinearLayoutManager
                            recyclerView.setLayoutManager(linearVertical);
                            if (isNetworkAvailable()){
                                setBusketType("2");
                                pd.show();
                                h.postDelayed(r,5000);
                                iBusketFragmentPresenter.loadData(user_id);
                            }else{
                                Toast.makeText(getContext(),
                                        "Check Your Network Connection", Toast.LENGTH_LONG)
                                        .show();
                            }



                        }
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Gift Card", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (user_id == null){
                            MConstraint.setVisibility(View.INVISIBLE);
                            Guest.setVisibility(View.VISIBLE);
                            recyclerView = view.findViewById(R.id.recycler_view);
                            LinearLayoutManager linearVertical = new LinearLayoutManager(getContext());

                            recyclerView.setLayoutManager(linearVertical);
                        }else
                        {
                            MConstraint.setVisibility(View.VISIBLE);
                            Guest.setVisibility(View.INVISIBLE);
                            text_g.setVisibility(View.VISIBLE);
                            giftbuttonlayout.setVisibility(View.VISIBLE);
                            recyclerView = view.findViewById(R.id.recycler_view);
                            LinearLayoutManager linearVertical = new LinearLayoutManager(getContext());

                            recyclerView.setLayoutManager(linearVertical);
                            if (isNetworkAvailable()){
                                pd.show();
                                h.postDelayed(r,5000);
                                setBusketType("1");
                                iBusketFragmentPresenter.loadData(user_id);
                            }else{
                                Toast.makeText(getContext(),
                                        "Check Your Network Connection", Toast.LENGTH_LONG)
                                        .show();
                            }



                        }
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();

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

        gProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> users = db.getUserDetails();
                String user_id = users.get("uid");
                String user_email = users.get("email");
                String user_name = users.get("name");
                String user_phone = users.get("phone");
                BigDecimal d = new BigDecimal(100);
                BigDecimal d2 = d.setScale(0, BigDecimal.ROUND_HALF_UP);
                String dd = String.valueOf(d2);

                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("https").authority("www.filymart.com").path("/mobilepayment");
                uriBuilder.appendQueryParameter("amount", dd);

                uriBuilder.appendQueryParameter("description", "Order Payment");
                uriBuilder.appendQueryParameter("type", "MERCHANT");
                uriBuilder.appendQueryParameter("reference", "001");
                uriBuilder.appendQueryParameter("first_name", user_name);
                uriBuilder.appendQueryParameter("last_name", "");
                uriBuilder.appendQueryParameter("email", user_email);
                Uri payPalUri = uriBuilder.build();

                Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
                startActivity(viewIntent);
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


    public boolean setBusketType(String type){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("BusketType", type);
        editor.commit(); //commit changes
        return true;
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