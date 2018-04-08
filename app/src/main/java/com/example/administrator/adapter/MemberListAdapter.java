package com.example.administrator.adapter;

import android.content.Context;

import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.BaseViewHolder;
import com.example.administrator.xxxq_grid.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;

public class MemberListAdapter extends BaseRecycleAdapter {

    /**
     * @param context       //上下文
     * @param layoutId      //布局id
     * @param data          //数据源
     * @param refreshLayout
     */
    public MemberListAdapter(Context context, int layoutId, List<Map<String, Object>> data, RefreshLayout refreshLayout) {
        super(context, layoutId, data, refreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, Object> map) {
        holder.setText(R.id.adapter_member_list_phone, BaseUtils.toString(map.get("phone")));
        holder.setText(R.id.adapter_member_list_sale_number, BaseUtils.toString(map.get("money")));
        holder.setText(R.id.adapter_member_list_update_time, BaseUtils.toString(map.get("moneyUpdateTime")));
        holder.setImageUrl(R.id.adapter_member_list_avatar, BaseUtils.toString(map.get("photo")));
    }
}
