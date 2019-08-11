package com.mart.filymart.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.mart.filymart.JSONParser;
import com.mart.filymart.Product;
import com.mart.filymart.ProductsAdapter;
import com.mart.filymart.R;
import com.mart.filymart.SearchedActivity;
import com.mart.filymart.TransparentProgressDialog;
import com.mart.filymart.fragment.ShoppingFragmentContent.model.ProductModel;
import com.mart.filymart.fragment.ShoppingFragmentContent.presenter.IShoppingFragmentPresenter;
import com.mart.filymart.fragment.ShoppingFragmentContent.presenter.ShoppingFragmentPresenter;
import com.mart.filymart.fragment.ShoppingFragmentContent.view.IShoppingFragmentView;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ShoppingFragment extends Fragment implements AdapterView.OnItemSelectedListener, IShoppingFragmentView {

    private static final String URL_PRODUCTS = "http://www.filymart.com/get_all_products.php";

    //@BindView (R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.searchBar)
    EditText searchBar;
    @BindView(R.id.btnSearch)
    ImageButton btnSearch;
    private ProductsAdapter adapter;
    private List<Product> productList;
    ProgressDialog progressDialog;
    private SessionManager session;
    private ConstraintLayout fragmentLayout;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    private IShoppingFragmentPresenter iShoppingFragmentPresenter;

    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;

    String[] bankNames={"Featured Products","Fruits & Vegetables", "Milk, Eggs & Meat", "Beverages & Drinks", "Grain, Food & Oil",
    "Accessories & Electronics", "Mens Collections", "Womens Collection", "Kids Collections", "Snacks,Biskuits & Bites",
            "Cleaning & Hygiene", "Featured Brands"};

    public ShoppingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);

        Spinner spin =  view.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        fragmentLayout = view.findViewById(R.id.fragmentLayout);
        fragmentLayout.setClickable(true);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin.setAdapter(aa);
        ButterKnife.bind(this,view);
        recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        productList = new ArrayList<>();


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

        db = new SQLiteHandler(getContext());

        iShoppingFragmentPresenter = new ShoppingFragmentPresenter(this);
        iShoppingFragmentPresenter.setProgressBarVisiblity(View.INVISIBLE);

        if(getActivity().getIntent().getIntExtra("fragmentNumber",0)==1){
            //set the desired fragment as current fragment to fragment pager
            Toast.makeText(getContext(),
                    "Product is not Available now!", Toast.LENGTH_LONG)
                    .show();
        }

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
    @OnClick(R.id.btnSearch)
    public void search(){
        String value = searchBar.getText().toString().trim();

        // Check for empty data in the form
        if (!value.isEmpty()) {
            // login user
            Intent intent = new Intent(getContext(), SearchedActivity.class);
            intent.putExtra("value", value);
            startActivity(intent);
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getContext(),
                    "Please enter the Product Name!", Toast.LENGTH_LONG)
                    .show();
        }
    }


    @Override
    public void loadDatas(ArrayList<ProductModel> info, int a) {
        adapter = new ProductsAdapter(ShoppingFragment.this, info, a);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }

    }

    @Override
    public void onSetProgressBarVisibility(int visibility) {
    }

    @Override
    public boolean setClickable(boolean visibility) {
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String value = bankNames[position];

        if (isNetworkAvailable()) switch (value) {
            case "Fruits & Vegetables":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.clear();

                break;
            case "Milk, Eggs & Meat":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadEData();
                //endProgressBar();
                break;
            case "Beverages & Drinks":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadBeveData();
                break;
            case "Womens Collection":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadBDataWomens();
                break;
            case "Kids Collections":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadADataKids();
                break;
            case "Cleaning & Hygiene":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadAData();
                break;
            case "Accessories & Electronics":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadFoodDataAccessoriesElectronics();
                //endProgressBar();
                break;
            case "Mens Collections":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadEDataMens();
                break;
            case "Grain, Food & Oil":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadFoodData();
                break;
            case "Snacks,Biskuits & Bites":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadBData();
                break;
            case "Featured Brands":
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadBeveDataAllBrands();
                break;
            default:
                pd.show();
                h.postDelayed(r,5000);
                iShoppingFragmentPresenter.loadData();

                break;
        }
        else{
            Toast.makeText(getContext(),
                    "Check Your Network Connection", Toast.LENGTH_LONG)
                    .show();
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        Resources r= getLayoutInflater().getContext().getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));

    }

}
