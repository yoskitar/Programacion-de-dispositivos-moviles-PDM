package e.oscarjimfer.pruebatfg;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class AdapterServices extends RecyclerView.Adapter<AdapterServices.ViewHolderWall> implements View.OnClickListener{

    ArrayList<ServiceModel> aList;
    private View.OnClickListener listener;

    public AdapterServices(ArrayList<ServiceModel> aList) {

        this.aList = aList;
    }

    @NonNull
    @Override
    public ViewHolderWall onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.service_item_list,viewGroup,false);
        view.setOnClickListener(this);
        return new ViewHolderWall(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWall viewHolderWall, int i) {
        viewHolderWall.serviceName.setText(aList.get(i).getServiceName());
        viewHolderWall.serviceDateInit.setText(aList.get(i).getDateInit());
        viewHolderWall.photo.setImageBitmap(aList.get(i).getImgService());
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

        TextView serviceName;
        TextView serviceDateInit;
        ImageView photo;

        public ViewHolderWall(@NonNull View itemView) {
            super(itemView);
            serviceName = (TextView) itemView.findViewById(R.id.ILS_serviceName);
            serviceDateInit = (TextView) itemView.findViewById(R.id.ILS_serviceDateInit);
            photo = (ImageView) itemView.findViewById(R.id.IV_serviceImg);
        }

    }
}