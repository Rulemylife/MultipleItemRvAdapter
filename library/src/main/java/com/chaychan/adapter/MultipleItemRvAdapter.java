package com.chaychan.adapter;

import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;

import java.util.List;

/**
 * @author ChayChan
 * @description: 封装多条目adapter
 * @date 2018/3/21  9:55
 */

public abstract class MultipleItemRvAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    private SparseArray<BaseItemProvider> mItemProviders;
    protected ProviderDelegate mProviderDelegate;

    public MultipleItemRvAdapter(@Nullable List<T> data) {
        super(data);
    }

    /**
     * 用于adapter构造函数完成参数的赋值后调用
     */
    public void finishInitialize() {
        mProviderDelegate = new ProviderDelegate();

        setMultiTypeDelegate(new MultiTypeDelegate<T>() {

            @Override
            protected int getItemType(T t) {
                return getViewType(t);
            }
        });

        registerItemProvider();

        mItemProviders = mProviderDelegate.getItemProviders();
        for (int i = 0; i < mItemProviders.size(); i++) {
            int key = mItemProviders.keyAt(i);

            BaseItemProvider provider = mItemProviders.get(key);
            provider.mData = mData;

            ItemProviderTag tag = provider.getClass().getAnnotation(ItemProviderTag.class);
            getMultiTypeDelegate().registerItemType(key, tag.layout());
        }
    }

    protected abstract int getViewType(T t);

    public abstract void registerItemProvider();

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        int itemViewType = helper.getItemViewType();
        BaseItemProvider provider = mItemProviders.get(itemViewType);

        provider.mContext = helper.itemView.getContext();

        int position = helper.getLayoutPosition() - getHeaderLayoutCount();
        provider.convert(helper, item, position);

        bindClick(helper, item, position, provider);
    }

    private void bindClick(final BaseViewHolder helper, final T item, final int position, final BaseItemProvider provider) {
        View itemView = helper.itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provider.onClick(helper, item, position);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return provider.onLongClick(helper, item, position);
            }
        });
    }
}
