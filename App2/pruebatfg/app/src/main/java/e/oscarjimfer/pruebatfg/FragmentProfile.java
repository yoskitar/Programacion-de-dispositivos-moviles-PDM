package e.oscarjimfer.pruebatfg;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FragmentProfile extends Fragment {

    private RecyclerView servicesRecyclerView;
    private TextView userName;
    private static ImageView userPhoto;
    private static Bitmap bitmapUserPhoto;
    private String userNameData;
    private SwipeRefreshLayout refreshLayoutProfile;
    private static ArrayList<ServiceModel> listServices;
    private View view;
    private static String TOKEN_USER;
    private static String ID_USER;
    private static String ID_PRODUCER;
    private static OkHttpClient client;
    private final Semaphore available = new Semaphore(0, true);
    private final Semaphore availableProfileInfo = new Semaphore(0, true);


    public FragmentProfile() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize elements -> encapsular en función como en todas las clases

        listServices = new ArrayList<>();
        TOKEN_USER = getArguments().getString(getResources().getString(R.string.TOKEN_USER));
        ID_USER = getArguments().getString(getResources().getString(R.string.ID_USER));
        ID_PRODUCER = getArguments().getString(getResources().getString(R.string.ID_PRODUCER));
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        //Obtenemos la información de perfil del usuario
        getUserInfo();
        //Obtenermos la lista de servicios del productor
        getServicesByProducer();
    }

    private void getUserInfo(){
        MyApolloClient.getMyApolloClient(TOKEN_USER).query(UserQuery.builder()._id(ID_USER).build())
                .enqueue(new ApolloCall.Callback<UserQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UserQuery.Data> response) {
                        userNameData = response.data().user.username;
                        getImgUser(MyApolloClient.getBaseUrlDownload(),"butterfly");
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }



    private void getServicesByProducer() {
         MyApolloClient.getMyApolloClient(TOKEN_USER).query(
                ServicesByProducerQuery.builder().producer(ID_PRODUCER).build())
                .enqueue(new ApolloCall.Callback<ServicesByProducerQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ServicesByProducerQuery.Data> response) {
                        Iterator<ServicesByProducerQuery.ServicesByProducer> it = response.data().servicesByProducer.iterator();
                        while (it.hasNext()){
                            ServicesByProducerQuery.ServicesByProducer serviceBP = it.next();
                            boolean semaforo = false;
                            if(response.data().servicesByProducer.indexOf(serviceBP) == response.data().servicesByProducer.size()-1){
                                semaforo = true;
                            }
                            getImgService(MyApolloClient.getBaseUrlDownload(), new ServiceModel(serviceBP._id, serviceBP.serviceName,
                                    serviceBP.description.info, serviceBP.description.photo,serviceBP.duration,
                                    serviceBP.createdAt, serviceBP.dateInit), semaforo);

                        }
                        if(response.data().servicesByProducer.isEmpty()){
                            available.release();
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);
        servicesRecyclerView = (RecyclerView) view.findViewById(R.id.RV_servicesProducer);
        userName = (TextView) view.findViewById(R.id.TV_userNameProducer);
        userPhoto = (ImageView) view.findViewById(R.id.IV_userPhoto);
        refreshLayoutProfile = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutProfile);

        try{
            availableProfileInfo.acquire();
            handler.postDelayed(prof,10);
        }catch (InterruptedException e){
            e.printStackTrace();
        }



        refreshLayoutProfile.setColorSchemeResources(R.color.notification_bar);
        refreshLayoutProfile.setProgressBackgroundColorSchemeResource(R.color.transparent);
        refreshLayoutProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new RefreshServicesList().execute();
                try {
                    available.acquire();
                    handler.postDelayed(r,10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        try {
            available.acquire();
            handler.postDelayed(r,10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return view;

    }

    private class RefreshServicesList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                listServices.clear();
                getServicesByProducer();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /*private class GetImageServices extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for(ServiceModel s:listServices){
                    getImgService(MyApolloClient.getBaseUrlDownload(),
                            new Pair<String, String>("imgName",s.get_id()+".jpg"),listServices.indexOf(s));
                    /*s.setImgService(getBitmapFromURL(MyApolloClient.getBaseUrlDownload()));*/
               /* }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //handler.postDelayed(r, 8000);
        }
    }*/

    public void getImgService(String url, final ServiceModel service, final boolean semaforo) {
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();

        httpBuider.addQueryParameter("imgName",service.get_id()+".jpg");

        Request request = new Request.Builder().url(httpBuider.build()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                service.setImgService(BitmapFactory
                        .decodeStream(response.body().byteStream()));
                listServices.add(service);
                if(semaforo){
                    available.release();
                }
            }
        });
    }

    public void getImgUser(String url, final String idUserProfile) {
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();

        httpBuider.addQueryParameter("imgName",idUserProfile+".jpg");

        Request request = new Request.Builder().url(httpBuider.build()).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                bitmapUserPhoto = BitmapFactory
                        .decodeStream(response.body().byteStream());
                availableProfileInfo.release();
            }
        });
    }

    final Handler handler = new Handler();
    final Runnable r = new Runnable() {
        public void run() {
            AdapterServices adapterServices = new AdapterServices(listServices);
            servicesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            servicesRecyclerView.setAdapter(adapterServices);
            refreshLayoutProfile.setRefreshing(false);
        }
    };

    final Runnable prof = new Runnable() {
        public void run() {
            userPhoto.setImageBitmap(bitmapUserPhoto);
            userName.setText(userNameData);
        }
    };


    /*public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("imgName","butterfly.jpg");
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }*/
}
