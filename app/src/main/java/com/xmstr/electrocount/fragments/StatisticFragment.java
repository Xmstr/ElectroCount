package com.xmstr.electrocount.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.xmstr.electrocount.Item;
import com.xmstr.electrocount.R;
import com.xmstr.electrocount.adapter.ItemsAdapter;
import com.xmstr.electrocount.db.CountsDataSource;

/**
 * Created by xmast_000 on 11.07.2015.
 */
public class StatisticFragment extends Fragment{
    CountsDataSource dataSource;
    ItemsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        dataSource = new CountsDataSource(getActivity());
        adapter = new ItemsAdapter(dataSource.getAllItems());
        View v = inflater.inflate(R.layout.fragment_stats, container, false);
        ListView lv = (ListView) v.findViewById(R.id.statisticListView);
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Item item = adapter.getItem(i);
                new DialogFragment() {
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        getDialog().setTitle(R.string.edit_text);
                        final View rootView = inflater.inflate(R.layout.dialog, container, false);
                        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
                        editText.setText(item.text);
                        Button btnPositive = (Button) rootView.findViewById(R.id.btn_positive);
                        Button btnNegative = (Button) rootView.findViewById(R.id.btn_negative);
                        btnPositive.setText(R.string.save_text);
                        btnPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dataSource.updateItemText(item, editText.getText().toString());
                                dismiss();
                                adapter.refresh(dataSource.getAllItems());
                            }
                        });
                        btnNegative.setText(R.string.delete);
                        btnNegative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dataSource.deleteItem(item);
                                adapter.refresh(dataSource.getAllItems());
                                dismiss();
                            }
                        });
                        return rootView;
                    }
                }.show(getFragmentManager(), "dialog");
                return false;
            }
        });

        return v;
    }
}
