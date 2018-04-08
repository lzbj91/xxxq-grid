package com.example.administrator.adapter;

import android.content.Context;

import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.BaseViewHolder;
import com.example.administrator.xxxq_grid.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;

public class MemberConvertibilityRecordListAdapter extends BaseRecycleAdapter {
    public MemberConvertibilityRecordListAdapter(Context context, int layoutId, List<Map<String, Object>> data, RefreshLayout refreshLayout) {
        super(context, layoutId, data, refreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, Object> map) {
        holder.setText(R.id.adapter_member_convertibility_records_list_name, BaseUtils.toString(map.get("goodName")));
        holder.setText(R.id.adapter_member_convertibility_records_list_card, BaseUtils.toString(map.get("memberCard")));
        holder.setText(R.id.adapter_member_convertibility_records_list_time, BaseUtils.toString(map.get("orderTime")));
        holder.setText(R.id.adapter_member_convertibility_records_list_state, BaseUtils.toString(Integer.valueOf(BaseUtils.toString(map.get("state"))) == 1 ? "已兑换" : "未兑换"));
        holder.setImageUrl(R.id.adapter_member_convertibility_records_list_pic, BaseUtils.toString(map.get("photo")));
    }
}
