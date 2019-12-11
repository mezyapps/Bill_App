package com.mezyapps.bill_app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.model.LocalDBItemModel;
import com.mezyapps.bill_app.utils.SelectBillIItemInterface;
import com.mezyapps.bill_app.utils.SharedLoginUtils;
import com.mezyapps.bill_app.view.activity.LoginActivity;
import com.mezyapps.bill_app.view.activity.MainActivity;

import java.util.ArrayList;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder>  {

    private Context mContext;
    private ArrayList<LocalDBItemModel> localDBItemModelArrayList;
    private SelectBillIItemInterface selectBillIItemInterface;
    public ItemAdapter(Context mContext, ArrayList<LocalDBItemModel> localDBItemModelArrayList, SelectBillIItemInterface selectBillIItemInterface) {
        this.mContext = mContext;
        this.localDBItemModelArrayList = localDBItemModelArrayList;
        this.selectBillIItemInterface=selectBillIItemInterface;
    }

    @NonNull
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_bill_item_adpter, parent, false);
        return new ItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.MyViewHolder holder, final int position) {


        holder.textQty.setText(localDBItemModelArrayList.get(position).getQty());
        holder.textRate.setText(localDBItemModelArrayList.get(position).getRate());
        holder.textAmount.setText(localDBItemModelArrayList.get(position).getAmt());
        holder.textsrNo.setText(localDBItemModelArrayList.get(position).getSr_no());
        
        holder.iv_open_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog  dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_bill_edit);
                TextView txt_edit = dialog.findViewById(R.id.txt_edit);
                TextView txt_delete = dialog.findViewById(R.id.txt_delete);
                TextView sr_no = dialog.findViewById(R.id.sr_no);
                dialog.setCancelable(true);
                String sr_nostr="SR.NO :"+localDBItemModelArrayList.get(position).getSr_no();
                sr_no.setText(sr_nostr);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                txt_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        selectBillIItemInterface.getSelectItemEdit(localDBItemModelArrayList.get(position));
                    }
                });
                txt_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        selectBillIItemInterface.getSelectItemDelete(localDBItemModelArrayList.get(position).getId());

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDBItemModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textQty, textRate, textAmount,textsrNo;
        private ImageView iv_open_dialog;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textQty = itemView.findViewById(R.id.textQty);
            textRate = itemView.findViewById(R.id.textRate);
            textAmount = itemView.findViewById(R.id.textAmount);
            textsrNo = itemView.findViewById(R.id.textsrNo);
            iv_open_dialog = itemView.findViewById(R.id.iv_open_dialog);
        }
    }
}
