package com.mezyapps.bill_app.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.api_common.ApiClient;
import com.mezyapps.bill_app.api_common.ApiInterface;
import com.mezyapps.bill_app.model.SuccessModel;
import com.mezyapps.bill_app.utils.NetworkUtils;
import com.mezyapps.bill_app.utils.SharedLoginUtils;
import com.mezyapps.bill_app.utils.ShowProgressDialog;
import com.mezyapps.bill_app.view.activity.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {
    private Context mContext;
    private TextView textName,textMobileNumber;
    private TextInputEditText edit_password, edit_confirm_password;
    private Button btn_update;
    public static ApiInterface apiInterface;
    private String password,confirm_password,user_id;
    private ShowProgressDialog showProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_change_password, container, false);
        mContext=getActivity();
        find_View_IDs(view);
        events();
        return  view;
    }

    private void find_View_IDs(View view) {
        textName=view.findViewById(R.id.textName);
        textMobileNumber=view.findViewById(R.id.textMobileNumber);
        String name= SharedLoginUtils.getUserName(mContext);
        String mobile_no=SharedLoginUtils.getUserMobile(mContext);
        textName.setText(name);
        textMobileNumber.setText(mobile_no);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        user_id= SharedLoginUtils.getUserId(mContext);
        edit_password = view.findViewById(R.id.edit_password);
        edit_confirm_password = view.findViewById(R.id.edit_confirm_password);
        btn_update = view.findViewById(R.id.btn_update);
        showProgressDialog=new ShowProgressDialog(mContext);

    }

    private void events() {
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation())
                {
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        callChangePassword();
                    }
                    else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }
                }
            }
        });
    }
    private boolean validation() {
        password=edit_password.getText().toString().trim();
        confirm_password=edit_confirm_password.getText().toString().trim();
        if(password.equalsIgnoreCase(""))
        {
            Toast.makeText(mContext, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirm_password.equalsIgnoreCase(""))
        {
            Toast.makeText(mContext, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(confirm_password))
        {
            Toast.makeText(mContext, "Password And Confirm password Not Match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

    private void callChangePassword() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.changePassword(user_id,password);
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
                            if (code.equalsIgnoreCase("1")) {

                                Toast.makeText(mContext, "Change Password Successfully", Toast.LENGTH_SHORT).show();
                                ((MainActivity) mContext).logoutApplication();
                            } else {
                                Toast.makeText(mContext, "Password Not Change", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(mContext, "Response Null", Toast.LENGTH_SHORT).show();
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



}
