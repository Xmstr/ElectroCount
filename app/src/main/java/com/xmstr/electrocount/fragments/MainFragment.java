package com.xmstr.electrocount.fragments;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.xmstr.electrocount.AlarmReceiver;
import com.xmstr.electrocount.R;
import com.xmstr.electrocount.adapter.ItemsAdapter;
import com.xmstr.electrocount.db.CountsDataSource;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Xmstr. Thanks to Dany Win
 */
public class MainFragment extends Fragment {
    private static final SimpleDateFormat ONLY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    CountsDataSource dataSource;
    ItemsAdapter adapter;
    TextView mainNumber;
    TextView differenceNumber;
    TextView mainCost;
    TextView adviceText;
    int difNumber;

    public MainFragment() {
        //EMPTY CONSTRUCTOR
    }


    @Override
    public void onResume() {
        setNumber();
        calculateCost();
        setAdvice();
        super.onResume();
    }

    private String prepareNumber(String number) {
        String preparedNumber = "00000";
        switch (number.length()) {
            case 1:
                preparedNumber = "0000" + number;
                break;
            case 2:
                preparedNumber = "000" + number;
                break;
            case 3:
                preparedNumber = "00" + number;
                break;
            case 4:
                preparedNumber = "0" + number;
                break;
            case 5:
                preparedNumber = number;
                break;
            default:
                break;
        }
        return preparedNumber;

    }

    private void setNumber() {
        mainNumber.setText(prepareNumber(dataSource.getLastItemNumber()));
        difNumber = (Integer.parseInt(dataSource.getLastItemNumber())) - (Integer.parseInt(dataSource.getPrevItemNumber()));
        differenceNumber.setText(String.valueOf(difNumber));
    }

    private void setAdvice() {
        adviceText.setText(makeAdvice());
    }

    private String makeAdvice() {
        List<String> advices = Arrays.asList(getResources().getStringArray(R.array.advices_array));
        return advices.get(new Random().nextInt(advices.size()));
    }


    private void calculateCost() {
        // MAIN COST
        DecimalFormat df = new DecimalFormat("####0.00");
        double current = Double.parseDouble(dataSource.getLastItemNumber());
        double prev = Double.parseDouble(dataSource.getPrevItemNumber());
        double price = Double.parseDouble(dataSource.getLastPrice());
        mainCost.setText(String.valueOf(df.format((current - prev) * price)) + " руб.");
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
        adviceText = (TextView) v.findViewById(R.id.adviceText);
        differenceNumber = (TextView) v.findViewById(R.id.mainDifferenceNumber);
        setNumber();
        calculateCost();
        setAdvice();
        // LISTENER
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataSource.checkForPrice()) {
                    new DialogFragment() {
                        @Override
                        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                            getDialog().setTitle(R.string.new_count_title);
                            final View rootView = inflater.inflate(R.layout.dialog, container, false);
                            final EditText editText = (EditText) rootView.findViewById(R.id.editText);
                            TextView prevNumber = (TextView) rootView.findViewById(R.id.prevNumber);
                            prevNumber.setText(prepareNumber(dataSource.getLastItemNumber()));
                            editText.setSelection(editText.length());
                            Button btnPositive = (Button) rootView.findViewById(R.id.btn_positive);
                            Button btnNegative = (Button) rootView.findViewById(R.id.btn_negative);
                            btnPositive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (checkNumber(editText)) {
                                        String date = ONLY_DATE_FORMAT.format(System.currentTimeMillis());
                                        dataSource.createItem(prepareNumber(editText.getText().toString()), date);
                                        setNumber();
                                        calculateCost();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Неверный показатель", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                private boolean checkNumber(final EditText text) {
                                    return !(text.getText() == null
                                            || text.getText().length() == 0
                                            || ((Integer.parseInt(text.getText().toString())) <= Integer.parseInt(dataSource.getLastItemNumber())));
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
                    new DialogFragment() {
                        @Override
                        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                            getDialog().setTitle(R.string.first_count_title);
                            final View rootView = inflater.inflate(R.layout.dialog_withprice, container, false);
                            final EditText editText = (EditText) rootView.findViewById(R.id.editText);
                            final EditText tariffEditText = (EditText) rootView.findViewById(R.id.tariffEditText);
                            editText.setSelection(editText.length());
                            Button btnPositive = (Button) rootView.findViewById(R.id.btn_positive);
                            Button btnNegative = (Button) rootView.findViewById(R.id.btn_negative);
                            btnPositive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (checkNumber(editText) && checkPrice(tariffEditText)) {
                                        String date = ONLY_DATE_FORMAT.format(System.currentTimeMillis());
                                        dataSource.createItem(prepareNumber(editText.getText().toString()), date);
                                        dataSource.createPrice(tariffEditText.getText().toString());
                                        setNumber();
                                        calculateCost();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Неверный показатель", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                private boolean checkNumber(final EditText text) {
                                    return !(text.getText() == null
                                            || text.getText().length() == 0
                                            || ((Integer.parseInt(text.getText().toString())) <= Integer.parseInt(dataSource.getLastItemNumber())));
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

                        private boolean checkPrice(EditText text) {
                            return !(text.getText() == null
                                    || text.getText().length() == 0);
                        }
                    }.show(getFragmentManager(), "dialog");
                }
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
        ImageButton adviceButton = (ImageButton) v.findViewById(R.id.adviceButton);
        adviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdvice();
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
        PackageManager pm;
        ComponentName componentName;
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Удаление статистики").setMessage("Очистить показания?").setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataSource.deleteAll();
                        setNumber();
                        calculateCost();
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
                builder2.setTitle("О приложении").setMessage("Электросчетчик v.1.2").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
                break;
            case R.id.action_stopnoti:
                pm = getActivity().getPackageManager();
                componentName = new ComponentName(getActivity(), AlarmReceiver.class);
                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
                Toast.makeText(getActivity(), "Уведомления отключены", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_startnoti:
                pm = getActivity().getPackageManager();
                componentName = new ComponentName(getActivity(), AlarmReceiver.class);
                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
                Intent broadcast = new Intent(getActivity(), AlarmReceiver.class);
                getActivity().sendBroadcast(broadcast);
                Toast.makeText(getActivity(), "Уведомления включены", Toast.LENGTH_LONG).show();
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
                                        setNumber();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), "Неправильный тариф", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                private boolean checkPrice(final EditText text) {
                                    return !(text.getText() == null
                                            || text.getText().length() == 0);
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (isNotiEnabled()) {
            menu.findItem(R.id.action_startnoti).setVisible(false);
            menu.findItem(R.id.action_stopnoti).setVisible(true);
        } else {
            menu.findItem(R.id.action_startnoti).setVisible(true);
            menu.findItem(R.id.action_stopnoti).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    private boolean isNotiEnabled() {
        PackageManager pm = getActivity().getPackageManager();
        Intent testIntent = new Intent(getActivity(), AlarmReceiver.class);
        List<ResolveInfo> resolveInfo = pm.queryBroadcastReceivers(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.size() > 0;
    }
}
