package com.mezyapps.bill_app.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.api_common.ApiClient;
import com.mezyapps.bill_app.api_common.ApiInterface;
import com.mezyapps.bill_app.model.SuccessModel;
import com.mezyapps.bill_app.model.UserModel;
import com.mezyapps.bill_app.utils.NetworkUtils;
import com.mezyapps.bill_app.utils.SharedLoginUtils;
import com.mezyapps.bill_app.utils.ShowProgressDialog;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText edit_name,edit_number,edit_password;
    private Button btn_sign_up;
    private String name,mobile,password;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<UserModel> userModelArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        edit_name=findViewById(R.id.edit_name);
        edit_number=findViewById(R.id.edit_number);
        edit_password=findViewById(R.id.edit_password);
        btn_sign_up=findViewById(R.id.btn_sign_up);

        showProgressDialog=new ShowProgressDialog(SignUpActivity.this);
    }

    private void events() {
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation())
                {
                    if (NetworkUtils.isNetworkAvailable(SignUpActivity.this)) {
                        callSignUpUser();
                    }
                    else {
                        NetworkUtils.isNetworkNotAvailable(SignUpActivity.this);
                    }
                }
            }
        });
    }
    private void callSignUpUser() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.signUp(name,mobile,password);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        userModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                userModelArrayList=successModule.getUserModelArrayList();

                                if(userModelArrayList.size()!=0) {
                                    SharedLoginUtils.putLoginSharedUtils(SignUpActivity.this);
                                    String user_id=userModelArrayList.get(0).getId();
                                    String name=userModelArrayList.get(0).getName();
                                    String mobile=userModelArrayList.get(0).getName();
                                    SharedLoginUtils.addUser(SignUpActivity.this,user_id,name,mobile);
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else if(code.equalsIgnoreCase("2")){
                                Toast.makeText(SignUpActivity.this, "User Already Register This No", Toast.LENGTH_SHORT).show();
                            }
                            else if(code.equalsIgnoreCase("0"))
                            {
                                Toast.makeText(SignUpActivity.this, "User Not Register", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(SignUpActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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

    private boolean validation() {
        name=edit_name.getText().toString().trim();
        mobile=edit_number.getText().toString().trim();
        password=edit_password.getText().toString().trim();

        if(name.equalsIgnoreCase(""))
        {
            edit_name.setError("Enter Name");
            edit_name.requestFocus();
            return false;
        }else if(mobile.equalsIgnoreCase(""))
        {
            edit_number.setError("Enter Mobile Number");
            edit_number.requestFocus();
            return false;
        }else if(mobile.length()<10)
        {
            edit_number.setError("Enter Valid Mobile Number");
            edit_number.requestFocus();
            return false;
        }else if(password.equalsIgnoreCase(""))
        {
            edit_password.setError("Enter Password");
            edit_password.requestFocus();
            return false;
        }
        return true;
    }
}
