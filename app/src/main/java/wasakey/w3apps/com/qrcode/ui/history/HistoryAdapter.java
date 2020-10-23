package wasakey.w3apps.com.qrcode.ui.history;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import wasakey.w3apps.com.qrcode.R;
import wasakey.w3apps.com.qrcode.databinding.ItemHistoryBinding;
import wasakey.w3apps.com.qrcode.helpers.constant.AppConstants;
import wasakey.w3apps.com.qrcode.helpers.itemtouch.ItemTouchHelperAdapter;
import wasakey.w3apps.com.qrcode.helpers.model.Code;
import wasakey.w3apps.com.qrcode.helpers.util.TimeUtil;
import wasakey.w3apps.com.qrcode.helpers.util.database.DatabaseUtil;
import wasakey.w3apps.com.qrcode.ui.base.ItemClickListener;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> implements ItemTouchHelperAdapter {
    /**
     * Fields
     */
    private List<Code> mItemList;
    private ItemClickListener<Code> mItemClickListener;

    public HistoryAdapter(ItemClickListener<Code> itemClickListener) {
        mItemList = new ArrayList<>();
        mItemClickListener = itemClickListener;
    }

    private boolean isEqual(Code left, Code right) {
        return /*left.equals(right)*/false;
    }

    public void clear() {
        mItemList.clear();
        notifyDataSetChanged();
    }

    public void setItemList(List<Code> itemList) {
        mItemList = itemList;
    }

    public List<Code> getItems() {
        return mItemList;
    }

    public void removeItem(Code item) {
        int index = getItemPosition(item);
        if (index < 0 || index >= mItemList.size()) return;
        mItemList.remove(index);
        notifyItemRemoved(index);
    }

    public Code getItem(int position) {
        if (position < 0 || position >= mItemList.size()) return null;
        return mItemList.get(position);
    }

    public int getItemPosition(Code item) {
        return mItemList.indexOf(item);
    }

    public int addItem(Code item) {
        Code oldItem = findItem(item);

        if (oldItem == null) {
            mItemList.add(item);
            notifyItemInserted(mItemList.size() - 1);
            return mItemList.size() - 1;
        }

        return updateItem(item, item);
    }

    public void addItem(List<Code> items) {
        for (Code item : items) {
            addItem(item);
        }
    }

    public void addItemToPosition(Code item, int position) {
        mItemList.add(position, item);
        notifyItemInserted(position);
    }

    public void addItemToPosition(List<Code> item, int position) {
        mItemList.addAll(position, item);
        notifyItemRangeChanged(position, item.size());
    }

    public Code findItem(Code item) {
        for (Code currentItem : mItemList) {
            if (isEqual(item, currentItem)) {
                return currentItem;
            }
        }
        return null;
    }

    public int updateItem(Code oldItem, Code newItem) {
        int oldItemIndex = getItemPosition(oldItem);
        mItemList.set(oldItemIndex, newItem);
        notifyItemChanged(oldItemIndex);
        return oldItemIndex;
    }

    public int updateItem(Code newItem, int position) {
        mItemList.set(position, newItem);
        notifyItemChanged(position);
        return position;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Code item = getItem(position);

        if (item != null)
            holder.bind(item);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        AsyncTask.execute(() -> {
            DatabaseUtil.on().deleteEntity(getItem(position));
            mItemList.remove(position);
        });

    }

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemHistoryBinding mBinding;

        HistoryViewHolder(@NonNull ItemHistoryBinding itemBinding) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
        }

        void bind(Code item) {
            Context context = mBinding.getRoot().getContext();

            if (context != null) {
                Glide.with(context)
                        .asBitmap()
                        .apply(new RequestOptions()
                                .skipMemoryCache(false)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .load(item.getCodeImagePath())
                        .into(mBinding.imageViewCode);

                String scanType = String.format(Locale.ENGLISH,
                        context.getString(R.string.code_scan),
                        context.getResources().getStringArray(R.array.code_types)[item.getType()]);

                mBinding.textViewCodeType.setText(scanType);

                mBinding.textViewTime.setText(
                        TimeUtil.getFormattedDateString(item.getTimeStamp(),
                                AppConstants.APP_HISTORY_DATE_FORMAT));
            }

            mBinding.constraintLayoutContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getItem(getAdapterPosition()), getAdapterPosition());
            }
        }
    }
}
