package com.example.firebaseprac2;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 정인섭 on 2017-10-31.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    ArrayList<User> list = new ArrayList<>();
    TransferId transferId;

    public RecyclerViewAdapter(TransferId transferId) {
        this.transferId = transferId;
    }

    public void refreshList(ArrayList<User> list) {
        this.list = list;
        Log.d("size는", list.size()+"");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(list.get(position).email);
        holder.current_id = list.get(position).userID;
        holder.token = list.get(position).token;

    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        String current_id;
        String token;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    transferId.transferUserId(current_id, token);
                }
            });

        }
    }

    interface TransferId{
        void transferUserId(String id, String token);
    }
}
