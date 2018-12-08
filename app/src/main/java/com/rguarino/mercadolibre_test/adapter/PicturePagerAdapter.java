package com.rguarino.mercadolibre_test.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.rguarino.mercadolibre_test.PhotoViewActivity;
import com.rguarino.mercadolibre_test.ProductActivity;
import com.rguarino.mercadolibre_test.R;
import com.rguarino.mercadolibre_test.entity.Picture;
import com.rguarino.mercadolibre_test.interfaces.OnViewListener;

import java.util.List;

public class PicturePagerAdapter extends PagerAdapter
{
    List<Picture> pictures;
    Context mContext;
    LayoutInflater mLayoutInflater;

    public PicturePagerAdapter(Context context, List<Picture> pictures) {
        mContext = context;
        this.pictures = pictures;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private OnViewListener mOnViewListener;
    public void setOnViewListener(OnViewListener mOnViewListener) {
        this.mOnViewListener = mOnViewListener;
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
        View itemView = mLayoutInflater.inflate(R.layout.item_image_product, container, false);
        ImageView imageView =  itemView.findViewById(R.id.image);

        container.addView(itemView);
        Glide
                .with(mContext)
                .load(pictures.get(position).getUrl())
                .into(imageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnViewListener.viewOnClick(v, 0);
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
