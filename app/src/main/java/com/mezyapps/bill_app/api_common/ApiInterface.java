package com.mezyapps.bill_app.api_common;




import com.mezyapps.bill_app.model.SuccessModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST(EndApi.WS_LOGIN)
    @FormUrlEncoded
    Call<SuccessModel> login(@Field("mobile") String mobile_no,
                             @Field("password") String password);

    @POST(EndApi.WS_SIGN_UP)
    @FormUrlEncoded
    Call<SuccessModel> signUp(@Field("name") String name,
                             @Field("mobile") String mobile,
                             @Field("password") String password);

    @POST(EndApi.WS_CHANGE_PASSWORD)
    @FormUrlEncoded
    Call<SuccessModel> changePassword(@Field("user_id") String user_id,
                                      @Field("password") String password);

    @POST(EndApi.WS_CHECK_SESSION)
    @FormUrlEncoded
    Call<SuccessModel> checkSession(@Field("user_id") String user_id);




}
