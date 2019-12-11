package com.mezyapps.bill_app.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText edit_username, edit_password;
    private Button btn_login,btn_sign_up;
    private String username, password;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<UserModel> userModelArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        find_View_IdS();
        events();
    }

    private void find_View_IdS() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        btn_login = findViewById(R.id.btn_login);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        edit_username = findViewById(R.id.edit_username);
        edit_password = findViewById(R.id.edit_password);
        showProgressDialog=new ShowProgressDialog(LoginActivity.this);
    }

    private void events() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                        callLogin();
                    }
                    else {
                        NetworkUtils.isNetworkNotAvailable(LoginActivity.this);
                    }
                }
            }
        });
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
    }

    private boolean validation() {
        username = edit_username.getText().toString().trim();
        password = edit_password.getText().toString().trim();

        if (username.equalsIgnoreCase("")) {
            edit_username.setError("Enter Mobile Number");
            edit_username.requestFocus();
            return false;
        }else if (username.length()<10) {
            edit_username.setError("Enter Valid Mobile Number");
            edit_username.requestFocus();
            return false;
        } else if (password.equalsIgnoreCase("")) {
            edit_password.setError("Enter Password");
            edit_password.requestFocus();
            return false;
        }
        return true;
    }

    private void callLogin() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.login(username,password);
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
                                    SharedLoginUtils.putLoginSharedUtils(LoginActivity.this);
                                    String user_id=userModelArrayList.get(0).getId();
                                    String name=userModelArrayList.get(0).getName();
                                    String mobile=userModelArrayList.get(0).getMobile_no();
                                    SharedLoginUtils.addUser(LoginActivity.this,user_id,name,mobile);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "User Not Registered", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(LoginActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
