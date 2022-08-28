package com.qzb.smaple.wifip2p;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.qzb.smaple.databinding.ItemDeviceBinding;
import com.qzb.smaple.wifiDirect.OnItemChildClickListener;

import java.util.Objects;

public class P2PDeviceListAdapter extends ListAdapter<P2PDeviceItem, P2PDeviceListAdapter.ViewHolder> {

    private Context context;
    private OnItemChildClickListener onItemChildClickListener;

    public P2PDeviceListAdapter(Context context) {
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
        P2PDeviceItem item = getItem(position);
        binding.tvDevice.setText(item.getWifiP2pDevice().deviceName);
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

    private static class DiffCallback extends DiffUtil.ItemCallback<P2PDeviceItem> {

        @Override
        public boolean areItemsTheSame(@NonNull P2PDeviceItem oldItem, @NonNull P2PDeviceItem newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull P2PDeviceItem oldItem, @NonNull P2PDeviceItem newItem) {
            return Objects.equals(oldItem, newItem) && oldItem.isConnected() == newItem.isConnected();
        }
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }
}
