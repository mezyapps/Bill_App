package com.mezyapps.bill_app.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SuccessModel {
    private String message;
    private String code;


    @SerializedName("user_list")
    private ArrayList<UserModel> userModelArrayList;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<UserModel> getUserModelArrayList() {
        return userModelArrayList;
    }

    public void setUserModelArrayList(ArrayList<UserModel> userModelArrayList) {
        this.userModelArrayList = userModelArrayList;
    }
}
