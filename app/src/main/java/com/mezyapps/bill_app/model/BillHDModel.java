package com.mezyapps.bill_app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BillHDModel implements Parcelable {
    String id;
    String cust_name;
    String date;
    String total_qty;
    String total_amt;


    public static  final Parcelable.Creator CREATOR=new Parcelable.Creator()
    {


        @Override
        public BillHDModel createFromParcel(Parcel source) {
            return new BillHDModel(source);
        }

        @Override
        public BillHDModel[] newArray(int size) {
            return new BillHDModel[0];
        }
    };

    public BillHDModel() {
    }

    public BillHDModel(Parcel source) {
        this.id=source.readString();
        this.cust_name = source.readString();
        this.date = source.readString();
        this.total_amt = source.readString();
        this.total_qty = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.cust_name);
        dest.writeString(this.date);
        dest.writeString(this.total_amt);
        dest.writeString(this.total_qty);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(String total_qty) {
        this.total_qty = total_qty;
    }

    public String getTotal_amt() {
        return total_amt;
    }

    public void setTotal_amt(String total_amt) {
        this.total_amt = total_amt;
    }
}
