package e.quarks.alzhelp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CarerMainActivity extends VoiceActivity {


    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD
    private FirebaseUser currentUser;       //Usuario actual
    private RelativeLayout rellaymain;
    private RelativeLayout rellayvinculate;
    private RelativeLayout rellayalerts;
    private RelativeLayout rellayusers;
    private RelativeLayout rellayresultsuser;
    private ArrayList<HelpModel> helpList;
    private ArrayList<User> userList;
    private ArrayList<ResultModel> resultsUserList;
    private RecyclerView recycler;
    private RecyclerView recyclerUsers;
    private RecyclerView recyclerResultsUser;
    private AdapterWallDataAlerts adapter;
    private AdapterWallDataUsers adapterUsers;
    private AdapterWallDataResultsUser adapterResultsUser;
    private Button btnAlerts;
    private Button btnAddUser;
    private Button btnAnalytics;
    private Button btnVinculate;
    private Button btnCancel;
    private ImageButton btnSpeak;
    private ImageButton backIcon;
    private ImageButton btnReturnAnalytics;
    private ImageButton btnReturnResults;
    private EditText inEmailV;
    private EditText inPinV;
    private User selectedUser;
    private HelpModel currentAlert;

    private final static String CHANNEL_ID = "NOTIFICATION";
    private final static int NOTIFICATION_ID = 0;

    // Variables gestión Chatbot
    private static final String LOGTAG = "CHATBOT";
    private static final Integer ID_PROMPT_QUERY = 0;
    private static final Integer ID_PROMPT_INFO = 1;
    private static final String DEBUG_TAG = "DEBUG";
    private long startListeningTime = 0; // To skip errors (see processAsrError method)



    // Idioma del sistena
    private static final String languages = Locale.getDefault().toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_main);

        initializeElements();
        fireBaseInitializer();
        updateWallAdapter();
        //Initialize the speech recognizer and synthesizer
        initSpeechInputOutput(this);

        CharSequence charSequence = getMessageText(getIntent());
        if(charSequence!=null) {
            String result = charSequence.toString().toLowerCase();
            String alertGeo = getIntent().getStringExtra("GEO");
            switch (result) {
                case "localizar":
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                    if (alertGeo != null) {
                        mapIntent.setData(Uri.parse(alertGeo));
                        startActivity(mapIntent);
                    } else {
                        Toast.makeText(this, "La localización del usuario" +
                                " no se encuentra disponible.", Toast.LENGTH_SHORT).show();
                    }


                    break;
                case "llamar":
                    Toast.makeText(this, "llamar", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ask the user to speak
                try {
                    speak(getResources().getString(R.string.initial_prompt), languages, ID_PROMPT_QUERY);
                } catch (Exception e) {
                    Log.e(LOGTAG, "TTS not accessible");
                }
            }
        });

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellaymain.setVisibility(View.GONE);
                rellayvinculate.setVisibility(View.VISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellayvinculate.setVisibility(View.GONE);
                rellaymain.setVisibility(View.VISIBLE);

            }
        });

        btnAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellaymain.setVisibility(View.GONE);
                rellayalerts.setVisibility(View.VISIBLE);

            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellayalerts.setVisibility(View.GONE);
                rellaymain.setVisibility(View.VISIBLE);
            }
        });

        btnAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersForAnalytics();
                rellaymain.setVisibility(View.GONE);
                rellayusers.setVisibility(View.VISIBLE);
            }
        });

        btnReturnAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellayusers.setVisibility(View.GONE);
                rellaymain.setVisibility(View.VISIBLE);
            }
        });

        btnReturnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellayresultsuser.setVisibility(View.GONE);
                rellayusers.setVisibility(View.VISIBLE);
            }
        });


        btnVinculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query q = refDB.child("Users").orderByChild("email").equalTo(inEmailV.getText().toString());

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            User user = ds.getValue(User.class);
                            if(user.getPinUser().equals(inPinV.getText().toString())){
                                if(user.getTypeUser().equals("A")) {
                                    refDB.child("Users").child(ds.getKey()).child("carer")
                                            .setValue(currentUser.getUid())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CarerMainActivity.this,
                                                                "Usuario vinculado con éxito",
                                                                Toast.LENGTH_SHORT).show();
                                                        rellayvinculate.setVisibility(View.GONE);
                                                        rellaymain.setVisibility(View.VISIBLE);
                                                        clearForms("Vinculate");
                                                    } else {
                                                        Toast.makeText(CarerMainActivity.this,
                                                                "Fallo de registro en la BD",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }else{
                                    Toast.makeText(CarerMainActivity.this,
                                            "No existe ningún usuario con el email indicado",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(CarerMainActivity.this,
                                        "El pin de identificación de usuario no es correcto",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CarerMainActivity.this,
                                "El usuario introducido no existe",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){

            Toast.makeText(CarerMainActivity.this,currentUser.getUid(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(CarerMainActivity.this,"Sesión cerrada, vuelva a logearse", Toast.LENGTH_SHORT).show();
            Intent loginActivity = new Intent(CarerMainActivity.this,LoginActivity.class);
            startActivity(loginActivity);
        }
    }

    //En esta función inicializaremos los elementos de UI definidos
    private void initializeElements(){
        rellaymain = (RelativeLayout) findViewById(R.id.rellaymainc);

        rellayvinculate = (RelativeLayout) findViewById(R.id.rellayvinculate);
        rellayalerts = (RelativeLayout) findViewById(R.id.rellayalerts);
        rellayusers = (RelativeLayout) findViewById(R.id.rellayusers);
        rellayresultsuser = (RelativeLayout) findViewById(R.id.rellayresultusers);

        recycler = (RecyclerView) findViewById(R.id.recyclerVHelp);
        recyclerUsers = (RecyclerView) findViewById(R.id.recyclerVAnalytics);
        recyclerResultsUser = (RecyclerView) findViewById(R.id.recyclerVResults);

        helpList = new ArrayList<>();
        userList = new ArrayList<>();
        resultsUserList = new ArrayList<>();


        recycler.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerResultsUser.setLayoutManager(new LinearLayoutManager(this));

        btnAlerts = (Button) findViewById(R.id.btnShowAlerts);
        btnAddUser = (Button) findViewById(R.id.btnAddUserToCarer);
        btnAnalytics = (Button) findViewById(R.id.btnAnalitycs);
        btnVinculate = (Button) findViewById(R.id.btnVinculateUserToCarer);
        btnCancel = (Button) findViewById(R.id.btn_cancelVinculateUser);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeech);
        backIcon = (ImageButton) findViewById(R.id.imgbtn_ReturnAlerts);
        btnReturnAnalytics = (ImageButton) findViewById(R.id.imgbtn_ReturnUsers);
        btnReturnResults = (ImageButton) findViewById(R.id.imgbtn_ReturnResultsUser);
        inEmailV = (EditText) findViewById(R.id.inEmailV);
        inPinV = (EditText) findViewById(R.id.inPin);
    }

    /**
     * Explain to the user why we need their permission to record audio on the device
     * See the checkASRPermission in the VoiceActivity class
     */
    public void showRecordPermissionExplanation() {
        Toast.makeText(getApplicationContext(), R.string.asr_permission, Toast.LENGTH_SHORT).show();
    }

    /**
     * If the user does not grant permission to record audio on the device, a message is shown and the app finishes
     */
    public void onRecordAudioPermissionDenied() {
        Toast.makeText(getApplicationContext(), R.string.asr_permission_notgranted, Toast.LENGTH_SHORT).show();
        System.exit(0);
    }

    /**
     * Starts listening for any user input.
     * When it recognizes something, the <code>processAsrResult</code> method is invoked.
     * If there is any error, the <code>onAsrError</code> method is invoked.
     */
    private void startListening() {

        if (deviceConnectedToInternet()) {
            try {

                /*Start listening, with the following default parameters:
                 * Language = English
                 * Recognition model = Free form,
                 * Number of results = 1 (we will use the best result to perform the search)
                 */
                startListeningTime = System.currentTimeMillis();
                listen(new Locale("ES"), RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, 1); //Start listening
            } catch (Exception e) {
                this.runOnUiThread(new Runnable() {  //Toasts must be in the main thread
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.asr_notstarted, Toast.LENGTH_SHORT).show();
                        changeButtonAppearanceToDefault();
                    }
                });

                Log.e(LOGTAG, "ASR could not be started");
                try {
                    speak(getResources().getString(R.string.asr_notstarted), languages, ID_PROMPT_INFO);
                } catch (Exception ex) {
                    Log.e(LOGTAG, "TTS not accessible");
                }

            }
        } else {

            this.runOnUiThread(new Runnable() { //Toasts must be in the main thread
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
                    changeButtonAppearanceToDefault();
                }
            });
            try {
                speak(getResources().getString(R.string.check_internet_connection), languages, ID_PROMPT_INFO);
            } catch (Exception ex) {
                Log.e(LOGTAG, "TTS not accessible");
            }
            Log.e(LOGTAG, "Device not connected to Internet");

        }
    }

    /**
     * Provides feedback to the user (by means of a Toast and a synthesized message) when the ASR encounters an error
     */
    @Override
    public void processAsrError(int errorCode) {

        changeButtonAppearanceToDefault();
        //Possible bug in Android SpeechRecognizer: NO_MATCH errors even before the the ASR
        // has even tried to recognized. We have adopted the solution proposed in:
        // http://stackoverflow.com/questions/31071650/speechrecognizer-throws-onerror-on-the-first-listening
        long duration = System.currentTimeMillis() - startListeningTime;
        if (duration < 500 && errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            Log.e(LOGTAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. Going to ignore the error");
            stopListening();
        } else {
            int errorMsg=R.string.asr_error_default;
            switch (errorCode) {

                case SpeechRecognizer.ERROR_AUDIO:
                    errorMsg = R.string.asr_error_audio;
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMsg = R.string.asr_error_permissions;
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMsg = R.string.asr_error_network;
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMsg = R.string.asr_error_networktimeout;
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMsg = R.string.asr_error_nomatch;
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMsg = R.string.asr_error_recognizerbusy;
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMsg = R.string.asr_error_server;
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMsg = R.string.asr_error_speechtimeout;
                    break;
                default:
                    errorMsg = R.string.asr_error; //Another frequent error that is not really due to the ASR, we will ignore it
                    break;
            }
            String msg = getResources().getString(errorMsg);
            this.runOnUiThread(new Runnable() { //Toasts must be in the main thread
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.asr_error, Toast.LENGTH_LONG).show();
                }
            });

            Log.e(LOGTAG, "Error when attempting to listen: " + msg);
            try {
                speak(msg, languages, ID_PROMPT_INFO);

            } catch (Exception e) {
                Log.e(LOGTAG, "TTS not accessible");
            }
        }
    }

    /**
     * Checks whether the device is connected to Internet (returns true) or not (returns false)
     * From: http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
     */
    public boolean deviceConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    /**
     * Synthesizes the best recognition result
     */
    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {

        if (nBestList != null) {

            Log.d(LOGTAG, "ASR best result: " + nBestList.get(0));

            if (nBestList.size() > 0) {
                changeButtonAppearanceToDefault();
                processAction(nBestList.get(0).toLowerCase()); //Send the best recognition hypothesis to the chatbot
            }
        }
    }

    private void processAction(String s) {
        switch (s) {
            case "mostrar alertas":
                btnAlerts.callOnClick();
                break;

            default:
                if(s.contains("mostrar estadísticas de")){
                    String name = s.substring(24);//Eliminamos el encabezado
                    getResultByName(name.toLowerCase());
                }
                break;
        }
    }

    private void getResultByName(final String name) {
        Query q = refDB.child("Users").orderByChild("name").equalTo(name).limitToFirst(1);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    user.setUid(ds.getKey());
                    rellaymain.setVisibility(View.GONE);
                    rellayresultsuser.setVisibility(View.VISIBLE);
                    getResultsForUser(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer(){
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    //Función para limpiar formulario
    private void clearForms(String form){
        switch (form){
            case "Vinculate":
                inEmailV.setText("");
                inPinV.setText("");
                break;
        }
    }

    private void getUsersForAnalytics(){
        Query q = refDB.child("Users").orderByChild("carer").equalTo(currentUser.getUid());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    user.setUid(ds.getKey());
                    userList.add(user);
                }
                adapterUsers = new AdapterWallDataUsers(userList);
                adapterUsers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedUser = userList.get(recyclerUsers.getChildAdapterPosition(v));
                        rellayusers.setVisibility(View.GONE);
                        rellayresultsuser.setVisibility(View.VISIBLE);
                        getResultsForUser(selectedUser);
                    }
                });
                recyclerUsers.setAdapter(adapterUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getResultsForUser(User user) {
        if(!user.getUid().equals("null")){
            Query qObj = refDB.child("Results").child(user.getUid()).child("Objects").orderByKey();
            Query qQuiz = refDB.child("Results").child(user.getUid()).child("Quiz").orderByKey();

            qObj.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    resultsUserList.clear();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        ResultModel r = ds.getValue(ResultModel.class);
                        r.setType("Object");
                        r.setDate(ds.getKey());
                        resultsUserList.add(r);
                    }
                    adapterResultsUser = new AdapterWallDataResultsUser(resultsUserList);
                    recyclerResultsUser.setAdapter(adapterResultsUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            qQuiz.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        ResultModel r = ds.getValue(ResultModel.class);
                        r.setType("Quiz");
                        r.setDate(ds.getKey());
                        resultsUserList.add(r);
                    }
                    adapterResultsUser = new AdapterWallDataResultsUser(resultsUserList);
                    recyclerResultsUser.setAdapter(adapterResultsUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else{
            Toast.makeText(CarerMainActivity.this,
                    "No existe usuario con el nombre:" + user.getName(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Invoked when the ASR is ready to start listening. Provides feedback to the user to show that the app is listening:
     * * It changes the color and the message of the speech button
     */
    @Override
    public void processAsrReadyForSpeech() {
        changeButtonAppearanceToListening();
    }

    /**
     * Provides feedback to the user to show that the app is listening:
     * * It changes the color and the message of the speech button
     */
    private void changeButtonAppearanceToListening() {
        Toast.makeText(CarerMainActivity.this,
                "Escuchando",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Provides feedback to the user to show that the app is idle:
     * * It changes the color and the message of the speech button
     */
    private void changeButtonAppearanceToDefault() {
        Toast.makeText(CarerMainActivity.this,
                "Escucha detenida",
                Toast.LENGTH_SHORT).show();
    }


    private void updateWallAdapter() {

        refDB.child("Help").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                helpList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    HelpModel t = ds.getValue(HelpModel.class);
                    if(t!=null && !t.getIdUser().equals("Default")){
                        helpList.add(t);
                        currentAlert = t;
                        notifyme();
                    }

                    adapter = new AdapterWallDataAlerts(helpList);
                    recycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onTTSDone(String uttId) {
        if (uttId.equals(ID_PROMPT_QUERY.toString())) {
            runOnUiThread(new Runnable() {
                public void run() {
                    startListening();
                }
            });
        }
    }


    /**
     * Invoked when the TTS encounters an error.
     * <p>
     * In this case it just writes in the log.
     */
    @Override
    public void onTTSError(String uttId) {
        Log.e(LOGTAG, "TTS error");
    }

    /**
     * Invoked when the TTS starts synthesizing
     * <p>
     * In this case it just writes in the log.
     */
    @Override
    public void onTTSStart(String uttId) {
        Log.d(LOGTAG, "TTS starts speaking");
    }


    public void notifyme(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Create an intent for the reply action
        Intent actionIntent = new Intent(this, NotificationDetails.class);
        if(!currentAlert.getIdUser().equals("")){
            actionIntent.putExtra("GEO",currentAlert.getGeo());
            actionIntent.putExtra("IDALERT",currentAlert.getIdUser());
        }

        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(this, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        String replyLabel = "My reply";
        String[] replyChoices = getResources().getStringArray(R.array.reply_choices);

        android.support.v4.app.RemoteInput remoteInput =
                new android.support.v4.app.RemoteInput.Builder(NotificationUtils.EXTRA_VOICE_REPLY)
                        .setLabel(replyLabel)
                        .setChoices(replyChoices)
                        .build();



        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.common_google_signin_btn_icon_light,
                        replyLabel, actionPendingIntent).addRemoteInput(remoteInput)
                        .build();


        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                        .setContentTitle("Help!")
                        .setContentText("An user need your help!")
                        .setContentIntent(actionPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().addAction(action))
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFICATION_ID,notification);
    }

    private CharSequence getMessageText(Intent intent){
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        if(bundle != null){
            return bundle.getCharSequence(NotificationUtils.EXTRA_VOICE_REPLY);
        }
        return null;
    }

}
