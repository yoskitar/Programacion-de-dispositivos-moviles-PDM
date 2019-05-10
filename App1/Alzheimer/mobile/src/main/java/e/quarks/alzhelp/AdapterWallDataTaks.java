package e.quarks.alzhelp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterWallDataTaks extends RecyclerView.Adapter<AdapterWallDataTaks.ViewHolderWall> implements View.OnClickListener{

    ArrayList<TaskModel> taskList;
    private View.OnClickListener listener;

    public AdapterWallDataTaks(ArrayList<TaskModel> taskList) {

        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolderWall onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list,viewGroup,false);
        view.setOnClickListener(this);
        return new ViewHolderWall(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWall viewHolderWall, int i) {
        viewHolderWall.titleTask.setText(taskList.get(i).getTitle());
        viewHolderWall.descriptionTask.setText(taskList.get(i).getDescription());
        viewHolderWall.dateTask.setText(taskList.get(i).getDateLimit());
        viewHolderWall.timeTask.setText(taskList.get(i).getTimeLimit());

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setOnClickListener(View.OnClickListener Listener){
        this.listener = Listener;
    }

    @Override
    public void onClick(View v) {

        if(listener!=null){
            listener.onClick(v);
        }
    }

    public static class ViewHolderWall extends RecyclerView.ViewHolder {

        TextView titleTask;
        TextView descriptionTask;
        TextView dateTask;
        TextView timeTask;

        public ViewHolderWall(@NonNull View itemView) {
            super(itemView);
            titleTask = (TextView) itemView.findViewById(R.id.idTitleTask);
            descriptionTask = (TextView) itemView.findViewById(R.id.idDescriptionTask);
            dateTask = (TextView) itemView.findViewById(R.id.idDateTask);
            timeTask = (TextView) itemView.findViewById(R.id.idTimeTask);
        }

    }
}
