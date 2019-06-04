package e.oscarjimfer.pruebatfg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    ImageView userPhoto;
    Button sendImage;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    private String pathPickedImg;
    private static String  tokenUser;
    private static String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tokenUser = getIntent().getStringExtra("TOKEN-USER");
        idUser = getIntent().getStringExtra("ID-USER");
        Toast.makeText(MainActivity.this,tokenUser + " - " + idUser,
                Toast.LENGTH_SHORT).show();

        /*userPhoto = findViewById(R.id.IV_userPhoto);
        sendImage = findViewById(R.id.BT_sendImage);

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }else{
                    openGallery();
                }
            }
        });*/

    }
/*
    private void sendImage() {
            try {
                /*String imgPath = Environment.getExternalStorageDirectory().toString()
                        +"/Pictures/Instagram/IMG_20190129_184437_152.jpg";*//*
                File imgFileToSend = new File(pathPickedImg);
                if (imgFileToSend.exists() && imgFileToSend.isFile()) {

                    MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("operations",
                                    "{ \"query\": \"mutation ($file: Upload!) " +
                                            "{ singleUpload(file: $file) { id } }\", \"variables\": " +
                                            "{ \"file\": \"0\" } }")
                            .addFormDataPart("map", "{\"0\":[\"variables.file\"]}")
                            .addFormDataPart("0", imgFileToSend.getName(),
                                    RequestBody.create(MediaType.parse("image/jpg"), imgFileToSend));


                    Request sendImgRequest = new Request.Builder()
                            .url(MyApolloClient.getBaseUrl())
                            .post(multipartBodyBuilder.build())
                            .header("Content-Type", "application/json")
                            .build();

                    new OkHttpClient().newCall(sendImgRequest)
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Log.d("SEND-IMG", "Image sent: " + response.message());
                                    } else {
                                        Log.d("ERROR-SEND-IMG", response.body().toString());
                                    }
                                }
                            });
                }
            }catch(Exception e){
                e.printStackTrace();
            }
    }
*//*
    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(MainActivity.this,"Accept permissions",Toast.LENGTH_SHORT).show();

            }else{
                ActivityCompat.requestPermissions(
                        MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode
                        );
            }
        }else{
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        //seleccionar archivos con mimetype image/* (todas extensiones de imágenes)
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
            pickedImgUri = data.getData();
            userPhoto.setImageURI(pickedImgUri);

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = getContentResolver().query(pickedImgUri, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            //Get the column index of MediaStore.Images.Media.DATA
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //Gets the String value in the column
            pathPickedImg = cursor.getString(columnIndex);
            cursor.close();
        }
    }

    public void getUsers() {
        MyApolloClient.getMyApolloClient().query(
                UsersQuery.builder().build()).
                enqueue(new ApolloCall.Callback<UsersQuery.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<UsersQuery.Data> response) {
                        Log.d("GRAPHQLU", response.data().users().get(0).username);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("GRAPHQL", e.getMessage());
                    }
                });
    }

    public void getConsumers() {
        MyApolloClient.getMyApolloClient().query(
                ConsumersQuery.builder().build()).
                enqueue(new ApolloCall.Callback<ConsumersQuery.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<ConsumersQuery.Data> response) {
                        Log.d("GRAPHQLCS", response.data().consumers().get(0).user_id().username);
                        getConsumer(response.data().consumers().get(0).user_id()._id);

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("GRAPHQL", e.getMessage());
                    }
                });
    }

    public void getConsumer(String  idUser) {
        MyApolloClient.getMyApolloClient().query(
                ConsumerQuery.builder().user_id(idUser).build()).
                enqueue(new ApolloCall.Callback<ConsumerQuery.Data>() {

                    @Override
                    public void onResponse(@NotNull Response<ConsumerQuery.Data> response) {
                        Log.d("GRAPHQLC", response.data().consumer().user_id.username);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("GRAPHQL", e.getMessage());
                    }
                });
    }
*/

}