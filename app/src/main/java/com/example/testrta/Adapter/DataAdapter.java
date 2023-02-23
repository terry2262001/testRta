package com.example.testrta.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testrta.Model.Data;
import com.example.testrta.R;

import java.util.ArrayList;
import java.util.Date;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataVH> {


    private Context mContext;
    private ArrayList<Data> dataList;
    private onClickItem listener;

    public DataAdapter(Context mContext, ArrayList<Data> dataList,onClickItem listener) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.listener = listener;
    }
    public DataAdapter(Context mContext, ArrayList<Data> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public DataVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_data, parent, false);
        return  new DataAdapter.DataVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataVH holder, int position) {
        Data  data = dataList.get(position);
        holder.tvNameData.setText(data.getName());
        holder.itemView.setBackgroundColor(data.isSelected() ? Color.GRAY : Color.WHITE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemOnClick(data);
                data.setSelected(!data.isSelected());
                holder.itemView.setBackgroundColor(data.isSelected() ? Color.GRAY : Color.WHITE);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public class DataVH extends RecyclerView.ViewHolder {
        TextView tvNameData;

        public DataVH(@NonNull View itemView) {
            super(itemView);
            tvNameData = itemView.findViewById(R.id.tvNameData);

        }
    }

    public interface onClickItem{
        void onItemOnClick(Data data);
    }

}
