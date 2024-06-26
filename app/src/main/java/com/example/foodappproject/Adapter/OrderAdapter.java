package com.example.foodappproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodappproject.Domain.Order;
import com.example.foodappproject.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.viewholder> {
    ArrayList<Order> items;

    public OrderAdapter(ArrayList<Order> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.viewholder holder, int position) {
        holder.idTxt.setText(String.valueOf(position + 1));
        holder.priceTxt.setText("$"+items.get(position).getTotal_Price());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView idTxt,priceTxt;
        public viewholder(@NonNull View itemView)
        {
            super(itemView);
            idTxt = itemView.findViewById(R.id.order_id_value_textview);
            priceTxt = itemView.findViewById(R.id.total_price_value_textview);
        }
    }
}
