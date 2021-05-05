package com.mart.filymart.FruitsVergetablesContent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mart.filymart.FruitsVergetablesContent.model.ProductModel;
import com.mart.filymart.FruitsVergetablesContent.presenter.FruitsVegetablesPresenter;
import com.mart.filymart.FruitsVergetablesContent.presenter.IFruitsVegetablesPresenter;
import com.mart.filymart.FruitsVergetablesContent.view.IFruitsVegetablesView;
import com.mart.filymart.JSONParser;
import com.mart.filymart.Product;
import com.mart.filymart.R;
import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class FruitsVergetablesActivity extends AppCompatActivity implements IFruitsVegetablesView {

    private static final String URL_PRODUCTS = "http://www.filymart.com/get_all_products.php";

    private RecyclerView recyclerView;
    private ProductsAdapterFv adapter;
    private List<Product> productList;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialogFV pd;
    private Handler h;
    private Runnable r;
    IFruitsVegetablesPresenter iFruitsVegetablesPresenter;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruits_vergetables);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        recyclerView = findViewById(R.id.recycler_view);



        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        productList = new ArrayList<>();

        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setCancelable(false);;
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        iFruitsVegetablesPresenter = new FruitsVegetablesPresenter(this);
        h = new Handler();
        pd = new ProgressDialogFV(this, R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };


        if (isNetworkAvailable()){
            pd.show();
            h.postDelayed(r,5000);
            iFruitsVegetablesPresenter.clear();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Check Your Network Connection", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void loadDatas(ArrayList<ProductModel> info) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        adapter = new ProductsAdapterFv(FruitsVergetablesActivity.this, info);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroy() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        super.onDestroy();
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public void fruitsVergetableBack(View view) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
