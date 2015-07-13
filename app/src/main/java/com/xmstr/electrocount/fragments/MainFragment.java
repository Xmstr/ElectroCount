package com.xmstr.electrocount.fragments;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import com.github.clans.fab.FloatingActionButton;
import com.xmstr.electrocount.R;
import com.xmstr.electrocount.adapter.ItemsAdapter;
import com.xmstr.electrocount.db.CountsDataSource;

/**
 * Created by xmast_000 on 11.07.2015.
 */
public class MainFragment extends Fragment {
    private static final SimpleDateFormat ONLY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    CountsDataSource dataSource;
    ItemsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataSource = new CountsDataSource(getActivity());
        adapter = new ItemsAdapter(dataSource.getAllItems());
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        //FAB
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);

        // MAIN NUMBER
        final TextView mainNumber = (TextView)v.findViewById(R.id.mainNumberTextView);
        mainNumber.setText(dataSource.getLastItemNumber());
        // MAIN COST
        DecimalFormat df = new DecimalFormat("####0.00");
        TextView mainCost = (TextView)v.findViewById(R.id.mainCostTextView);
        double cost = Double.parseDouble(dataSource.getLastItemNumber());
        mainCost.setText(String.valueOf(df.format(cost/7)) + " руб.");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogFragment() {
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        getDialog().setTitle(R.string.new_count_title);
                        final View rootView = inflater.inflate(R.layout.dialog, container, false);
                        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
                        Button btnPositive = (Button) rootView.findViewById(R.id.btn_positive);
                        Button btnNegative = (Button) rootView.findViewById(R.id.btn_negative);
                        btnPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String date = ONLY_DATE_FORMAT.format(System.currentTimeMillis());
                                dataSource.createItem(editText.getText().toString(), date);
                                mainNumber.setText(editText.getText().toString());
                                dismiss();
                            }
                        });
                        btnNegative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dismiss();
                            }
                        });
                        return rootView;
                    }
                }.show(getFragmentManager(), "dialog");
            }
        });

        ImageButton statsButton = (ImageButton) v.findViewById(R.id.statisticButton);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction().replace(R.id.content_frame, new StatisticFragment(), StatisticFragment.class.getSimpleName())
                        .addToBackStack(null).commit();
            }
        });
        return v;
    }

}
