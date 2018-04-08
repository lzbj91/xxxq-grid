package com.example.administrator.adapter;

import android.content.Context;

import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.BaseViewHolder;
import com.example.administrator.xxxq_grid.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;

public class MemberRecordsConsumptionListAdapter extends BaseRecycleAdapter {

    /**
     * @param context       //上下文
     * @param layoutId      //布局id
     * @param data          //数据源
     * @param refreshLayout
     */
    public MemberRecordsConsumptionListAdapter(Context context, int layoutId, List<Map<String, Object>> data, RefreshLayout refreshLayout) {
        super(context, layoutId, data, refreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, Object> map) {
        holder.setText(R.id.adapter_member_records_consumption_title, BaseUtils.toString(map.get("serveName")));
        String state = "";
        if ("4".equals(BaseUtils.toString(map.get("state")))) {
            state = "订单已完成";
        } else {
            state = "待评价";
        }
        holder.setText(R.id.adapter_member_records_consumption_state, state);
        holder.setText(R.id.adapter_member_records_consumption_money, BaseUtils.toString(map.get("totalPrice")));
        holder.setText(R.id.adapter_member_records_consumption_time, BaseUtils.toString(map.get("orderTime")));
        holder.setImageUrl(R.id.adapter_member_records_consumption_image, BaseUtils.toString(map.get("photo")));
    }
}
