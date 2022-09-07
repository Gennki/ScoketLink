package com.qzb.smaple.wifiDirect;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qzb.smaple.databinding.ItemDeviceBinding;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private final Context context;
    private final List<DeviceItem> data;
    private OnItemChildClickListener onItemChildClickListener;

    public DeviceAdapter(Context context, List<DeviceItem> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeviceBinding binding = ItemDeviceBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDeviceBinding binding = holder.getBinding();
        DeviceItem item = data.get(position);
        NsdServiceInfo nsdServiceInfo = item.getNsdServiceInfo();
        if (nsdServiceInfo != null) {
            binding.tvDevice.setText(nsdServiceInfo.getServiceName());
        }
        binding.btnConnect.setText(item.isConnected() ? "断开" : "连接");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemDeviceBinding binding;

        public ViewHolder(ItemDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.btnConnect.setOnClickListener(v -> {
                if (onItemChildClickListener != null) {
                    onItemChildClickListener.onItemChildClick(v, getAdapterPosition());
                }
            });
            binding.btnOperate.setOnClickListener(v -> {
                if (onItemChildClickListener != null) {
                    onItemChildClickListener.onItemChildClick(v, getAdapterPosition());
                }
            });
        }

        public ItemDeviceBinding getBinding() {
            return binding;
        }
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }
}
