package com.rguarino.mercadolibre_test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
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
import com.rguarino.mercadolibre_test.interfaces.OnLoadMoreListener;
import com.rguarino.mercadolibre_test.interfaces.OnViewListener;
import com.rguarino.mercadolibre_test.service.Retrofit;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // Suggestion persistence
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor shareEditor;
    private final String SUGGESTION_SEARCH = "SUGGESTION_SEARCH";
    private int PRIVATE_MODE = 0;
    private Set<String> suggestion;

    // Search
    private Boolean isSearching = false;
    private Integer offset = 0;
    private Boolean noMore = false;
    private String currentQuery;
    public final int PRODUCT_INCREASE_PAGER = 20;

    // Views
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.mercadolibre);

        sharedPreferences = getSharedPreferences(SUGGESTION_SEARCH, PRIVATE_MODE);
        shareEditor = sharedPreferences.edit();
        suggestion = sharedPreferences.getStringSet(SUGGESTION_SEARCH, new ArraySet<String>());

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

        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setSuggestions(suggestion.toArray(new String[suggestion.size()]));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSupportActionBar().setTitle(query);
                progressBar.setVisibility(View.VISIBLE);
                offset = 0;
                items.clear();
                productAdapter.notifyDataSetChanged();
                currentQuery = query;

                if(!suggestion.contains(currentQuery)) {
                    suggestion.add(currentQuery);
                    shareEditor.putStringSet(SUGGESTION_SEARCH, suggestion);
                    shareEditor.commit();
                    searchView.setSuggestions(suggestion.toArray(new String[suggestion.size()]));
                }

                searchItems();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                layoutNoItems.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
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

                            searchItems();
                        }
                    });
                }
            }
        });

        productAdapter.setOnViewListener(new OnViewListener() {
            @Override
            public void viewOnClick(View v, int position_p) {
                Product item = items.get(position_p);
                if (item.getBuying_mode().equals("buy_it_now")) {
                    Intent i = new Intent(MainActivity.this, ProductActivity.class);
                    i.putExtra("product", item);
                    startActivityForResult(i, 1);
                    overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Atención")
                            .setMessage("Solo se permite ver el detalle de productos del tipo 'buy_it_now', no esta permitido otros tales como 'classified'. Realiza una búsqueda tal como 'Celular', 'Notebook' u otros")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();

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

    public void searchItems() {
        isSearching = true;
        Retrofit.searchItems(PRODUCT_INCREASE_PAGER, currentQuery, offset)
                .enqueue(new Retrofit() {
                    @Override
                    public void onResponse(int statusCode, JSONObject jResponse) {
                        try {
                            int total = jResponse.getJSONObject("paging").getInt("total");
                            offset+= PRODUCT_INCREASE_PAGER;
                            noMore = offset >= total;
                            isSearching = false;

                            Type productType = new TypeToken<List<Product>>() {}.getType();
                            List<Product> products = new Gson().fromJson(jResponse.getJSONArray("results").toString(), productType);

                            // Elimino el loader more
                            if(items.size() > 0 && items.get(items.size() -1) == null) {
                                items.remove(items.size() - 1);
                            }

                            // Agrego productos y refresco
                            items.addAll(products);
                            productAdapter.notifyDataSetChanged();
                            productAdapter.loadEnded();

                            if(!searchView.isSearchOpen()) {
                                progressBar.setVisibility(View.GONE);
                                if (items.size() > 0) {
                                    layoutNoItems.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    layoutNoItems.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            manageError(null);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(int statusCode, String message) {
                        manageError(message);
                    }
                });
    }

    private void manageError(String message) {
        isSearching = false;
        progressBar.setVisibility(View.GONE);
        Snackbar
                .make(container, message != null ? message : "Ocurrio un error inesperado, reintente", Snackbar.LENGTH_LONG)
                .show();
    }
}
