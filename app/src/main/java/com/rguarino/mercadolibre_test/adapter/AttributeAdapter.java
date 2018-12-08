package com.rguarino.mercadolibre_test.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.rguarino.mercadolibre_test.R;
import com.rguarino.mercadolibre_test.entity.Attribute;
import java.util.List;

public class AttributeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Attribute> listAttribute;
    private Context context;

    public AttributeAdapter(Context activity, List<Attribute> listAttribute) {
        this.context = activity;
        this.listAttribute = listAttribute;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listAttribute == null ? 0 : listAttribute.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attribute, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Attribute product = listAttribute.get(position);
        final ProductViewHolder viewHolder = (ProductViewHolder) holder;
        viewHolder.setIsRecyclable(false);
        viewHolder.lblName.setText(product.getName() + ":");
        viewHolder.lblValue.setText(product.getValue_name());
    }

    private class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView lblName;
        TextView lblValue;

        public ProductViewHolder(View view) {
            super(view);
            lblName = view.findViewById(R.id.lblName);
            lblValue = view.findViewById(R.id.lblValue);
        }
    }
}
