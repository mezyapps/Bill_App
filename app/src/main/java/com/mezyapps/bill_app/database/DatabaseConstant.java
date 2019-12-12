package com.mezyapps.bill_app.database;

public class DatabaseConstant {

    public static final String DATABASE_NAME = "BILLAPP";
    public static final int DATABASE_VERSION = 1;

    public static class Item
    {
        public static final String ID="ITEM_ID";
        public static final String ITEM="ITEM";
        public static final String QTY="QTY";
        public static final String RATE="RATE";
        public static final String AMOUNT="AMOUNT";
        public static final String TABLE_NAME="BILL_TBL";

        public static final String BILL_TABLE = "CREATE TABLE " +TABLE_NAME+ "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ITEM + " TEXT,"+ QTY + " TEXT," +RATE+ " TEXT," + AMOUNT + " TEXT"+ ")";
    }

    public static class ItemTEMP
    {
        public static final String ID="ITEM_ID";
        public static final String ITEM="ITEM";
        public static final String QTY="QTY";
        public static final String RATE="RATE";
        public static final String AMOUNT="AMOUNT";
        public static final String TABLE_NAME="ITEM_TEMP";


        public static final String BILL_TABLE = "CREATE TABLE " +TABLE_NAME+ "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ITEM + " TEXT,"+ QTY + " TEXT," +RATE+ " TEXT," + AMOUNT + " TEXT"+ ")";
    }

    public static class BillHD
    {
        public static final String ID="BILL_ID";
        public static final String CUST_NAME="CUST_NAME";
        public static final String BILL_DATE="BILL_DATE";
        public static final String DATE_Y_M_D="DATE_Y_M_D";
        public static final String TABLE_NAME="BILL_HD";
        public static final String TOTAL_QTY="TOTAL_QTY";
        public static final String TOTAL_AMT="TOTAL_AMT";

        public static final String BILL_HD = "CREATE TABLE " +TABLE_NAME+ "(" + ID + " INTEGER PRIMARY KEY,"
                + CUST_NAME + " TEXT," +BILL_DATE+ " TEXT," +DATE_Y_M_D+ " TEXT,"+ TOTAL_QTY+ " TEXT,"+TOTAL_AMT+ " TEXT"+")";
    }


    public static class BillDT
    {
        public static final String ID="BILL_ID_DT";
        public static final String ITEM="ITEM";
        public static final String QTY="QTY";
        public static final String RATE="RATE";
        public static final String AMOUNT="AMOUNT";
        public static final String TABLE_NAME="BILL_DT";

        public static final String BILL_TD = "CREATE TABLE " +TABLE_NAME+ "(" + ID + " INTEGER,"
                + ITEM + " TEXT,"+ QTY + " TEXT," +RATE+ " TEXT," + AMOUNT + " TEXT"+")";
    }


    public static class ItemName
    {
        public static final String ID="ITEM_ID";
        public static final String ITEM="ITEM_NAME";
        public static final String TABLE_NAME="ItemName";

        public static final String TEMP_NAME = "CREATE TABLE " +TABLE_NAME+ "(" + ID + " INTEGER,"
                + ITEM + " TEXT"+")";
    }
}
