package com.example.damdinhdinh.taskreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.damdinhdinh.taskreminder.ListGroupTaskActivity;
import com.example.damdinhdinh.taskreminder.R;
import com.example.damdinhdinh.taskreminder.ShowPopup;
import com.example.damdinhdinh.taskreminder.database.DatabaseSQLite;
import com.example.damdinhdinh.taskreminder.model.GroupTask;

import java.util.List;

public class GroupTaskAdapter extends BaseAdapter{
    private Context context;
    private int layout;
    private List<GroupTask> arrGroupTask;
    private DatabaseSQLite database;
    private ShowPopup mListener;

    public GroupTaskAdapter(Context context, int layout, List<GroupTask> listGroupReminder, ShowPopup listener) {
        this.context = context;
        this.layout = layout;
        this.arrGroupTask = listGroupReminder;
        this.mListener = listener;
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

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.imgVerticalMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.showPopupMenu(context, view, i);
            }
        });
        GroupTask groupTask = arrGroupTask.get(i);
        holder.tvGroupTaskName.setText(groupTask.getName());
//        holder.imgGroupTaskIcon.setImageResource(groupTask.getIcon());
        holder.tvGroupTaskSize.setText(String.valueOf(groupTask.getArrTask().size()));

        return view;
    }

}
