package e.oscarjimfer.pruebatfg;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import e.oscarjimfer.pruebatfg.type.DescriptionData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static e.oscarjimfer.pruebatfg.MainActivity.PReqCode;
import static e.oscarjimfer.pruebatfg.MainActivity.REQUESCODE;

public class FragmentNewService extends Fragment{


    private EditText in_serviceName;
    private EditText in_serviceDescription;
    private TextView in_serviceDateInit;
    private TextView in_serviceDateEnd;
    private TextView in_numPasses;
    private ImageView in_imageService;
    private Button btn_registerService;
    private View view;
    private static String TOKEN_USER;
    private static String ID_USER;
    private static String ID_PRODUCER;
    private static int durationService;
    private static int generalizedHourInitEvent;
    private static String dateStartEvent;

    private DatePickerDialog.OnDateSetListener mDateInitSetListener;
    private DatePickerDialog.OnDateSetListener mDateEndSetListener;
    Uri pickedImgUri;
    private String pathPickedImg;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TOKEN_USER = getArguments().getString(getResources().getString(R.string.TOKEN_USER));
        ID_USER = getArguments().getString(getResources().getString(R.string.ID_USER));
        ID_PRODUCER = getArguments().getString(getResources().getString(R.string.ID_PRODUCER));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.new_event_fragment,container, false);
        in_serviceName = (EditText) view.findViewById(R.id.inServiceName);
        in_serviceDescription = (EditText) view.findViewById(R.id.inServiceDescription);
        in_serviceDateInit = (TextView) view.findViewById(R.id.inServiceDateInit);
        in_serviceDateEnd = (TextView) view.findViewById(R.id.inServiceDateEnd);
        in_numPasses = (TextView) view.findViewById(R.id.In_numPasses);
        in_imageService = (ImageView) view.findViewById(R.id.inImageService);
        btn_registerService = (Button) view.findViewById(R.id.btnRegisterService);


        btn_registerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(durationService>0 && cheackServiceFields()) {
                    MyApolloClient.getMyApolloClient(TOKEN_USER).mutate(RegisterServiceMutation.builder()
                            .description(DescriptionData.builder().info(in_serviceDescription.getText()
                                    .toString()).photo("default").build())
                            .producer(ID_PRODUCER)
                            .dateInit(dateStartEvent)
                            .serviceName(in_serviceName.getText().toString())
                            .duration(durationService)
                            .build()).enqueue(new ApolloCall.Callback<RegisterServiceMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<RegisterServiceMutation.Data> response) {

                            if(response.data().registerService.success){
                                final String service_id = response.data().registerService.service._id;
                                sendImage(service_id);
                                MyApolloClient.getMyApolloClient(TOKEN_USER).mutate(RegisterMicroServiceMutation
                                        .builder()
                                        .microServiceName(getResources().getString(R.string.PASS_TYPE))
                                        .service_id(service_id).build())
                                        .enqueue(new ApolloCall.Callback<RegisterMicroServiceMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<RegisterMicroServiceMutation.Data> response) {
                                        if(response.data().registerMicroService.success){
                                            MyApolloClient.getMyApolloClient(TOKEN_USER).mutate(RegisterTypeMicroServiceMutation
                                                    .builder()
                                                    .microService_id(response.data().registerMicroService.microService._id)
                                                    .build()).enqueue(new ApolloCall.Callback<RegisterTypeMicroServiceMutation.Data>() {
                                                @Override
                                                public void onResponse(@NotNull Response<RegisterTypeMicroServiceMutation.Data> response) {
                                                    if(response.data().registerTypeMicroService.success){
                                                        String num_passes = in_numPasses.getText().toString();
                                                        for(int i =0; i < Integer.valueOf(num_passes); i++) {
                                                            MyApolloClient.getMyApolloClient(TOKEN_USER).mutate(RegisterPassMutation
                                                                    .builder()
                                                                    .service_id(service_id)
                                                                    .numPass(i)
                                                                    .build()).enqueue(new ApolloCall.Callback<RegisterPassMutation.Data>() {
                                                                @Override
                                                                public void onResponse(@NotNull Response<RegisterPassMutation.Data> response) {
                                                                   // clearForm();
                                                                }

                                                                @Override
                                                                public void onFailure(@NotNull ApolloException e) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NotNull ApolloException e) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {

                        }
                    });
                }
            }
        });

        in_imageService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }else{
                    openGallery();
                }
            }
        });

        in_serviceDateInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar dateInit = Calendar.getInstance();
                int year = dateInit.get(Calendar.YEAR);
                int month = dateInit.get(Calendar.MONTH);
                int day = dateInit.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialogBirthday = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                        mDateInitSetListener,
                        year, month, day);
                dialogBirthday.show();
            }
        });

        mDateInitSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dateInit = dayOfMonth + "/" + (month+1) + "/" + year;
                dateStartEvent = (month+1) + "/" + (dayOfMonth+1) + "/" + year; //Formato para la base de datos(año/mes/dia)
                in_serviceDateInit.setText(dateInit);
                generalizedHourInitEvent = year*365*24 + month*31*24 + dayOfMonth*24;
            }
        };

        in_serviceDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar dateInit = Calendar.getInstance();
                int year = dateInit.get(Calendar.YEAR);
                int month = dateInit.get(Calendar.MONTH);
                int day = dateInit.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialogBirthday = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                        mDateEndSetListener,
                        year, month, day);
                dialogBirthday.show();
            }
        });

        mDateEndSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dateEnd = dayOfMonth + "/" + month + "/" + year;
                in_serviceDateEnd.setText(dateEnd);
                durationService = year*365*24 + month*31*24 + dayOfMonth*24 - generalizedHourInitEvent;
            }
        };

        return view;
    }

    private void clearForm() {
        in_imageService.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_new_event));
        in_numPasses.setText("");
        in_serviceDescription.setText("");
        in_serviceDateInit.setText("");
        in_serviceDateEnd.setText("");
        in_serviceName.setText("");
    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getContext(),"Accept permissions",Toast.LENGTH_SHORT).show();

            }else{
                ActivityCompat.requestPermissions(
                        getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data != null){
            pickedImgUri = data.getData();
            in_imageService.setImageURI(pickedImgUri);

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // Get the cursor
            Cursor cursor = getActivity().getApplicationContext().getContentResolver()
                    .query(pickedImgUri, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            //Get the column index of MediaStore.Images.Media.DATA
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //Gets the String value in the column
            pathPickedImg = cursor.getString(columnIndex);
            cursor.close();
        }
    }

    private boolean cheackServiceFields(){

        Boolean required = true;
        if(in_serviceName.getText().toString().equals("")){
            in_serviceName.setError(getString(R.string.required_field));
            required = false;
        }
        if(in_serviceDateInit.getText().toString().equals("")){
            in_serviceDateInit.setError(getString(R.string.required_field));
            required = false;
        }
        if(in_serviceDateEnd.getText().toString().equals("")){
            in_serviceDateEnd.setError(getString(R.string.required_field));
            required = false;
        }
        if(in_serviceDescription.getText().toString().equals("")){
            in_serviceDescription.setError(getString(R.string.required_field));
            required = false;
        }
        if(in_numPasses.getText().toString().equals("")){
            in_serviceDescription.setError(getString(R.string.required_field));
            required = false;
        }
        return required;
    }

    public FragmentNewService() {
    }

    private void sendImage(String idService) {
        try {
            /*String imgPath = Environment.getExternalStorageDirectory().toString()
                    +"/Pictures/Instagram/IMG_20190129_184437_152.jpg";*/

            //final File imgFileToSendTemp = new File(pathPickedImg);
            final File imgFileToSend = new File(pathPickedImg);
            /*String tempPath = pathPickedImg.replace(
                    pathPickedImg.split("/")[pathPickedImg.split("/").length-1],
                    idService);
            final File tempToSend = new File(tempPath);
            imgFileToSend.renameTo(tempToSend);*/
            Log.d("FILESNAMES",imgFileToSend.getName()+ " -> path: " + imgFileToSend.getAbsolutePath());


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
}
