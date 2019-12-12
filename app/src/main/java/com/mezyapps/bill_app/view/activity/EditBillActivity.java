package com.mezyapps.bill_app.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.adapter.BillitemAdapter;
import com.mezyapps.bill_app.adapter.ItemAdapter;
import com.mezyapps.bill_app.database.DatabaseConstant;
import com.mezyapps.bill_app.database.DatabaseHandler;
import com.mezyapps.bill_app.model.BillHDModel;
import com.mezyapps.bill_app.model.LocalDBItemModel;
import com.mezyapps.bill_app.utils.SelectBillIItemInterface;
import com.mezyapps.bill_app.utils.SharedLoginUtils;
import com.mezyapps.bill_app.view.fragment.HomeFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditBillActivity extends AppCompatActivity implements SelectBillIItemInterface {

    private ImageView iv_back;
    private TextView textDate, textTotalAmt, textTotalQty;
    private AutoCompleteTextView text_item;
    private EditText edt_qty, edt_rate, edt_amt, edt_party_name;
    private RecyclerView recyclerViewBill;
    private ImageView iv_add;
    private String qty, rate, amt,item;
    private DatabaseHandler databaseHandler;
    private ArrayList<LocalDBItemModel> localDBItemModelArrayList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SelectBillIItemInterface selectBillIItemInterface;
    private String itemId = "", getDate, sendDate;
    String total_amount = "", totalQty = "";
    private ItemAdapter itemAdapter;
    private Button save_bill,delete_bill;
    private BillHDModel billHDModel;
    private String bill_no, date, cust_name, total_amt, total_qty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bill);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        iv_back = findViewById(R.id.iv_back);
        text_item = findViewById(R.id.text_item);
        textDate = findViewById(R.id.textDate);
        edt_qty = findViewById(R.id.edt_qty);
        edt_rate = findViewById(R.id.edt_rate);
        edt_amt = findViewById(R.id.edt_amt);
        recyclerViewBill = findViewById(R.id.recyclerViewBill);
        iv_add = findViewById(R.id.iv_add);
        textTotalAmt = findViewById(R.id.textTotalAmt);
        textTotalQty = findViewById(R.id.textTotalQty);
        edt_party_name = findViewById(R.id.edt_party_name);
        save_bill = findViewById(R.id.save_bill);
        delete_bill = findViewById(R.id.delete_bill);
        databaseHandler = new DatabaseHandler(EditBillActivity.this);

        selectBillIItemInterface = (EditBillActivity.this);

        linearLayoutManager = new LinearLayoutManager(EditBillActivity.this);
        recyclerViewBill.setLayoutManager(linearLayoutManager);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            billHDModel = bundle.getParcelable("BILL");
            bill_no = billHDModel.getId();
            date = billHDModel.getDate();
            cust_name = billHDModel.getCust_name();
            edt_party_name.setText(cust_name);
            textDate.setText(date);

            callGetItemList(bill_no);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            Date data = null;
            try {
                data = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sendDate = output.format(data);
        }
    }

    private void callGetItemList(String bill_no) {
        try {
            String selectQuery = "SELECT  * FROM " + DatabaseConstant.BillDT.TABLE_NAME + " WHERE BILL_ID_DT=" + bill_no;

            localDBItemModelArrayList.clear();
            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            int sr_no = 1;
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.ID));
                String item = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.ITEM));
                String qty = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.QTY));
                String rate = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.RATE));
                String amt = cursor.getString(cursor.getColumnIndex(DatabaseConstant.BillDT.AMOUNT));


                LocalDBItemModel localDBItemModel = new LocalDBItemModel();
                localDBItemModel.setId(id);
                localDBItemModel.setItem(item);
                localDBItemModel.setQty(qty);
                localDBItemModel.setRate(rate);
                localDBItemModel.setAmt(amt);

                localDBItemModel.setSr_no(String.valueOf(sr_no));

                localDBItemModelArrayList.add(localDBItemModel);
            }
            if (localDBItemModelArrayList.size() != 0) {
                callSaveItem();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callSaveItem() {
        deleteTable();
        boolean val = false;
        for (int i = 0; i < localDBItemModelArrayList.size(); i++) {
            String qty = localDBItemModelArrayList.get(i).getQty();
            String item = localDBItemModelArrayList.get(i).getItem();
            String rate = localDBItemModelArrayList.get(i).getRate();
            String amount = localDBItemModelArrayList.get(i).getAmt();
            val = saveBillTemp(item,qty, rate, amount);
        }
        if (val) {
            callListItem();
        } else {
            callListItem();
        }

    }

    private void callListItem() {
        try {
            String selectQuery =
                    "SELECT  *," +
                            "(select sum(AMOUNT) from ITEM_TEMP) as[TOTAL_AMT]," +
                            "(select sum(QTY) from ITEM_TEMP) as[TOTAL_QTY]" +
                            " FROM " + DatabaseConstant.ItemTEMP.TABLE_NAME;

            localDBItemModelArrayList.clear();
            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            int sr_no = 1;
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ItemTEMP.ID));
                String item = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ItemTEMP.ITEM));
                String qty = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ItemTEMP.QTY));
                String rate = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ItemTEMP.RATE));
                String amt = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ItemTEMP.AMOUNT));
                total_amount = cursor.getString(cursor.getColumnIndex("TOTAL_AMT"));
                totalQty = cursor.getString(cursor.getColumnIndex("TOTAL_QTY"));


                LocalDBItemModel localDBItemModel = new LocalDBItemModel();
                localDBItemModel.setId(id);
                localDBItemModel.setItem(item);
                localDBItemModel.setQty(qty);
                localDBItemModel.setRate(rate);
                localDBItemModel.setAmt(amt);

                localDBItemModel.setSr_no(String.valueOf(sr_no));

                localDBItemModelArrayList.add(localDBItemModel);
                sr_no++;
            }
            if (localDBItemModelArrayList.size() != 0) {
                textTotalAmt.setText("Total :" + total_amount);
                textTotalQty.setText("Total :" + totalQty);
                itemAdapter = new ItemAdapter(EditBillActivity.this, localDBItemModelArrayList, selectBillIItemInterface);
                recyclerViewBill.setAdapter(itemAdapter);
                if (itemId.equalsIgnoreCase("")) {
                    linearLayoutManager.scrollToPosition(localDBItemModelArrayList.size() - 1);
                }
                itemAdapter.notifyDataSetChanged();
            } else {
                itemAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveBillTemp(String item,String qty, String rate, String amount) {
        Boolean returnVal = false;
        try {
            long result = 0;

            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.ItemTEMP.ITEM, item);
            contentValues.put(DatabaseConstant.ItemTEMP.QTY, qty);
            contentValues.put(DatabaseConstant.ItemTEMP.RATE, rate);
            contentValues.put(DatabaseConstant.ItemTEMP.AMOUNT, amount);
            result = db.insert(DatabaseConstant.ItemTEMP.TABLE_NAME, null, contentValues);

            if (result == -1) {
                returnVal = false;
                db.close();
            } else {
                returnVal = true;
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnVal;
    }

    public void deleteTable() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.execSQL("delete from " + DatabaseConstant.ItemTEMP.TABLE_NAME);
        db.close();
    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                int qtyInt = Integer.parseInt(qty);
                int rateInt = Integer.parseInt(rate);
                int amount = qtyInt * rateInt;
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
                int qtyInt = Integer.parseInt(qty);
                int rateInt = Integer.parseInt(rate);
                int amount = qtyInt * rateInt;
                amt = String.valueOf(amount);
                edt_amt.setText(amt);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCalendarPicker();
            }
        });

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

        save_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSaveBillHD();
            }
        });
        delete_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EditBillActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete_bill);
                TextView txt_edit = dialog.findViewById(R.id.txt_edit);
                TextView txt_delete = dialog.findViewById(R.id.txt_delete);
                TextView sr_no = dialog.findViewById(R.id.sr_no);
                dialog.setCancelable(false);


                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                txt_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                txt_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callDeleteBill();
                    }
                });
            }
        });
    }

    private void callDeleteBill() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete("BILL_HD", DatabaseConstant.BillHD.ID + "=?", new String[]{bill_no});
        db.delete("BILL_DT", DatabaseConstant.BillDT.ID + "=?", new String[]{bill_no});

        Toast.makeText(this, "Bill Delete Successfully", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void callAddItem() {

        try {
            long result = 0;
            SQLiteDatabase db = databaseHandler.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.ItemTEMP.ITEM, item);
            contentValues.put(DatabaseConstant.ItemTEMP.QTY, qty);
            contentValues.put(DatabaseConstant.ItemTEMP.RATE, rate);
            contentValues.put(DatabaseConstant.ItemTEMP.AMOUNT, amt);
            result = db.insert(DatabaseConstant.ItemTEMP.TABLE_NAME, null, contentValues);

            if (result == -1) {
                db.close();
                Toast.makeText(EditBillActivity.this, "Item Not Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditBillActivity.this, "Item Add Successfully", Toast.LENGTH_SHORT).show();
                text_item.setText("");
                edt_qty.setText("");
                edt_rate.setText("");
                edt_amt.setText("");
                text_item.requestFocus();
                db.close();
                callListItem();
            }
        } catch (Exception e) {
            Toast.makeText(EditBillActivity.this, "Item Not Add", Toast.LENGTH_SHORT).show();
        }
    }

    private void callEditItem() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        String sql = "UPDATE ITEM_TEMP \n" +
                "SET ITEM = ?, \n" +
                "QTY = ?, \n" +
                "RATE = ?, \n" +
                "AMOUNT = ? \n" +
                "WHERE ITEM_ID = ?;\n";

        db.execSQL(sql, new String[]{item,qty, rate, amt, itemId});
        text_item.setText("");
        edt_qty.setText("");
        edt_rate.setText("");
        edt_amt.setText("");
        text_item.requestFocus();
        itemId = "";
        Toast.makeText(EditBillActivity.this, "Update Bill", Toast.LENGTH_SHORT).show();
        callListItem();
    }

    private boolean validation() {
        qty = edt_qty.getText().toString().trim();
        rate = edt_rate.getText().toString().trim();
        item = text_item.getText().toString().trim();
        if (item.equalsIgnoreCase("")) {
            edt_qty.setError("Enter Item");
            edt_qty.requestFocus();
            return false;
        }else if (qty.equalsIgnoreCase("")) {
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

    private void callCalendarPicker() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(EditBillActivity.this,
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
        db.delete("ITEM_TEMP", DatabaseConstant.ItemTEMP.ID + "=?", new String[]{id});
        itemId = "";
        localDBItemModelArrayList.clear();
        textTotalAmt.setText("");
        textTotalQty.setText("");
        callListItem();
    }

    private void callSaveBillHD() {
        try {
            if (localDBItemModelArrayList.size() != 0) {
                String cust_name = edt_party_name.getText().toString().trim();
                String date = textDate.getText().toString().trim();


                SQLiteDatabase db = databaseHandler.getWritableDatabase();
                String sql = "UPDATE BILL_HD \n" +
                        "SET CUST_NAME = ?, \n" +
                        "BILL_DATE = ?, \n" +
                        "DATE_Y_M_D = ?, \n" +
                        "TOTAL_QTY = ?, \n" +
                        "TOTAL_AMT = ? \n" +
                        "WHERE BILL_ID = ?;\n";

                db.execSQL(sql, new String[]{cust_name, date, sendDate, totalQty, total_amount, bill_no});
                callSaveBillDT();
            } else {
                Toast.makeText(this, "No Item Added", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(EditBillActivity.this, "Item Not Add", Toast.LENGTH_SHORT).show();
        }
    }

    private void callSaveBillDT() {


        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        db.delete("BILL_DT", DatabaseConstant.BillDT.ID + "=?", new String[]{bill_no});
        callListItem();

        boolean val = false;
        for (int i = 0; i < localDBItemModelArrayList.size(); i++) {
            String item = localDBItemModelArrayList.get(i).getItem();
            String qty = localDBItemModelArrayList.get(i).getQty();
            String rate = localDBItemModelArrayList.get(i).getRate();
            String amount = localDBItemModelArrayList.get(i).getAmt();
            val = saveBillDTCall(item,qty, rate, amount);
        }
        if (val) {
            deleteTable();
            Toast.makeText(EditBillActivity.this, "Bill Update Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditBillActivity.this, BillPreviewSaveActivity.class);
            int billInt = Integer.parseInt(bill_no);
            intent.putExtra("BILL", billInt);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(EditBillActivity.this, "Bill Not Update", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean saveBillDTCall(String item,String qty, String rate, String amount) {
        Boolean returnVal = false;
        try {
            long result = 0;

            SQLiteDatabase db = databaseHandler.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.BillDT.ID, bill_no);
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
            Toast.makeText(EditBillActivity.this, "Item Not Add", Toast.LENGTH_SHORT).show();
        }
        return returnVal;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        textTotalAmt.setText("");
        textTotalQty.setText("");
        callListItem();
    }
}
