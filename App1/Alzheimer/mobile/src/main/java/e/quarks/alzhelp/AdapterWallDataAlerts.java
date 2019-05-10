package e.quarks.alzhelp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterWallDataAlerts extends RecyclerView.Adapter<AdapterWallDataAlerts.ViewHolderWall> implements View.OnClickListener{

    ArrayList<HelpModel> helpList;
    private View.OnClickListener listener;

    public AdapterWallDataAlerts(ArrayList<HelpModel> helpList) {

        this.helpList = helpList;
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
        viewHolderWall.titleTask.setText(helpList.get(i).getEmail());
        viewHolderWall.descriptionTask.setText(helpList.get(i).getMsg());
        viewHolderWall.dateTask.setText(helpList.get(i).getDate());

    }

    @Override
    public int getItemCount() {
        return helpList.size();
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

        public ViewHolderWall(@NonNull View itemView) {
            super(itemView);
            titleTask = (TextView) itemView.findViewById(R.id.idTitleTask);
            descriptionTask = (TextView) itemView.findViewById(R.id.idDescriptionTask);
            dateTask = (TextView) itemView.findViewById(R.id.idDateTask);
        }

    }
}
