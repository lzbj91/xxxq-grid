package com.example.administrator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.HttpRequest;
import com.example.administrator.xxxq_grid.R;
import com.liji.imagezoom.util.ImageZoom;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SupplierEvaluationListAdapter extends BaseAdapter {

    private List<Map<String, Object>> list;
    private Context context;
    private LayoutInflater inflater;
    private RefreshLayout refreshLayout;

    //图片横向list
    private GridView gridView;
    private SupplierEvaluationPictureListAdapter supplierEvaluationPictureListAdapter;

    public SupplierEvaluationListAdapter(Context context, List<Map<String, Object>> list, RefreshLayout refreshLayout) {
        super();
        this.context = context;
        this.refreshLayout = refreshLayout;
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_supplier_evaluation_list, viewGroup, false);
        Map<String, Object> map = list.get(i);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.adapter_supplier_evaluation_avatar);
        TextView adapter_supplier_evaluation_name = (TextView) convertView.findViewById(R.id.adapter_supplier_evaluation_name);
        TextView adapter_supplier_evaluation_service = (TextView) convertView.findViewById(R.id.adapter_supplier_evaluation_service);
        RatingBar adapter_supplier_evaluation_rating = (RatingBar) convertView.findViewById(R.id.adapter_supplier_evaluation_rating);
        TextView adapter_supplier_evaluation_scour = (TextView) convertView.findViewById(R.id.adapter_supplier_evaluation_scour);
        TextView adapter_supplier_evaluation_evaluation = (TextView) convertView.findViewById(R.id.adapter_supplier_evaluation_evaluation);

        adapter_supplier_evaluation_name.setText(BaseUtils.toString(map.get("memberName")));
        adapter_supplier_evaluation_service.setText(BaseUtils.toString(map.get("name")));
        adapter_supplier_evaluation_scour.setText(BaseUtils.toString(map.get("rank") + "分"));
        adapter_supplier_evaluation_evaluation.setText(BaseUtils.toString(map.get("comment")));
        adapter_supplier_evaluation_rating.setRating(Float.valueOf(BaseUtils.toString(map.get("rank"))));
        Glide.with(context).load(new HttpRequest().getFileUrl() + BaseUtils.toString(map.get("photo"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(avatar);
        List<Map<String, Object>> picture = (List<Map<String, Object>>) list.get(i).get("photos");
        supplierEvaluationPictureListAdapter = new SupplierEvaluationPictureListAdapter(context, picture);
        gridView = (GridView) convertView.findViewById(R.id.adapter_supplier_evaluation_evaluation_picture);
        gridView.setAdapter(supplierEvaluationPictureListAdapter);

        final List<String> l = new ArrayList<String>();
        for (int j = 0; j < picture.size(); j++) {
            l.add(new HttpRequest().getFileUrl() + BaseUtils.toString(picture.get(j).get("photo")));
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageZoom.show(context, position, l);
            }
        });

        return convertView;
    }

    public List<Map<String, Object>> addData(List<Map<String, Object>> newData) {
        this.list.addAll(newData);
        refreshLayout.finishLoadMore();
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 500);
        return this.list;
    }

    public void updateData(List<Map<String, Object>> data) {
        if (data != this.list) {
            this.list.clear();
            this.list.addAll(data);
            refreshLayout.finishRefresh();
            notifyDataSetChanged();
        }
    }

}
