package com.example.fraccionamiento.Adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fraccionamiento.Classes.DateClass;
import com.example.fraccionamiento.Classes.PaymentsClass;
import com.example.fraccionamiento.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Este adaptador permite traer todos los pagos realizados por un usuario en especifico, conjunto de datos denominado nodo crearea una vista independiente con los
// datos obtenidos de FireBase

public class AdapterPayments extends RecyclerView.Adapter<AdapterPayments.ViewHolderData> implements View.OnClickListener {

    private View.OnClickListener listener;
    private ArrayList<PaymentsClass> payments;

    public AdapterPayments(ArrayList<PaymentsClass> payments) {
        this.payments = payments;
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public AdapterPayments.ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_receipts,null,false);
        view.setOnClickListener(this);
        return new AdapterPayments.ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPayments.ViewHolderData holder, int position) {
        DateClass date = new DateClass(payments.get(position).getMonth());
        holder.txtMonth.setText(date.parseStringMonth());
        holder.txtMountPayed.setText(String.valueOf(payments.get(position).getMountPayed()));
        holder.txtYear.setText(String.valueOf(payments.get(position).getYear()));
        if (payments.get(position).isDebt()){
            holder.txtMountDebt.setText(String.valueOf(payments.get(position).getMountDebt()));
        }

        if (payments.get(position).isPayed()){
            holder.imgVwStatusPayment.setImageResource(R.drawable.ic_payed);
        }else {
            holder.imgVwStatusPayment.setImageResource(R.drawable.ic_not_payed);
        }



    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        private TextView txtMonth, txtYear, txtMountPayed, txtMountDebt;
        private ImageView imgVwStatusPayment;
        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            txtMonth = itemView.findViewById(R.id.txtMonthPayed);
            txtYear = itemView.findViewById(R.id.txtYearPayed);
            txtMountPayed = itemView.findViewById(R.id.txtMountPayed);
            txtMountDebt = itemView.findViewById(R.id.txtMountDebt);
            imgVwStatusPayment = itemView.findViewById(R.id.imgVwIconStatusPayed);

        }
    }
}
