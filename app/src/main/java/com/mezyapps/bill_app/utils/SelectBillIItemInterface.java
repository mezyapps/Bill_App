package com.mezyapps.bill_app.utils;

import com.mezyapps.bill_app.model.LocalDBItemModel;

public interface SelectBillIItemInterface {
    public void getSelectItemEdit(LocalDBItemModel localDBItemModel);
    public void getSelectItemDelete(String id);
}
