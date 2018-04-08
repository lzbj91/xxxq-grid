package com.example.administrator.adapter;

import android.content.Context;

import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.BaseViewHolder;
import com.example.administrator.xxxq_grid.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;
import java.util.Map;

public class HomeNewsAdapter extends BaseRecycleAdapter {

    /**
     * @param context  //上下文
     * @param layoutId //布局id
     * @param data     //数据源
     */
    public HomeNewsAdapter(Context context, int layoutId, List<Map<String, Object>> data, RefreshLayout refreshLayout) {
        super(context, layoutId, data, refreshLayout);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, Object> map) {
        String content = BaseUtils.toString(map.get("content"));
        content = content.substring(0, content.length() < 50 ? content.length() : 50);
        holder.setText(R.id.adapter_home_news_list_title, BaseUtils.toString(map.get("name")));
        holder.setText(R.id.adapter_home_news_list_content, content);
        holder.setText(R.id.adapter_home_news_list_time, BaseUtils.toString(map.get("showTime")));
        holder.setImageUrl(R.id.adapter_home_news_list_avatar, BaseUtils.toString(map.get("photo")));
    }
}
