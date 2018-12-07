package com.rguarino.mercadolibre_test.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rguarino.mercadolibre_test.R;
import com.rguarino.mercadolibre_test.entity.Product;
import com.rguarino.mercadolibre_test.interfaces.OnViewListener;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> listProduct;
    private Context context;

    private OnViewListener mOnViewListener;
    public void setOnViewListener(OnViewListener mOnViewListener) {
        this.mOnViewListener = mOnViewListener;
    }

    public ProductAdapter(Context activity, List<Product> listProduct) {
        this.context = activity;
        this.listProduct = listProduct;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Product product = listProduct.get(position);
        final ProductViewHolder viewHolder = (ProductViewHolder) holder;
        viewHolder.lblTitle.setText(product.getTitle());
        viewHolder.lblPrice.setText("$ " + product.getPrice().intValue());

        if(product.getInstallments() != null && product.getInstallments().getRate().intValue() == 0) {
            viewHolder.lblInstallments.setText("Hasta " + product.getInstallments().getQuantity() + " cuotas sin interés");
        } else {
            viewHolder.lblInstallments.setVisibility(View.GONE);
        }

        if(product.getShipping().getFree_shipping()) {
            viewHolder.lblShipping.setText("Envío gratis");
        } else {
            viewHolder.lblShipping.setVisibility(View.GONE);
        }

        Glide
                .with(context)
                .load(product.getThumbnail())
                .into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return listProduct == null ? 0 : listProduct.size();
    }

    private class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView lblTitle;
        TextView lblPrice;
        TextView lblInstallments;
        TextView lblShipping;

        public ProductViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            lblTitle = view.findViewById(R.id.lblTitle);
            lblPrice = view.findViewById(R.id.lblPrice);
            lblInstallments = view.findViewById(R.id.lblInstallments);
            lblShipping = view.findViewById(R.id.lblShipping);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnViewListener.viewOnClick(v, getAdapterPosition());
                }
            });


        }
    }
}
