package com.xmstr.electrocount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmstr.electrocount.Item;
import com.xmstr.electrocount.R;

import java.util.List;

/**
 * Created by xmast_000 on 11.07.2015.
 */
public class ItemsAdapter extends BaseAdapter {

    List<Item> items;

    public ItemsAdapter(List<Item> items) {
        this.items = items;
    }

    public void refresh(List<Item> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Item item = getItem(i);
        ViewHolder viewHolder;
        if (view == null) {
            view = ((LayoutInflater)viewGroup.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.statistic_row, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.textView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(item.text + " - " + item.date);
        return view;
    }

    static class ViewHolder {
        TextView textView;
    }
}