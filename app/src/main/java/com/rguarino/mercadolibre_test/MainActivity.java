package com.rguarino.mercadolibre_test;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.rguarino.mercadolibre_test.adapter.ProductAdapter;
import com.rguarino.mercadolibre_test.entity.Product;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.mercadolibre);
        }

        items = new ArrayList();
        layoutNoItems = findViewById(R.id.layout_no_items);
        progressBar = findViewById(R.id.progressBar);
        container = findViewById(R.id.container);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getApplicationContext(), R.drawable.line_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);

        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchInAPI(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //TODO Aca gestionamos la gestion de sugerencias
                //searchInAPI(newText);
                //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                layoutNoItems.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
        if (text != null && !text.isEmpty() && text.length() >= 3) {
            progressBar.setVisibility(View.VISIBLE);
            Retrofit.searchItems(text)
                    .enqueue(new Retrofit() {
                        @Override
                        public void onResponse(int statusCode, JSONObject jResponse) {
                            try {

                                Type productType = new TypeToken<List<Product>>() { }.getType();
                                List<Product> product = new Gson().fromJson(jResponse.getJSONArray("results").toString(), productType);

                                showList(product);
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

    private void showList(List<Product> products) {
        items.clear();
        items.addAll(products);

        progressBar.setVisibility(View.GONE);
        if(items.size() > 0) {
            layoutNoItems.setVisibility(View.GONE);

            productAdapter = new ProductAdapter(this, products);
            recyclerView.setAdapter(productAdapter);
            productAdapter.setOnViewListener(new OnViewListener() {
                @Override
                public void viewOnClick(View v, int position_p) {
                    Intent i = new Intent(MainActivity.this, ProductActivity.class);
                    //i.putExtra("idCampo", campos.get(position_p).getId());
                    startActivity(i);
                    overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                }
            });
        } else {
            layoutNoItems.setVisibility(View.VISIBLE);
        }
    }

    private void manageError() {
        progressBar.setVisibility(View.GONE);
        Snackbar.make(container, "Ocurrio un error inesperado, reintente", Snackbar.LENGTH_LONG)
                .show();
    }
}
