package com.usak.animeflvultimosagregados;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Guillem on 23/06/2015.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ListRowHolder> {

    private List<Item> itemList;
    private Context mContext;
    private ClickListener clickListener;

    public MyRecyclerAdapter(Context context, List<Item> itemList) {
        this.itemList = itemList;
        this.mContext = context;
    }

    @Override
    public ListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tile, null);
        ListRowHolder mh = new ListRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ListRowHolder listRowHolder, int i) {
        Item item = itemList.get(i);

        Picasso.with(mContext).load(item.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(listRowHolder.thumbnail);

        listRowHolder.title.setText(Html.fromHtml(item.getTitle()));

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    public interface ClickListener {
        public void itemClicked(View view, int i);
    }

    public class ListRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView thumbnail;
        protected TextView title;

        public ListRowHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            this.title = (TextView) view.findViewById(R.id.title);

        }

        @Override
        public void onClick(View v) {
            if(clickListener != null) {
                clickListener.itemClicked(v, getPosition());
            }
        }
    }


}
