package com.rguarino.mercadolibre_test.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.rguarino.mercadolibre_test.R;
import com.rguarino.mercadolibre_test.entity.Picture;

import java.util.List;

class PagerProductAdapter extends PagerAdapter
{
    List<Picture> pictures;
    Context mContext;
    LayoutInflater mLayoutInflater;

    public PagerProductAdapter(Context context, List<Picture> pictures) {
        mContext = context;
        this.pictures = pictures;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.image_pager_product, container, false);
        ImageView imageView =  itemView.findViewById(R.id.image);

        container.addView(itemView);
        Glide
                .with(mContext)
                .load(pictures.get(position).getUrl())
                .into(imageView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
