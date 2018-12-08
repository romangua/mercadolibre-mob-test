package com.rguarino.mercadolibre_test;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.rguarino.mercadolibre_test.adapter.ProductAdapter;
import com.rguarino.mercadolibre_test.entity.Product;
import com.rguarino.mercadolibre_test.interfaces.OnLoadMoreListener;
import com.rguarino.mercadolibre_test.interfaces.OnViewListener;
import com.rguarino.mercadolibre_test.service.Retrofit;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FrameLayout container;
    private MaterialSearchView searchView;
    private RelativeLayout layoutNoItems;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> items;
    private Boolean isSearching = false;
    private Integer offset = 0;
    private Boolean noMore = false;
    private String currentQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.mercadolibre);
        }

        //Intent i = new Intent(MainActivity.this, ProductActivity.class);
        //startActivity(i);

        layoutNoItems = findViewById(R.id.layout_no_items);
        progressBar = findViewById(R.id.progressBar);
        container = findViewById(R.id.container);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        productAdapter = new ProductAdapter(this.getApplicationContext(), recyclerView, items);
        recyclerView.setAdapter(productAdapter);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getApplicationContext(), R.drawable.line_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);

        productAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!noMore) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Agrego un item null para manejar la view del loader
                            items.add(null);
                            productAdapter.notifyItemInserted(items.size() - 1);

                            searchInAPI(currentQuery);
                        }
                    });
                }
            }
        });

        productAdapter.setOnViewListener(new OnViewListener() {
            @Override
            public void viewOnClick(View v, int position_p) {
                Intent i = new Intent(MainActivity.this, ProductActivity.class);
                //i.putExtra("idCampo", campos.get(position_p).getId());
                startActivity(i);
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
            }
        });

        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearching = true;
                items.clear();
                productAdapter.notifyDataSetChanged();
                currentQuery = query;
                searchInAPI(currentQuery);
                progressBar.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //TODO Aca gestionamos las sugerencias
                //searchInAPI(newText);
                //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                layoutNoItems.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                if(!isSearching) {
                    if (items.size() > 0)
                        recyclerView.setVisibility(View.VISIBLE);
                    else
                        layoutNoItems.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Drawable drawable = menu.findItem(R.id.action_search).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.iconMenu), PorterDuff.Mode.SRC_ATOP);
        }
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void searchInAPI(String text) {
        if (text != null && !text.isEmpty()) {
            Retrofit.searchItems(text, offset)
                    .enqueue(new Retrofit() {
                        @Override
                        public void onResponse(int statusCode, JSONObject jResponse) {
                            try {
                                int total = jResponse.getJSONObject("paging").getInt("total");
                                offset+= Retrofit.PRODUCT_INCREASE_PAGER;
                                noMore = offset >= total;

                                Log.e("total", ""+total);
                                Log.e("offset", ""+offset);

                                isSearching = false;
                                //productAdapter.loadEnded();

                                Type productType = new TypeToken<List<Product>>() { }.getType();
                                List<Product> products = new Gson().fromJson(jResponse.getJSONArray("results").toString(), productType);

                                // Elimino el loader
                                if(items.size() > 0)
                                    items.remove(items.size() - 1);

                                // Agrego productos y refresco
                                items.addAll(products);
                                productAdapter.notifyDataSetChanged();
                                productAdapter.loadEnded();


                                progressBar.setVisibility(View.GONE);
                                if(items.size() > 0) {
                                    layoutNoItems.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    layoutNoItems.setVisibility(View.VISIBLE);
                                }
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
    }

    private void manageError() {
        isSearching = false;
        progressBar.setVisibility(View.GONE);
        Snackbar.make(container, "Ocurrio un error inesperado, reintente", Snackbar.LENGTH_LONG)
                .show();
    }
}
