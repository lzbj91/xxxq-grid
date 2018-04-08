package com.example.administrator.adapter;

import android.content.Context;

import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.BaseViewHolder;
import com.example.administrator.xxxq_grid.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;

public class MessageListAdapter extends BaseRecycleAdapter {

    /**
     * @param context  //上下文
     * @param layoutId //布局id
     * @param data     //数据源
     */
    public MessageListAdapter(Context context, int layoutId, List<Map<String, Object>> data, RefreshLayout refreshLayout) {
        super(context, layoutId, data, refreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, Object> map) {
        holder.setText(R.id.adapter_message_list_content, BaseUtils.toString(map.get("content")));
        holder.setText(R.id.adapter_message_list_time, BaseUtils.toString(map.get("createTime")));
        if (Integer.parseInt(BaseUtils.toString(map.get("isRead"))) == 0) {
            holder.setVisible(R.id.adapter_message_list_read);
        } else {
            holder.setGone(R.id.adapter_message_list_read);
        }
    }
}
