package com.longrise.pczhaojue.bluetoothdemo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @author PCzhaojue
 * @name BlueToothDemo
 * @class name：com.longrise.pczhaojue.bluetoothdemo
 * @class describe
 * @time 2018/8/15 上午9:35
 * @change
 * @chang time
 * @class describe
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    private List<BlueToothDevice> list;

    public MyAdapter(List<BlueToothDevice> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        final String name;
        final String address;
        final String signal;

        holder.nameText.setText("DeviceName：" + list.get(position).getDeviceName());
        holder.addressText.setText("MAC Address：" + list.get(position).getDeviceMacAddr());
        holder.signalText.setText("signal：" + list.get(position).getSignal());
        name = list.get(position).getDeviceName() == null ? "":list.get(position).getDeviceName();
        address = list.get(position).getDeviceMacAddr() == null ? "":list.get(position).getDeviceMacAddr();
        signal = list.get(position).getSignal() == null ? "":list.get(position).getSignal();

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (listener != null){
                    listener.onItemClick(name,address,signal);
                }
            }
        });

    }

    @Override
    public int getItemCount()
    {
        if (list.size() > 0)
        {
            return list.size();
        }
        return 0;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener
    {
        void onItemClick(String name, String address, String signal);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        private final TextView nameText;
        private final TextView addressText;
        private final TextView signalText;

        public ViewHolder(View itemView)
        {
            super(itemView);
            nameText = itemView.findViewById(R.id.tv_name);
            addressText = itemView.findViewById(R.id.tv_macAddress);
            signalText = itemView.findViewById(R.id.tv_signal);
        }
    }

}
