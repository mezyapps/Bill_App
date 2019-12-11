package com.mezyapps.bill_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mezyapps.bill_app.R;
import com.mezyapps.bill_app.model.BillHDModel;
import com.mezyapps.bill_app.view.activity.BillPerviewActivity;
import com.mezyapps.bill_app.view.activity.BillPreviewSaveActivity;
import com.mezyapps.bill_app.view.activity.EditBillActivity;

import java.util.ArrayList;

public class BillHDAdapter  extends RecyclerView.Adapter<BillHDAdapter.MyViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<BillHDModel> billHDAdapterArrayList;
    private  ArrayList<BillHDModel> arrayListFiltered;

    public BillHDAdapter(Context mContext, ArrayList<BillHDModel> billHDAdapterArrayList) {
        this.mContext = mContext;
        this.billHDAdapterArrayList = billHDAdapterArrayList;
        this.arrayListFiltered = billHDAdapterArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_bill_adpter,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final BillHDModel billHDModel=billHDAdapterArrayList.get(position);

        holder.textBillNo.setText(billHDModel.getId());
        holder.textCustName.setText(billHDModel.getCust_name());
        holder.textDate.setText(billHDModel.getDate());
        holder.textTotalQty.setText(billHDModel.getTotal_qty());
        holder.textTotalAmt.setText(billHDModel.getTotal_amt());
        holder.textViewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, BillPerviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("BILL", (Parcelable) billHDAdapterArrayList.get(position));
                mContext.startActivity(intent);
            }
        });
        holder.textEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, EditBillActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("BILL", (Parcelable) billHDAdapterArrayList.get(position));
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return billHDAdapterArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textBillNo,textCustName,textDate,textTotalQty,textTotalAmt,textViewBill,textEdit;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textBillNo=itemView.findViewById(R.id.textBillNo);
            textCustName=itemView.findViewById(R.id.textCustName);
            textDate=itemView.findViewById(R.id.textDate);
            textTotalQty=itemView.findViewById(R.id.textTotalQty);
            textTotalAmt=itemView.findViewById(R.id.textTotalAmt);
            textViewBill=itemView.findViewById(R.id.textViewBill);
            textEdit=itemView.findViewById(R.id.textEdit);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().replaceAll("\\s","").toLowerCase().trim();
                if (charString.isEmpty() || charSequence.equals("")) {
                    billHDAdapterArrayList = arrayListFiltered;
                } else {
                    ArrayList<BillHDModel> filteredList = new ArrayList<>();
                    for (int i = 0; i < billHDAdapterArrayList.size(); i++) {
                        String bill_no=billHDAdapterArrayList.get(i).getId().replaceAll("\\s","").toLowerCase().trim();
                        String  party_name=billHDAdapterArrayList.get(i).getCust_name().toLowerCase().replaceAll("\\s","").toLowerCase().trim();
                        if ((bill_no.contains(charString))||(party_name.contains(charString))) {
                            filteredList.add(billHDAdapterArrayList.get(i));
                        }
                    }
                    if (filteredList.size() > 0) {
                        billHDAdapterArrayList = filteredList;
                    } else {
                        billHDAdapterArrayList = arrayListFiltered;
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = billHDAdapterArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                billHDAdapterArrayList = (ArrayList<BillHDModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
