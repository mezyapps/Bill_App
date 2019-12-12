package com.mezyapps.bill_app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(@Nullable Context context) {
        super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstant.Item.BILL_TABLE);
        db.execSQL(DatabaseConstant.BillHD.BILL_HD);
        db.execSQL(DatabaseConstant.BillDT.BILL_TD);
        db.execSQL(DatabaseConstant.ItemTEMP.BILL_TABLE);
        db.execSQL(DatabaseConstant.ItemName.ITEM_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.Item.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.BillDT.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.BillHD.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.ItemTEMP.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.ItemName.TABLE_NAME);
    }
}
