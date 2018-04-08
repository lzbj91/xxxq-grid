package com.example.administrator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.xxxq_grid.R;

import java.util.ArrayList;
import java.util.List;

public class FieldExplorationGridViewAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private LayoutInflater inflater;

    public FieldExplorationGridViewAdapter(Context context, List<String> list) {
        super();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        if (list != null) {
            this.list = list;
        } else {
            this.list = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_field_exploration_grid_view_picture_list, null);
            vh = new ViewHolder();
            vh.imageView = (ImageView) view.findViewById(R.id.adapter_field_exploration_grid_view_picture);
            view.setTag(vh);
        }
        vh = (ViewHolder) view.getTag();
        if (list != null && list.size() > 0) {
            Glide.with(context).load("file://" + list.get(i)).centerCrop().into(vh.imageView);
        }
        return view;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
