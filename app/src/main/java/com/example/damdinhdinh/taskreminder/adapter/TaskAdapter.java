package com.example.damdinhdinh.taskreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.damdinhdinh.taskreminder.ListTaskActivity;
import com.example.damdinhdinh.taskreminder.R;
import com.example.damdinhdinh.taskreminder.ShowPopup;
import com.example.damdinhdinh.taskreminder.database.DatabaseSQLite;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Task> arrTask;
    private DatabaseSQLite database;
    private ShowPopup mListener;

    public TaskAdapter(Context context, int layout, List<Task> arrTask, ShowPopup listener) {
        this.context = context;
        this.layout = layout;
        this.arrTask = arrTask;
        mListener = listener;
        database = new DatabaseSQLite(context, "task.sqlite", null, 1);
    }

    @Override
    public int getCount() {
        return arrTask.size();
    }

    @Override
    public Object getItem(int i) {
        return arrTask.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        TextView tvName;
        CheckBox cbIsDone;
        TextView tvTime;
        TextView tvDate;
        TextView tvRepeatType;
        ImageView imgMenuTask;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.tvName = view.findViewById(R.id.tv_reminder_name);
            holder.cbIsDone = view.findViewById(R.id.cb_reminder_is_done);
            holder.tvTime = view.findViewById(R.id.tv_reminder_time);
            holder.tvDate = view.findViewById(R.id.tv_reminder_date);
            holder.tvRepeatType = view.findViewById(R.id.tv_reminder_repeat_type);
            holder.imgMenuTask = view.findViewById(R.id.img_menu_item_task);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.imgMenuTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.showPopupMenu(context, view, i);
            }
        });
        holder.tvName.setText(arrTask.get(i).getName());
        holder.cbIsDone.setChecked(arrTask.get(i).isNotification());
        holder.tvTime.setText(arrTask.get(i).getTime24Hour());
        holder.tvDate.setText(arrTask.get(i).getDateYearMonth());
        holder.tvRepeatType.setText(arrTask.get(i).getRepeatType());

        return view;
    }
}
