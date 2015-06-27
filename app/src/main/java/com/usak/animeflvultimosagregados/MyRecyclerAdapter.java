package com.usak.animeflvultimosagregados;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Guillem on 23/06/2015.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<ListRowHolder> {

    private List<Item> itemList;
    private Context mContext;

    public MyRecyclerAdapter(Context context, List<Item> itemList) {
        this.itemList = itemList;
        this.mContext = context;
    }

    @Override
    public ListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, null);
        ListRowHolder mh = new ListRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ListRowHolder listRowHolder, int i) {
        Item item = itemList.get(i);

        Picasso.with(mContext).load(item.getThumbnail())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(listRowHolder.thumbnail);

        listRowHolder.title.setText(Html.fromHtml(item.getTitle()));
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }
}
