package com.example.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder> {

    private ArrayList<OrderDetail> orderList;

    public OrderDetailsAdapter(ArrayList<OrderDetail> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_detail, parent, false);
        return new OrderDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position) {
        OrderDetail orderDetail = orderList.get(position);
        holder.orderName.setText(orderDetail.getName());
        holder.orderContact.setText(orderDetail.getContact());
        holder.orderAddress.setText(orderDetail.getAddress());
        holder.orderDate.setText(orderDetail.getDate());
        holder.orderTime.setText(orderDetail.getTime());
        holder.orderPincode.setText(orderDetail.getPincode());
        holder.orderPrice.setText(String.valueOf(orderDetail.getPrice()));
        holder.orderType.setText(orderDetail.getType());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderDetailsViewHolder extends RecyclerView.ViewHolder {

        public TextView orderName, orderContact, orderAddress, orderDate, orderTime, orderPincode, orderPrice, orderType;

        public OrderDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.textViewOrderName);
            orderContact = itemView.findViewById(R.id.textViewOrderContact);
            orderAddress = itemView.findViewById(R.id.textViewOrderAddress);
            orderDate = itemView.findViewById(R.id.textViewOrderDate);
            orderTime = itemView.findViewById(R.id.textViewOrderTime);
            orderPincode = itemView.findViewById(R.id.textViewOrderPincode);
            orderPrice = itemView.findViewById(R.id.textViewOrderPrice);
            orderType = itemView.findViewById(R.id.textViewOrderType);
        }
    }
}
