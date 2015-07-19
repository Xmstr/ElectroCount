package com.xmstr.electrocount.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.xmstr.electrocount.R;
import com.xmstr.electrocount.adapter.ItemsAdapter;
import com.xmstr.electrocount.db.CountsDataSource;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by xmast_000 on 11.07.2015.
 */
public class MainFragment extends Fragment {
    private static final SimpleDateFormat ONLY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    CountsDataSource dataSource;
    ItemsAdapter adapter;
    TextView mainNumber;
    TextView mainCost;

    public MainFragment() {
        //EMPTY CONSTRUCTOR
    }


    @Override
    public void onResume() {
        setNumber();
        calculateCost();
        super.onResume();
    }

    private void setNumber() {
        mainNumber.setText(dataSource.getLastItemNumber());
    }

    private void calculateCost() {
        // MAIN COST
        DecimalFormat df = new DecimalFormat("####0.00");
        double cost = Double.parseDouble(dataSource.getLastItemNumber());
        mainCost.setText(String.valueOf(df.format(cost / 7)) + " руб.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        dataSource = new CountsDataSource(getActivity());
        adapter = new ItemsAdapter(dataSource.getAllItems());
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mainCost = (TextView) v.findViewById(R.id.mainCostTextView);
        //FAB
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        // MAIN NUMBER
        mainNumber = (TextView) v.findViewById(R.id.mainNumberTextView);
        setNumber();
        calculateCost();

        fab.setOnClickListener(new View.OnClickListener() {
            public boolean confirmedSame = true;

            @Override
            public void onClick(View view) {
                new DialogFragment() {
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        getDialog().setTitle(R.string.new_count_title);
                        final View rootView = inflater.inflate(R.layout.dialog, container, false);
                        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
                        TextView prevNumber = (TextView) rootView.findViewById(R.id.prevNumber);
                        prevNumber.setText(dataSource.getLastItemNumber());
                        editText.setSelection(editText.length());
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                //editText.setText("00000");
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        Button btnPositive = (Button) rootView.findViewById(R.id.btn_positive);
                        Button btnNegative = (Button) rootView.findViewById(R.id.btn_negative);
                        btnPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (checkNumber(editText)) {
                                    String date = ONLY_DATE_FORMAT.format(System.currentTimeMillis());
                                    dataSource.createItem(editText.getText().toString(), date);
                                    setNumber();
                                    calculateCost();
                                    dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Неправильный ввод", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            }

                            private boolean checkNumber(final EditText text) {
                                if (text.getText() == null
                                        || text.getText().length() == 0
                                        || ((Integer.parseInt(text.getText().toString())) < Integer.parseInt(dataSource.getLastItemNumber())))
                                    return false;
                                else if ((Integer.parseInt(text.getText().toString())) == Integer.parseInt(dataSource.getLastItemNumber())) {
                                    new DialogFragment(){
                                        @Nullable
                                        @Override
                                        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                                            getDialog().setTitle("Уверены?");
                                            final View rootView = inflater.inflate(R.layout.dialog_confirm, container, false);
                                            TextView confirmText = (TextView)rootView.findViewById(R.id.confirmTextView);
                                            confirmText.setText("Показания совпадают. Применить?");
                                            Button btnPositive = (Button) rootView.findViewById(R.id.btn_positive);
                                            Button btnNegative = (Button) rootView.findViewById(R.id.btn_negative);
                                            btnPositive.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    confirmedSame = true;
                                                    Toast.makeText(getActivity(), "Равны", Toast.LENGTH_SHORT).show();
                                                    dismiss();
                                                }
                                            });
                                            btnNegative.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    confirmedSame = false;
                                                    dismiss();
                                                }
                                            });
                                            return rootView;
                                        }
                                    }.show(getFragmentManager(), "dialog_confirm");
                                    return confirmedSame;
                                }
                                else return confirmedSame;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                dataSource.deleteAll();
                setNumber();
                calculateCost();
                adapter.refresh(dataSource.getAllItems());
                Toast.makeText(getActivity(), "Очищено", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_info:
                Toast.makeText(getActivity(), "INFO", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
