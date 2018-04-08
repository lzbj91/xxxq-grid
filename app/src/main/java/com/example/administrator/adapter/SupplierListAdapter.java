package com.example.administrator.adapter;

import android.content.Context;

import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.BaseViewHolder;
import com.example.administrator.xxxq_grid.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;

public class SupplierListAdapter extends BaseRecycleAdapter {


    /**
     * @param context       //上下文
     * @param layoutId      //布局id
     * @param data          //数据源
     * @param refreshLayout
     */
    public SupplierListAdapter(Context context, int layoutId, List<Map<String, Object>> data, RefreshLayout refreshLayout) {
        super(context, layoutId, data, refreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, Object> map) {
        holder.setText(R.id.adapter_supplier_list_title, BaseUtils.toString(map.get("name")));
        holder.setText(R.id.adapter_supplier_list_source, BaseUtils.toString(map.get("evaluate")) + "分");
        holder.setText(R.id.adapter_supplier_list_order_num, BaseUtils.toString(map.get("supplierSaleNum")));
        holder.setText(R.id.adapter_supplier_list_sale_num, BaseUtils.toString(map.get("supplierSalePrice")));
        holder.setImageUrl(R.id.adapter_supplier_list_image, BaseUtils.toString(map.get("photo")));
        holder.setRating(R.id.adapter_supplier_list_star, Float.valueOf(BaseUtils.toString(map.get("evaluate"))));
    }
}
