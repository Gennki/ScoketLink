package com.qzb.scoketlink.wifip2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.qzb.scoketlink.databinding.ItemDeviceBinding;

import java.util.List;

public class DeviceListAdapter extends ListAdapter<WifiP2pDevice, DeviceListAdapter.ViewHolder> {

    private Context context;
    private List<WifiP2pDevice> data;

    public DeviceListAdapter(Context context, List<WifiP2pDevice> data) {
        super(new DiffCallback());
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDeviceBinding binding = holder.getBinding();
        WifiP2pDevice device = data.get(position);
        binding.tvDevice.setText(device.deviceName);
    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemDeviceBinding binding;

        public ViewHolder(ItemDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemDeviceBinding getBinding() {
            return binding;
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<WifiP2pDevice> {

        @Override
        public boolean areItemsTheSame(@NonNull WifiP2pDevice oldItem, @NonNull WifiP2pDevice newItem) {
            return oldItem.deviceAddress == newItem.deviceAddress;
        }

        @Override
        public boolean areContentsTheSame(@NonNull WifiP2pDevice oldItem, @NonNull WifiP2pDevice newItem) {
            return oldItem.equals(newItem);
        }
    }
}
