package com.qzb.smaple;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.qzb.smaple.databinding.ItemDeviceBinding;

import java.util.Objects;

public class DeviceListAdapter extends ListAdapter<DeviceItem, DeviceListAdapter.ViewHolder> {

    private Context context;
    private OnItemChildClickListener onItemChildClickListener;

    public DeviceListAdapter(Context context) {
        super(new DiffCallback());
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeviceBinding binding = ItemDeviceBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDeviceBinding binding = holder.getBinding();
        DeviceItem item = getItem(position);
        NsdServiceInfo nsdServiceInfo = item.getNsdServiceInfo();
        binding.tvDevice.setText(nsdServiceInfo.getServiceName());
        binding.btnConnect.setText(item.isConnected() ? "断开" : "连接");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemDeviceBinding binding;

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

    private static class DiffCallback extends DiffUtil.ItemCallback<DeviceItem> {

        @Override
        public boolean areItemsTheSame(@NonNull DeviceItem oldItem, @NonNull DeviceItem newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull DeviceItem oldItem, @NonNull DeviceItem newItem) {
            return Objects.equals(oldItem, newItem) && oldItem.isConnected() == newItem.isConnected();
        }
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }
}
