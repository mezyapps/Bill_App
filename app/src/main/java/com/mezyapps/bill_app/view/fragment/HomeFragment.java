package com.mezyapps.bill_app.view.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.adapter.ItemAdapter;
import com.mezyapps.bill_app.api_common.ApiClient;
import com.mezyapps.bill_app.api_common.ApiInterface;
import com.mezyapps.bill_app.database.DatabaseConstant;
import com.mezyapps.bill_app.database.DatabaseHandler;
import com.mezyapps.bill_app.model.LocalDBItemModel;
import com.mezyapps.bill_app.model.SuccessModel;
import com.mezyapps.bill_app.utils.SelectBillIItemInterface;
import com.mezyapps.bill_app.utils.SharedLoginUtils;
import com.mezyapps.bill_app.utils.ShowProgressDialog;
import com.mezyapps.bill_app.view.activity.BillPerviewActivity;
import com.mezyapps.bill_app.view.activity.BillPreviewSaveActivity;
import com.mezyapps.bill_app.view.activity.LoginActivity;
import com.mezyapps.bill_app.view.activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SelectBillIItemInterface {
    private Context mContext;
    private TextView textDate, textTotalAmt, textTotalQty;
    private AutoCompleteTextView text_item;
    private EditText edt_qty, edt_rate, edt_amt, edt_party_name;
    private RecyclerView recyclerViewBill;
    private ImageView iv_add;
    private String qty, rate, amt;
    private DatabaseHandler databaseHandler;
    private ArrayList<LocalDBItemModel> localDBItemModelArrayList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SelectBillIItemInterface selectBillIItemInterface;
    private String itemId = "", getDate, sendDate,Item="";
    String total_amount = "", totalQty = "";
    private ItemAdapter itemAdapter;
    private Button save_bill;
    public static boolean isToRefresh = false;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private String user_id;
    private Dialog dialog_check_user_session;
    private HashSet<String> stringHashSet=new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        find_View_IDS(view);
        events();
        return view;
    }

    private void find_View_IDS(View view) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        textDate = view.findViewById(R.id.textDate);
        edt_qty = view.findViewById(R.id.edt_qty);
        edt_rate = view.findViewById(R.id.edt_rate);
        text_item = view.findViewById(R.id.text_item);
        edt_amt = view.findViewById(R.id.edt_amt);
        recyclerViewBill = view.findViewById(R.id.recyclerViewBill);
        iv_add = view.findViewById(R.id.iv_add);
        textTotalAmt = view.findViewById(R.id.textTotalAmt);
        textTotalQty = view.findViewById(R.id.textTotalQty);
        edt_party_name = view.findViewById(R.id.edt_party_name);
        save_bill = view.findViewById(R.id.save_bill);
        databaseHandler = new DatabaseHandler(mContext);
        showProgressDialog = new ShowProgressDialog(mContext);

        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerViewBill.setLayoutManager(linearLayoutManager);

        selectBillIItemInterface = (HomeFragment.this);

        getDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        sendDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String date = getDate;
        textDate.setText(date);
        user_id = SharedLoginUtils.getUserId(mContext);
        text_item.requestFocus();
        callListItemName();
        callCheckSession();
        callListItem();
    }

    private void callCheckSession() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.checkSession(user_id);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("0")) {
                                openSessionExpiredDialog(message, code);
                            }
                            if (code.equalsIgnoreCase("2")) {
                                openSessionExpiredDialog(message, code);
                            }

                        } else {
                            Log.d("Tag", "Response Null");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                showProgressDialog.dismissDialog();
            }
        });
    }

    private void openSessionExpiredDialog(String message, String code) {
        dialog_check_user_session = new Dialog(mContext);
        dialog_check_user_session.setContentView(R.layout.dialog_user_session);
        Button btn_close = dialog_check_user_session.findViewById(R.id.btn_close);
        final TextView textCall = dialog_check_user_session.findViewById(R.id.textCall);
        TextView textMsg = dialog_check_user_session.findViewById(R.id.textMsg);
        textMsg.setText(message);

        if (code.equalsIgnoreCase("2")) {
            dialog_check_user_session.setCancelable(true);
            btn_close.setVisibility(View.VISIBLE);
        } else {
            dialog_check_user_session.setCancelable(false);
            btn_close.setVisibility(View.GONE);
        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_check_user_session.dismiss();
            }
        });
        textCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = textCall.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
        Window windowUser = dialog_check_user_session.getWindow();
        windowUser.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog_check_user_session.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void events() {
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (itemId.equalsIgnoreCase("")) {
                        callAddItem();
                    } else {
                        callEditItem();
                    }
                }
            }
        });
        edt_rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qty = edt_qty.getText().toString().trim();
                rate = edt_rate.getText().toString().trim();
                if (qty.equalsIgnoreCase("")) {
                    qty = "0";

                }
                if (rate.equalsIgnoreCase("")) {
                    rate = "0";
                }
                Double qtyInt = Double.parseDouble(qty);
                Double rateInt = Double.parseDouble(rate);
                Double amount = qtyInt * rateInt;
                amt = String.valueOf(amount);
                edt_amt.setText(amt);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qty = edt_qty.getText().toString().trim();
                rate = edt_rate.getText().toString().trim();
                if (qty.equalsIgnoreCase("")) {
                    qty = "0";

                }
                if (rate.equalsIgnoreCase("")) {
                    rate = "0";
                }
                Double qtyInt = Double.parseDouble(qty);
                Double rateInt = Double.parseDouble(rate);
                Double amount = qtyInt * rateInt;
                amt = String.valueOf(amount);
                edt_amt.setText(amt);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        save_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSaveBill();
            }
        });

        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCalendarPicker();
            }
        });

        text_item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                text_item.showDropDown();
                return false;
            }
        });
    }

    private void callListItemName() {
        try {
            String selectQuery = "SELECT  * FROM " + DatabaseConstant.ItemName.TABLE_NAME;

            stringHashSet.clear();
            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);


            while (cursor.moveToNext()) {
                String itemName = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ItemName.ITEM));
                stringHashSet.add(itemName);
            }
            if (stringHashSet.size() != 0) {
                ArrayList<String> arrayList=new ArrayList<>();
                arrayList.addAll(stringHashSet);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, arrayList);
                text_item.setThreshold(1);
                text_item.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callAddItem() {

        try {
            long result = 0;
            SQLiteDatabase db = databaseHandler.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.Item.ITEM, Item);
            contentValues.put(DatabaseConstant.Item.QTY, qty);
            contentValues.put(DatabaseConstant.Item.RATE, rate);
            contentValues.put(DatabaseConstant.Item.AMOUNT, amt);
            result = db.insert(DatabaseConstant.Item.TABLE_NAME, null, contentValues);

            if (result == -1) {
                db.close();
                Toast.makeText(mContext, "Item Not Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Item Add Successfully", Toast.LENGTH_SHORT).show();
                text_item.setText("");
                edt_qty.setText("");
                edt_rate.setText("");
                edt_amt.setText("");
                text_item.requestFocus();
                db.close();
                callAddItemName(Item);
                callListItem();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Item Not Add", Toast.LENGTH_SHORT).show();
        }
    }

    private void callAddItemName(String item) {
        try {

            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.ItemName.ITEM, item);
            db.insert(DatabaseConstant.ItemName.TABLE_NAME, null, contentValues);
            callListItemName();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callEditItem() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        String sql = "UPDATE BILL_TBL \n" +
                "SET ITEM = ?, \n" +
                "QTY = ?, \n" +
                "RATE = ?, \n" +
                "AMOUNT = ? \n" +
                "WHERE ITEM_ID = ?;\n";

        db.execSQL(sql, new String[]{Item,qty, rate, amt, itemId});
        text_item.setText("");
        edt_qty.setText("");
        edt_rate.setText("");
        edt_amt.setText("");
        text_item.requestFocus();
        itemId = "";
        Toast.makeText(mContext, "Update Bill", Toast.LENGTH_SHORT).show();
        callListItem();
    }

    private void callListItem() {
        try {
            String selectQuery =
                    "SELECT  *," +
                            "(select sum(AMOUNT) from BILL_TBL) as[TOTAL_AMT]," +
                            "(select sum(QTY) from BILL_TBL) as[TOTAL_QTY]" +
                            " FROM " + DatabaseConstant.Item.TABLE_NAME;

            localDBItemModelArrayList.clear();
            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            int sr_no = 1;
            while (cursor.moveToNext()) {
                String item = cursor.getString(cursor.getColumnIndex(DatabaseConstant.Item.ITEM));
                String id = cursor.getString(cursor.getColumnIndex(DatabaseConstant.Item.ID));
                String qty = cursor.getString(cursor.getColumnIndex(DatabaseConstant.Item.QTY));
                String rate = cursor.getString(cursor.getColumnIndex(DatabaseConstant.Item.RATE));
                String amt = cursor.getString(cursor.getColumnIndex(DatabaseConstant.Item.AMOUNT));
                total_amount = cursor.getString(cursor.getColumnIndex("TOTAL_AMT"));
                totalQty = cursor.getString(cursor.getColumnIndex("TOTAL_QTY"));


                LocalDBItemModel localDBItemModel = new LocalDBItemModel();
                localDBItemModel.setId(id);
                localDBItemModel.setQty(qty);
                localDBItemModel.setRate(rate);
                localDBItemModel.setAmt(amt);
                localDBItemModel.setItem(item);

                localDBItemModel.setSr_no(String.valueOf(sr_no));

                localDBItemModelArrayList.add(localDBItemModel);
                sr_no++;
            }
            if (localDBItemModelArrayList.size() != 0) {
                textTotalAmt.setText("Total :" + total_amount);
                textTotalQty.setText("Total :" + totalQty);
                itemAdapter = new ItemAdapter(mContext, localDBItemModelArrayList, selectBillIItemInterface);
                recyclerViewBill.setAdapter(itemAdapter);
                if (itemId.equalsIgnoreCase("")) {
                    linearLayoutManager.scrollToPosition(localDBItemModelArrayList.size() - 1);
                }
                itemAdapter.notifyDataSetChanged();
            }
            else
            {
                itemAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callSaveBill() {
        try {
            String selectQuery = "SELECT max(BILL_ID) as id FROM " + DatabaseConstant.BillHD.TABLE_NAME;
            SQLiteDatabase database = databaseHandler.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);

            cursor.moveToFirst();

            String maxid = cursor.getString(cursor.getColumnIndex("id"));
            int maxValue = 0;
            if (maxid == null) {
                maxid = "1";
                maxValue = Integer.parseInt(maxid);
            } else {
                maxValue = Integer.parseInt(maxid);
                maxValue++;
            }

            callSaveBillHD(maxValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callSaveBillHD(int maxValue) {
        try {

            String cust_name = edt_party_name.getText().toString().trim();
            String date = textDate.getText().toString().trim();
            long result = 0;

            SQLiteDatabase db = databaseHandler.getWritableDatabase();

            if (localDBItemModelArrayList.size() != 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseConstant.BillHD.ID, maxValue);
                contentValues.put(DatabaseConstant.BillHD.CUST_NAME, cust_name);
                contentValues.put(DatabaseConstant.BillHD.BILL_DATE, date);
                contentValues.put(DatabaseConstant.BillHD.DATE_Y_M_D, sendDate);
                contentValues.put(DatabaseConstant.BillHD.TOTAL_QTY, totalQty);
                contentValues.put(DatabaseConstant.BillHD.TOTAL_AMT, total_amount);
                result = db.insert(DatabaseConstant.BillHD.TABLE_NAME, null, contentValues);

                if (result == -1) {
                    Toast.makeText(mContext, "Bill Not Save", Toast.LENGTH_SHORT).show();
                    db.close();
                } else {
                    callSaveBillDT(maxValue);
                    db.close();
                }
            } else {
                Toast.makeText(mContext, "please Add Bill Item", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Item Not Add", Toast.LENGTH_SHORT).show();
        }
    }

    private void callSaveBillDT(int maxValue) {

        boolean val = false;
        for (int i = 0; i < localDBItemModelArrayList.size(); i++) {
            String item = localDBItemModelArrayList.get(i).getItem();
            String qty = localDBItemModelArrayList.get(i).getQty();
            String rate = localDBItemModelArrayList.get(i).getRate();
            String amount = localDBItemModelArrayList.get(i).getAmt();
            val = saveBillDTCall(maxValue,item, qty, rate, amount);
        }
        if (val) {
            edt_party_name.setText("");
            deleteTable();
            Toast.makeText(mContext, "Bill Create Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, BillPreviewSaveActivity.class);
            intent.putExtra("BILL", maxValue);
            startActivity(intent);

        } else {
            Toast.makeText(mContext, "Bill Not Save", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean saveBillDTCall(int maxValue,String item, String qty, String rate, String amount) {
        Boolean returnVal = false;
        try {
            long result = 0;

            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.BillDT.ID, maxValue);
            contentValues.put(DatabaseConstant.BillDT.ITEM, item);
            contentValues.put(DatabaseConstant.BillDT.QTY, qty);
            contentValues.put(DatabaseConstant.BillDT.RATE, rate);
            contentValues.put(DatabaseConstant.BillDT.AMOUNT, amount);
            result = db.insert(DatabaseConstant.BillDT.TABLE_NAME, null, contentValues);

            if (result == -1) {
                returnVal = false;
                db.close();
            } else {
                returnVal = true;
                db.close();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Item Not Add", Toast.LENGTH_SHORT).show();
        }
        return returnVal;
    }

    private boolean validation() {
        qty = edt_qty.getText().toString().trim();
        rate = edt_rate.getText().toString().trim();
        Item = text_item.getText().toString().trim();
        if (qty.equalsIgnoreCase("")) {
            edt_qty.setError("Enter Qty");
            edt_qty.requestFocus();
            return false;
        } else if (rate.equalsIgnoreCase("")) {
            edt_rate.setError("Enter Rate");
            edt_rate.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void getSelectItemEdit(LocalDBItemModel localDBItemModel) {
        text_item.setText(localDBItemModel.getItem());
        edt_qty.setText(localDBItemModel.getQty());
        edt_amt.setText(localDBItemModel.getAmt());
        edt_rate.setText(localDBItemModel.getRate());
        itemId = localDBItemModel.getId();
    }

    @Override
    public void getSelectItemDelete(String id) {
        itemId = id;
        deleteItem(id);
    }

    private void deleteItem(String id) {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete("BILL_TBL", DatabaseConstant.Item.ID + "=?", new String[]{id});
        itemId = "";
        localDBItemModelArrayList.clear();
        textTotalAmt.setText("");
        textTotalQty.setText("");
        callListItem();
    }
    public void deleteTable() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.execSQL("delete from " + DatabaseConstant.Item.TABLE_NAME);
        db.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isToRefresh) {
            isToRefresh = false;
            textTotalAmt.setText("");
            textTotalQty.setText("");
            callListItem();
        }
    }

    private void callCalendarPicker() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String dateString = format.format(calendar.getTime());
                        String date = dateString;
                        textDate.setText(date);

                        SimpleDateFormat formatSendDate = new SimpleDateFormat("yyyy-MM-dd");
                        String dateSendString = formatSendDate.format(calendar.getTime());
                        sendDate = dateSendString;
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
