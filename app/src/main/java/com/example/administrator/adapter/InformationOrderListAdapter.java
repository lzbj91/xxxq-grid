package com.example.administrator.adapter;

import android.content.Context;

import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.BaseViewHolder;
import com.example.administrator.xxxq_grid.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;


public class InformationOrderListAdapter extends BaseRecycleAdapter {
    /**
     * @param context       //上下文
     * @param layoutId      //布局id
     * @param data          //数据源
     * @param refreshLayout
     */
    public InformationOrderListAdapter(Context context, int layoutId, List<Map<String, Object>> data, RefreshLayout refreshLayout) {
        super(context, layoutId, data, refreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, Object> map) {
        holder.setText(R.id.adapter_information_order_list_title, BaseUtils.toString(map.get("seContentTypeName")));
        holder.setText(R.id.adapter_information_order_list_content, BaseUtils.toString(map.get("seContentInfoName")));
        holder.setText(R.id.adapter_information_order_list_order_num, BaseUtils.toString(map.get("orderNum")));
        holder.setText(R.id.adapter_information_order_list_sale_num, BaseUtils.toString(map.get("orderComplete")));
        holder.setText(R.id.adapter_information_order_list_update_time, BaseUtils.toString(map.get("updateTime") == null ? "无" : map.get("updateTime")));
    }
}
