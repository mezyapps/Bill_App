package com.mezyapps.bill_app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.model.LocalDBItemModel;
import com.mezyapps.bill_app.utils.SelectBillIItemInterface;

import java.util.ArrayList;

public class BillitemAdapter extends RecyclerView.Adapter<BillitemAdapter.MyViewHolder>  {

    private Context mContext;
    private ArrayList<LocalDBItemModel> localDBItemModelArrayList;

    public BillitemAdapter(Context mContext, ArrayList<LocalDBItemModel> localDBItemModelArrayList) {
        this.mContext = mContext;
        this.localDBItemModelArrayList = localDBItemModelArrayList;
    }

    @NonNull
    @Override
    public BillitemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_bill_print_adpter, parent, false);
        return new BillitemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillitemAdapter.MyViewHolder holder, final int position) {
        holder.textQty.setText(localDBItemModelArrayList.get(position).getQty());
        holder.textRate.setText(localDBItemModelArrayList.get(position).getRate());
        holder.textAmount.setText(localDBItemModelArrayList.get(position).getAmt());
        holder.textsrNo.setText(localDBItemModelArrayList.get(position).getSr_no());
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
