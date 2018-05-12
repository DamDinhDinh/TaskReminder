package com.example.damdinhdinh.taskreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.damdinhdinh.taskreminder.R;
import com.example.damdinhdinh.taskreminder.model.GroupTask;

import java.util.List;

public class GroupTaskAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<GroupTask> arrGroupTask;

    public GroupTaskAdapter(Context context, int layout, List<GroupTask> listGroupReminder) {
        this.context = context;
        this.layout = layout;
        this.arrGroupTask = listGroupReminder;
    }

    @Override
    public int getCount() {
        return arrGroupTask.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
        TextView tvGroupTaskName;
        ImageView imgGroupTaskIcon;
        TextView tvGroupTaskSize;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.tvGroupTaskName = view.findViewById(R.id.tv_group_task_name);
            holder.imgGroupTaskIcon = view.findViewById(R.id.img_icon_group_task);
            holder.tvGroupTaskSize = view.findViewById(R.id.tv_group_task_size);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        GroupTask groupTask = arrGroupTask.get(i);
        holder.tvGroupTaskName.setText(groupTask.getName());
//        holder.imgGroupTaskIcon.setImageResource(groupTask.getIcon());
        holder.tvGroupTaskSize.setText(String.valueOf(groupTask.getArrTask().size()));

        return view;
    }
}
