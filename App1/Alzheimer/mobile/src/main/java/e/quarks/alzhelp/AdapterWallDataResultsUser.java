package e.quarks.alzhelp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterWallDataResultsUser extends RecyclerView.Adapter<AdapterWallDataResultsUser.ViewHolderWall> implements View.OnClickListener{

    ArrayList<ResultModel> aList;
    private View.OnClickListener listener;

    public AdapterWallDataResultsUser(ArrayList<ResultModel> aList) {

        this.aList = aList;
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
        viewHolderWall.titleTask.setText(aList.get(i).getType());
        viewHolderWall.descriptionTask.setText("Correctas: " + aList.get(i).getCorrect() + "\nIncorrectas: " + aList.get(i).getIncorrect());
        viewHolderWall.dateTask.setText(aList.get(i).getDate());

    }

    @Override
    public int getItemCount() {
        return aList.size();
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
