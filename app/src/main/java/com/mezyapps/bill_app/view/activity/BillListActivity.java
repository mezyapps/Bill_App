package com.mezyapps.bill_app.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.adapter.BillHDAdapter;
import com.mezyapps.bill_app.adapter.ItemAdapter;
import com.mezyapps.bill_app.api_common.ApiClient;
import com.mezyapps.bill_app.api_common.ApiInterface;
import com.mezyapps.bill_app.database.DatabaseConstant;
import com.mezyapps.bill_app.database.DatabaseHandler;
import com.mezyapps.bill_app.model.BillHDModel;
import com.mezyapps.bill_app.model.LocalDBItemModel;
import com.mezyapps.bill_app.utils.NetworkUtils;
import com.mezyapps.bill_app.utils.ShowProgressDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class BillListActivity extends AppCompatActivity {

    private ImageView iv_back, iv_custom_calender, iv_search, iv_back_search,iv_not_found;
    private LinearLayout linear_layout_custom_day, linear_layout_today_date;
    private String currentDate, currentDateSend;
    private boolean isStartDate;
    private TextView textDateStart, textDateEnd, textDateStartCustom, textDateEndCustom, text_today_date;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private RecyclerView recycler_view_bill_list;
    private RelativeLayout rr_toolbar, rr_toolbar_search;
    private EditText edit_search;
    private ArrayList<BillHDModel> billHDModelArrayList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private BillHDAdapter billHDAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog = new ShowProgressDialog(BillListActivity.this);
        iv_back = findViewById(R.id.iv_back);
        iv_custom_calender = findViewById(R.id.iv_custom_calender);
        linear_layout_custom_day = findViewById(R.id.linear_layout_custom_day);
        linear_layout_today_date = findViewById(R.id.linear_layout_today_date);
        textDateStart = findViewById(R.id.textDateStart);
        textDateEnd = findViewById(R.id.textDateEnd);
        text_today_date = findViewById(R.id.text_today_date);
        iv_search = findViewById(R.id.iv_search);
        recycler_view_bill_list = findViewById(R.id.recycler_view_bill_list);
        rr_toolbar = findViewById(R.id.rr_toolbar);
        rr_toolbar_search = findViewById(R.id.rr_toolbar_search);
        iv_back_search = findViewById(R.id.iv_back_search);
        edit_search = findViewById(R.id.edit_search);
        iv_not_found = findViewById(R.id.iv_not_found);
        databaseHandler = new DatabaseHandler(BillListActivity.this);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BillListActivity.this);
        recycler_view_bill_list.setLayoutManager(linearLayoutManager);


        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentDateSend = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        textDateEnd.setText(currentDate);
        textDateStart.setText(currentDate);

        callAllBillList();
    }

    private void callAllBillList() {
        try {
            String selectQuery = "SELECT  * FROM " + DatabaseConstant.BillHD.TABLE_NAME;

            billHDModelArrayList.clear();
            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.ID));
                String cust_name = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.CUST_NAME));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.BILL_DATE));
                String total_qty = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.TOTAL_QTY));
                String total_amt = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.TOTAL_AMT));

                BillHDModel billHDModel = new BillHDModel();
                billHDModel.setId(id);
                billHDModel.setCust_name(cust_name);
                billHDModel.setDate(date);
                billHDModel.setTotal_qty(total_qty);
                billHDModel.setTotal_amt(total_amt);

                billHDModelArrayList.add(billHDModel);
            }
            if (billHDModelArrayList.size() != 0) {
                Collections.reverse(billHDModelArrayList);
                billHDAdapter = new BillHDAdapter(BillListActivity.this, billHDModelArrayList);
                recycler_view_bill_list.setAdapter(billHDAdapter);
                iv_not_found.setVisibility(View.GONE);
                billHDAdapter.notifyDataSetChanged();
            } else {
                iv_not_found.setVisibility(View.VISIBLE);
                billHDAdapter.notifyDataSetChanged();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callSingleDateFilter(String dateStr)
    {
        try {
            String selectQuery = "SELECT  * FROM " + DatabaseConstant.BillHD.TABLE_NAME +
                    " WHERE DATE_Y_M_D='"+dateStr+"'";

            billHDModelArrayList.clear();
            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.ID));
                String cust_name = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.CUST_NAME));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.BILL_DATE));
                String total_qty = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.TOTAL_QTY));
                String total_amt = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.TOTAL_AMT));

                BillHDModel billHDModel = new BillHDModel();
                billHDModel.setId(id);
                billHDModel.setCust_name(cust_name);
                billHDModel.setDate(date);
                billHDModel.setTotal_qty(total_qty);
                billHDModel.setTotal_amt(total_amt);

                billHDModelArrayList.add(billHDModel);
            }
            if (billHDModelArrayList.size() != 0) {
                Collections.reverse(billHDModelArrayList);
                billHDAdapter = new BillHDAdapter(BillListActivity.this, billHDModelArrayList);
                recycler_view_bill_list.setAdapter(billHDAdapter);
                billHDAdapter.notifyDataSetChanged();
                iv_not_found.setVisibility(View.GONE);
            } else {
                iv_not_found.setVisibility(View.VISIBLE);
                billHDAdapter.notifyDataSetChanged();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void callTwoDateFilter(String dateStr1,String dateStr2)
    {

        try {
            String selectQuery = "SELECT  * FROM " + DatabaseConstant.BillHD.TABLE_NAME +
                    " WHERE DATE_Y_M_D BETWEEN '"+dateStr1+"' AND '"+dateStr2+"'";

            billHDModelArrayList.clear();
            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.ID));
                String cust_name = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.CUST_NAME));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.BILL_DATE));
                String total_qty = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.TOTAL_QTY));
                String total_amt = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillHD.TOTAL_AMT));

                BillHDModel billHDModel = new BillHDModel();
                billHDModel.setId(id);
                billHDModel.setCust_name(cust_name);
                billHDModel.setDate(date);
                billHDModel.setTotal_qty(total_qty);
                billHDModel.setTotal_amt(total_amt);

                billHDModelArrayList.add(billHDModel);
            }
            if (billHDModelArrayList.size() != 0) {
                Collections.reverse(billHDModelArrayList);
                billHDAdapter = new BillHDAdapter(BillListActivity.this, billHDModelArrayList);
                recycler_view_bill_list.setAdapter(billHDAdapter);
                iv_not_found.setVisibility(View.GONE);
                billHDAdapter.notifyDataSetChanged();
            } else {
                iv_not_found.setVisibility(View.VISIBLE);
                billHDAdapter.notifyDataSetChanged();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void events() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_custom_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDateDialog();
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rr_toolbar.setVisibility(View.GONE);
                rr_toolbar_search.setVisibility(View.VISIBLE);
            }
        });

        iv_back_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rr_toolbar_search.setVisibility(View.GONE);
                rr_toolbar.setVisibility(View.VISIBLE);
                edit_search.setText("");
            }
        });

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                billHDAdapter.getFilter().filter(edit_search.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Custom Date Dialog
    private void customDateDialog() {
        final Dialog customDateDialog = new Dialog(BillListActivity.this);
        customDateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDateDialog.setContentView(R.layout.custom_date);

        TextView text_today = customDateDialog.findViewById(R.id.text_today);
        TextView text_yesterday = customDateDialog.findViewById(R.id.text_yesterday);
        TextView text_this_week = customDateDialog.findViewById(R.id.text_this_week);
        TextView text_this_month = customDateDialog.findViewById(R.id.text_this_month);
        TextView text_last_month = customDateDialog.findViewById(R.id.text_last_month);
        final TextView text_custom = customDateDialog.findViewById(R.id.text_custom);


        customDateDialog.setCancelable(true);
        customDateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        customDateDialog.show();

        Window window = customDateDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        //Events Custom Date
        text_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.VISIBLE);
                linear_layout_custom_day.setVisibility(View.GONE);
                customDateDialog.dismiss();
                currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                String SendDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                text_today_date.setText(currentDate);
                callSingleDateFilter(SendDate);
                //   Toast.makeText(SalesReportActivity.this, currentDate, Toast.LENGTH_SHORT).show();
            }
        });
        text_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.VISIBLE);
                linear_layout_custom_day.setVisibility(View.GONE);
                customDateDialog.dismiss();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                String yesterday = dateFormat.format(cal.getTime());
                text_today_date.setText(yesterday);


                DateFormat sendDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calSend = Calendar.getInstance();
                calSend.add(Calendar.DATE, -1);
                String yesterdaySend = sendDateFormat.format(calSend.getTime());
                text_today_date.setText(yesterday);

                callSingleDateFilter(yesterdaySend);
                // Toast.makeText(SalesReportActivity.this, yesterday, Toast.LENGTH_SHORT).show();
            }
        });
        text_this_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.GONE);
                linear_layout_custom_day.setVisibility(View.VISIBLE);
                customDateDialog.dismiss();

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String startDate = dateFormat.format(cal.getTime());
                cal.add(Calendar.DATE, 6);
                String endDate = dateFormat.format(cal.getTime());

                DateFormat dateFormatSend = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calSend = Calendar.getInstance();
                calSend.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String startDateSend = dateFormatSend.format(calSend.getTime());
                calSend.add(Calendar.DATE, 6);
                String endDateSend = dateFormatSend.format(calSend.getTime());

                textDateStart.setText(startDate);
                textDateEnd.setText(endDate);

                callTwoDateFilter(startDateSend,endDateSend);
                // Toast.makeText(SalesReportActivity.this, startDate+" "+endDate, Toast.LENGTH_SHORT).show();
            }

        });
        text_this_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.GONE);
                linear_layout_custom_day.setVisibility(View.VISIBLE);
                customDateDialog.dismiss();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                String startDate = dateFormat.format(cal.getTime());
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DATE, -1);
                String endDate = dateFormat.format(cal.getTime());

                DateFormat dateFormatSend = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calSend = Calendar.getInstance();
                calSend.set(Calendar.DAY_OF_MONTH, 1);
                String startDateSend = dateFormatSend.format(calSend.getTime());
                calSend.add(Calendar.MONTH, 1);
                calSend.add(Calendar.DATE, -1);
                String endDateSend = dateFormatSend.format(calSend.getTime());

                textDateStart.setText(startDate);
                textDateEnd.setText(endDate);
                callTwoDateFilter(startDateSend,endDateSend);
                //Toast.makeText(SalesReportActivity.this, startDate+" "+endDate, Toast.LENGTH_SHORT).show();
            }
        });
        text_last_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.GONE);
                linear_layout_custom_day.setVisibility(View.VISIBLE);
                customDateDialog.dismiss();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Date lastDateOfPreviousMonth = cal.getTime();
                String endDate = dateFormat.format(lastDateOfPreviousMonth);
                cal.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonth = cal.getTime();
                String startDate = dateFormat.format(firstDateOfPreviousMonth);


                DateFormat dateFormatSend = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calSend = Calendar.getInstance();
                calSend.set(Calendar.DATE, 1);
                calSend.add(Calendar.DAY_OF_MONTH, -1);
                Date lastDateOfPreviousMonthSend = calSend.getTime();
                String endDateSend = dateFormatSend.format(lastDateOfPreviousMonthSend);
                calSend.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonthSend = calSend.getTime();
                String startDateSend = dateFormatSend.format(firstDateOfPreviousMonthSend);


                textDateStart.setText(startDate);
                textDateEnd.setText(endDate);
                callTwoDateFilter(endDateSend,startDateSend);
                //Toast.makeText(SalesReportActivity.this, startDate+" "+endDate, Toast.LENGTH_SHORT).show();
            }
        });
        text_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.GONE);
                linear_layout_custom_day.setVisibility(View.VISIBLE);
                customDateDialog.dismiss();
                final Dialog customDateDialogDate = new Dialog(BillListActivity.this);
                customDateDialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customDateDialogDate.setContentView(R.layout.custom_date_calendar);

                textDateStartCustom = customDateDialogDate.findViewById(R.id.textDateStart);
                textDateEndCustom = customDateDialogDate.findViewById(R.id.textDateEnd);
                TextView textAll = customDateDialogDate.findViewById(R.id.textAll);

                currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                textDateStartCustom.setText(currentDate);
                textDateEndCustom.setText(currentDate);

                customDateDialogDate.setCancelable(false);
                customDateDialogDate.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                customDateDialogDate.show();
                Window window = customDateDialogDate.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                textDateStartCustom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isStartDate = true;
                        customDatePickerDialog();
                    }
                });
                textDateEndCustom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isStartDate = false;
                        customDatePickerDialog();
                    }
                });

                textAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDateDialogDate.dismiss();
                        String startDate = textDateStartCustom.getText().toString();
                        String endDate = textDateEndCustom.getText().toString();
                        textDateStart.setText(startDate);
                        textDateEnd.setText(endDate);

                        callTwoDateFilter(startDate,endDate);
                    }
                });
            }
        });
    }

    private void customDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(BillListActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String dateString = format.format(calendar.getTime());

                        if (isStartDate) {
                            textDateStartCustom.setText(dateString);
                        } else {
                            textDateEndCustom.setText(dateString);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        callAllBillList();
    }
}
