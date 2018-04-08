package com.example.administrator.adapter;

import android.content.Context;

import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.BaseViewHolder;
import com.example.administrator.xxxq_grid.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;

public class SupplierDetailListAdapter extends BaseRecycleAdapter {


    /**
     * @param context       //上下文
     * @param layoutId      //布局id
     * @param data          //数据源
     * @param refreshLayout
     */
    public SupplierDetailListAdapter(Context context, int layoutId, List<Map<String, Object>> data, RefreshLayout refreshLayout) {
        super(context, layoutId, data, refreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, Object> map) {
        holder.setText(R.id.adapter_supplier_detail_list_order_num, "订单号: " + BaseUtils.toString(map.get("orderNum")));
        holder.setText(R.id.adapter_supplier_detail_list_user_name, BaseUtils.toString(map.get("name")));
        holder.setText(R.id.adapter_supplier_detail_list_title, BaseUtils.toString(map.get("serveContent")));
        holder.setText(R.id.adapter_supplier_detail_list_time, BaseUtils.toString(map.get("orderTimeAs")));
        holder.setImageUrl(R.id.adapter_supplier_detail_list_avatar, BaseUtils.toString(map.get("photo")));
    }
}
