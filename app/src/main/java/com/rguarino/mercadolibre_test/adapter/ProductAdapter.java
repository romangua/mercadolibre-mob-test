package com.rguarino.mercadolibre_test.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rguarino.mercadolibre_test.R;
import com.rguarino.mercadolibre_test.entity.Product;
import com.rguarino.mercadolibre_test.interfaces.OnLoadMoreListener;
import com.rguarino.mercadolibre_test.interfaces.OnViewListener;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> listProduct;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean isLoading;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;

    private OnViewListener mOnViewListener;
    public void setOnViewListener(OnViewListener mOnViewListener) {
        this.mOnViewListener = mOnViewListener;
    }

    public ProductAdapter(Context activity, RecyclerView recyclerView, List<Product> listProduct) {
        this.context = activity;
        this.listProduct = listProduct;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    private OnLoadMoreListener onLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void loadEnded() {
        isLoading = false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return listProduct.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return listProduct == null ? 0 : listProduct.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {
            Product product = listProduct.get(position);
            final ProductViewHolder viewHolder = (ProductViewHolder) holder;
            viewHolder.setIsRecyclable(false);
            viewHolder.lblTitle.setText(product.getTitle());
            String price = String.format("%,d", (int)product.getPrice());
            viewHolder.lblPrice.setText("$ " + price);

            if (product.getInstallments() != null && product.getInstallments().getRate() == 0) {
                viewHolder.lblInstallments.setText("Hasta " + product.getInstallments().getQuantity() + " cuotas sin interés");
                viewHolder.layoutInstallments.setVisibility(View.VISIBLE);
            } else {
                viewHolder.layoutInstallments.setVisibility(View.GONE);
            }

            if (product.getShipping().getFree_shipping()) {
                viewHolder.lblShipping.setText("Envío gratis");
                viewHolder.layoutShipping.setVisibility(View.VISIBLE);
            } else {
                viewHolder.layoutShipping.setVisibility(View.GONE);
            }

            Glide
                    .with(context)
                    .load(product.getThumbnail())
                    .into(viewHolder.image);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView lblTitle;
        TextView lblPrice;
        LinearLayout layoutInstallments;
        TextView lblInstallments;
        LinearLayout layoutShipping;
        TextView lblShipping;

        public ProductViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            lblTitle = view.findViewById(R.id.lblTitle);
            lblPrice = view.findViewById(R.id.lblPrice);
            layoutInstallments = view.findViewById(R.id.layoutInstallments);
            lblInstallments = view.findViewById(R.id.lblInstallments);
            layoutShipping = view.findViewById(R.id.layoutShipping);
            lblShipping = view.findViewById(R.id.lblShipping);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnViewListener.viewOnClick(v, getAdapterPosition());
                }
            });


        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressLoadMore);
        }
    }
}
