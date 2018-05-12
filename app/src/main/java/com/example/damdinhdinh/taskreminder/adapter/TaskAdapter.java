package com.example.damdinhdinh.taskreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.damdinhdinh.taskreminder.R;
import com.example.damdinhdinh.taskreminder.model.Task;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Task> arrTask;

    public TaskAdapter(Context context, int layout, List<Task> arrTask) {
        this.context = context;
        this.layout = layout;
        this.arrTask = arrTask;
    }

    @Override
    public int getCount() {
        return arrTask.size();
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
        TextView tvName;
        CheckBox cbIsDone;
        ImageView imgEdit;
        ImageView imgDelete;
        TextView tvTime;
        TextView tvDate;
        TextView tvRepeatType;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.tvName = view.findViewById(R.id.tv_reminder_name);
            holder.cbIsDone = view.findViewById(R.id.cb_reminder_is_done);
            holder.imgEdit = view.findViewById(R.id.img_icon_reminder_edit);
            holder.imgDelete = view.findViewById(R.id.img_icon_reminder_delete);
            holder.tvTime = view.findViewById(R.id.tv_reminder_time);
            holder.tvDate = view.findViewById(R.id.tv_reminder_date);
            holder.tvRepeatType = view.findViewById(R.id.tv_reminder_repeat_type);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.tvName.setText(arrTask.get(i).getName());
        holder.cbIsDone.setChecked(arrTask.get(i).isNotification());
        holder.imgEdit.setImageResource(R.drawable.icons8_edit_48);
        holder.imgDelete.setImageResource(R.drawable.icons8_delete_48);
        holder.tvTime.setText(arrTask.get(i).getTime24Hour());
        holder.tvDate.setText(arrTask.get(i).getDate());
        holder.tvRepeatType.setText(arrTask.get(i).getRepeatType());

        return view;
    }
}
