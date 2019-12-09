package com.example.fraccionamiento.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fraccionamiento.Classes.UserClass;
import com.example.fraccionamiento.R;

import java.util.ArrayList;


// Este adaptador trae consigo el conjunto de datos de cada usuario y los va mostrando con una vista reciclada (recycler view)
public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.ViewHolderData> implements View.OnClickListener {
    private View.OnClickListener listener;
    private ArrayList<UserClass> userList;

    public AdapterUsers(ArrayList<UserClass> userList) {
        this.userList = userList;
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
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users,null,false);
        view.setOnClickListener(this);
        return new ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        String fullName = userList.get(position).getName() + " " + userList.get(position).getLastName();
        holder.lblUserName.setText(fullName);
        holder.lblDepNum.setText(userList.get(position).getDeptNum());
        if(userList.get(position).getDebt()){
            holder.imgVwPayStatus.setImageResource(R.drawable.ic_payed);
        }else{
            holder.imgVwPayStatus.setImageResource(R.drawable.ic_not_payed);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {
        private TextView lblUserName, lblDepNum;
        private ImageView imgVwPayStatus;
        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            lblUserName = itemView.findViewById(R.id.lblUserName);
            lblDepNum = itemView.findViewById(R.id.userDepNum);
            imgVwPayStatus = itemView.findViewById(R.id.imgVwIconPayStatus);
        }
    }
}
