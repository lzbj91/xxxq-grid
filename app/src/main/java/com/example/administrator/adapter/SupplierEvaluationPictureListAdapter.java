package com.example.administrator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.HttpRequest;
import com.example.administrator.xxxq_grid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplierEvaluationPictureListAdapter extends BaseAdapter {

    private List<Map<String, Object>> list;
    private Context context;
    private LayoutInflater inflater;

    public SupplierEvaluationPictureListAdapter(Context context, List<Map<String, Object>> list) {
        super();
//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = LayoutInflater.from(context).inflate(R.layout.adapter_supplier_evaluation_picture_list, null);
            vh = new ViewHolder();
            vh.imageView = (ImageView) view.findViewById(R.id.adapter_supplier_evaluation_picture_picture);
            view.setTag(vh);
        }
        vh = (ViewHolder) view.getTag();
        if (list != null && list.size() > 0) {
            Glide.with(context).load(new HttpRequest().getFileUrl() + BaseUtils.toString(list.get(i).get("photo"))).centerCrop().into(vh.imageView);
        }
        return view;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
