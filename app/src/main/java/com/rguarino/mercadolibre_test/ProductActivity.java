package com.rguarino.mercadolibre_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rguarino.mercadolibre_test.adapter.*;
import com.rguarino.mercadolibre_test.entity.Picture;
import com.rguarino.mercadolibre_test.entity.Product;
import com.rguarino.mercadolibre_test.interfaces.OnViewListener;
import com.rguarino.mercadolibre_test.service.Retrofit;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private FrameLayout container;
    private ProgressBar progressBar;
    private Product product;
    private ViewPager pager;
    private PicturePagerAdapter picturePagerAdapter;
    private List<Picture> pictures;
    private TextView lblPictureQuantity;
    private TextView lblUsed;
    private TextView lblTitle;
    private TextView lblPrice;
    private RelativeLayout layoutPay;
    private TextView lblPay;
    private ImageView imgTruck;
    private TextView lblShipping;
    private TextView lblProtectedBuyAndQuantitySold;
    private TextView lblPoints;
    private TextView lblLocationSeller;
    private TextView lblDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        product = getIntent().getExtras().getParcelable("product");
        progressBar = findViewById(R.id.progressBar);
        container = findViewById(R.id.container);
        pager = findViewById(R.id.pager);
        lblPictureQuantity = findViewById(R.id.lblPictureQuantity);
        lblPictureQuantity.setVisibility(View.GONE);
        lblUsed = findViewById(R.id.lblUsed);
        lblUsed.setVisibility(View.GONE);
        lblTitle = findViewById(R.id.lblTitle);
        lblPrice = findViewById(R.id.lblPrice);
        layoutPay = findViewById(R.id.layoutPay);
        lblPay = findViewById(R.id.lblPay);
        imgTruck = findViewById(R.id.imgTruck);
        lblShipping = findViewById(R.id.lblShipping);
        lblProtectedBuyAndQuantitySold = findViewById(R.id.lblProtectedBuyAndQuantitySold);
        lblPoints = findViewById(R.id.lblPoints);
        lblLocationSeller = findViewById(R.id.lblLocationSeller);
        lblDescription = findViewById(R.id.lblDescription);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewAttribute);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        AttributeAdapter attributeAdapter = new AttributeAdapter(this.getApplicationContext(), product.getAttributes());
        recyclerView.setAdapter(attributeAdapter);

        searchItem();
        getDescription();
        fillActivity();
    }

    private void fillActivity() {
        pictures = new ArrayList<>();
        picturePagerAdapter = new PicturePagerAdapter(this.getApplicationContext(), pictures);
        pager.setAdapter(picturePagerAdapter);

        picturePagerAdapter.setOnViewListener(new OnViewListener() {
            @Override
            public void viewOnClick(View v, int position) {
                try {
                    Intent i = new Intent(ProductActivity.this, PhotoViewActivity.class);
                    i.putExtra("url", product.getPictures().get(pager.getCurrentItem()).getUrl());
                    startActivity(i);
                }catch (Exception e){}
            }
        });

        if(product.getCondition().equals("used")) {
            lblUsed.setVisibility(View.VISIBLE);
        }

        lblTitle.setText(product.getTitle());
        String price = String.format("%,d", (int)product.getPrice());
        lblPrice.setText("$ " + price);

        if(product.getInstallments() != null) {
            if (product.getInstallments().getRate() == 0) {
                lblPay.setText("Pagá en hasta " + product.getInstallments().getQuantity() + " cuotas sin interés");
                lblPay.setTextColor(getResources().getColor(R.color.productPayNoRate));
            } else {
                lblPay.setText("Pagá en hasta " + product.getInstallments().getQuantity() + " cuotas");
                lblPay.setTextColor(getResources().getColor(R.color.productPay));
            }
        } else {
            layoutPay.setVisibility(View.GONE);
        }

        if(product.getShipping().getFree_shipping()) {
            lblShipping.setText("Envío gratis");
            lblShipping.setTextColor(getResources().getColor(R.color.productShippingFree));
            imgTruck.setImageDrawable(getResources().getDrawable(R.drawable.truck_icon_free));
        } else {
            lblShipping.setText("Envío $ 199");
            lblShipping.setTextColor(getResources().getColor(R.color.productShipping));
            imgTruck.setImageDrawable(getResources().getDrawable(R.drawable.truck_icon));
        }

        String quantitySold = "";
        if(product.getSold_quantity() > 0) {
            quantitySold = "- " + product.getSold_quantity() + " vendidos";
        }
        lblProtectedBuyAndQuantitySold.setText("Compra protegida " + quantitySold);
        lblLocationSeller.setText(product.getAddress().getState_name() + ", " + product.getAddress().getCity_name());

        // $1000 = 50 points
        int points = (int)(product.getPrice() * 50) / 1000;
        lblPoints.setText("Sumas " + points + " Mercado Puntos");

    }

    public void searchItem() {
        Retrofit.getItem(product.getId())
                .enqueue(new Retrofit() {
                    @Override
                    public void onResponse(int statusCode, JSONObject jResponse) {
                        try {
                            Type productType = new TypeToken<Product>() {}.getType();
                            product = new Gson().fromJson(jResponse.toString(), productType);

                            // Saco el thumbail y agrego las imagenes
                            pictures.clear();
                            pictures.addAll(product.getPictures());
                            picturePagerAdapter.notifyDataSetChanged();
                            lblPictureQuantity.setVisibility(View.VISIBLE);
                            lblPictureQuantity.setText(product.getPictures().size() + " Fotos");

                        } catch (Exception e) {
                            manageError();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(int statusCode, String message) {
                        manageError();
                    }
                });
    }

    public void getDescription() {
        Retrofit.getItemDescription(product.getId())
                .enqueue(new Retrofit() {
                    @Override
                    public void onResponse(int statusCode, JSONObject jResponse) {
                        try {
                            String description = jResponse.getString("plain_text");
                            lblDescription.setText(description);
                        } catch (Exception e) {
                            manageError();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(int statusCode, String message) {
                        manageError();
                    }
                });
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }

    private void manageError() {
        progressBar.setVisibility(View.GONE);
        Snackbar.make(container, "Ocurrio un error inesperado, reintente", Snackbar.LENGTH_LONG)
                .show();
    }
}
