package com.xmstr.electrocount.fragments;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String prepareNumber(String number){
        String preparedNumber = "00000";
        switch (number.length()){
            case 1:
                preparedNumber = "0000"+number;
                break;
            case 2:
                preparedNumber = "000"+number;
                break;
            case 3:
                preparedNumber = "00"+number;
                break;
            case 4:
                preparedNumber = "0"+number;
                break;
            case 5:
                preparedNumber = number;
                break;
            default:
                break;
        }
        return preparedNumber;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        dataSource = new CountsDataSource(getActivity());
        adapter = new ItemsAdapter(dataSource.getAllItems());
        View v = inflater.inflate(R.layout.fragment_stats, container, false);
        ListView lv = (ListView) v.findViewById(R.id.statisticListView);
        lv.setAdapter(adapter);
        lv.setEmptyView(v.findViewById(R.id.emptyListText));
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Item item = adapter.getItem(i);
                new DialogFragment() {
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        getDialog().setTitle(R.string.edit_text);
                        final View rootView = inflater.inflate(R.layout.dialog_edit, container, false);
                        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
                        editText.setText(item.text);
                        editText.setSelection(editText.length());
                        Button btnPositive = (Button) rootView.findViewById(R.id.btn_positive);
                        Button btnNegative = (Button) rootView.findViewById(R.id.btn_negative);
                        btnPositive.setText(R.string.save_text);
                        btnPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dataSource.updateItemText(item, prepareNumber(editText.getText().toString()));
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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Удаление статистики").setMessage("Очистить показания?").setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataSource.deleteAll();
                        adapter.refresh(dataSource.getAllItems());
                        Toast.makeText(getActivity(), "Очищено", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Отмена", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
                break;
            case R.id.action_info:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                builder2.setTitle("О приложении").setMessage("Электросчетчик v.1.1").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
                break;
            case R.id.action_changeprice:
                if (dataSource.checkForPrice()) {
                    new DialogFragment() {
                        @Override
                        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                            getDialog().setTitle(R.string.edit_tariff_text);
                            final View rootView = inflater.inflate(R.layout.dialog_editprice, container, false);
                            final EditText editText = (EditText) rootView.findViewById(R.id.editText);
                            TextView prevTariff = (TextView) rootView.findViewById(R.id.prevTariff);
                            prevTariff.setText(dataSource.getLastPrice());
                            editText.setSelection(editText.length());
                            Button btnPositive = (Button) rootView.findViewById(R.id.btn_positive);
                            Button btnNegative = (Button) rootView.findViewById(R.id.btn_negative);
                            btnPositive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (checkPrice(editText)) {
                                        dataSource.updatePrice(editText.getText().toString());
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Неправильный тариф", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                private boolean checkPrice(final EditText text) {
                                    if (text.getText() == null
                                            || text.getText().length() == 0)
                                        return false;
                                    else return true;
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
                } else {
                    Toast.makeText(getActivity(), "Тариф еще не введен", Toast.LENGTH_SHORT).show();
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
