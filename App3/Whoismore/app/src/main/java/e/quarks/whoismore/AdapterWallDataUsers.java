package e.quarks.whoismore;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterWallDataUsers extends RecyclerView.Adapter<AdapterWallDataUsers.ViewHolderWall> implements View.OnClickListener{

    ArrayList<User> aList;
    private View.OnClickListener listener;

    public AdapterWallDataUsers(ArrayList<User> aList) {

        this.aList = aList;
    }

    @NonNull
    @Override
    public ViewHolderWall onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_list,viewGroup,false);
        view.setOnClickListener(this);
        return new ViewHolderWall(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWall viewHolderWall, int i) {
        viewHolderWall.nameUser.setText(aList.get(i).getName());
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

        TextView nameUser;

        public ViewHolderWall(@NonNull View itemView) {
            super(itemView);
            nameUser = (TextView) itemView.findViewById(R.id.idNameUser);
        }

    }
}
