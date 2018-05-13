package com.example.damdinhdinh.taskreminder.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damdinhdinh.taskreminder.ListTaskActivity;
import com.example.damdinhdinh.taskreminder.MainActivity;
import com.example.damdinhdinh.taskreminder.R;
import com.example.damdinhdinh.taskreminder.database.DatabaseSQLite;
import com.example.damdinhdinh.taskreminder.model.GroupTask;

import java.util.List;

public class GroupTaskAdapter extends BaseAdapter{
    private MainActivity context;
    private int layout;
    private List<GroupTask> arrGroupTask;
    private DatabaseSQLite database;

    public GroupTaskAdapter(MainActivity context, int layout, List<GroupTask> listGroupReminder) {
        this.context = context;
        this.layout = layout;
        this.arrGroupTask = listGroupReminder;
        database = new DatabaseSQLite(context, "task.sqlite", null, 1);
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
        ImageView imgVerticalMenu;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.tvGroupTaskName = view.findViewById(R.id.tv_group_task_name);
            holder.imgGroupTaskIcon = view.findViewById(R.id.img_icon_group_task);
            holder.tvGroupTaskSize = view.findViewById(R.id.tv_group_task_size);
            holder.imgVerticalMenu = view.findViewById(R.id.img_vertical_menu);
            holder.imgVerticalMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.showPopupMenu(context, view, i);
                }
            });
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
