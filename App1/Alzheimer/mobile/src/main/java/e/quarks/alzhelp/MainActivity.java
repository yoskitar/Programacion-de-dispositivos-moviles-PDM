package e.quarks.alzhelp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends LoginActivity {

    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD
    private FirebaseUser currentUser;       //Usuario actual
    //Declaración de elementos de UI
    private RelativeLayout rellaynoItem;
    private RelativeLayout rellayItems;
    private RelativeLayout rellayDetailItem;
    private RelativeLayout rellaybtnTask;
    private RelativeLayout rellaybtnTaskRemove;
    private ArrayList<TaskModel> taskList;
    private RecyclerView recycler;
    private AdapterWallDataTaks adapter;
    private Button btnAddTask;
    private Button btnObjects;
    private Button btnCancelRemoveTask;
    private Button btnRemoveTask;
    private Button btnQuizGame;
    private Button btnAFHelp;
    private Button btnCreateTask;
    private Button btnCancelTask;
    private ImageButton btnPinUser;
    private EditText inTitle;
    private EditText inDescription;
    private EditText inDate;
    private EditText inTime;
    private String idCarer;
    private TaskModel selectedTask;
    LocationManager locationManager;
    private FusedLocationProviderClient client;
    private String locationS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeElements();
        fireBaseInitializer();
        requestLocationPermission();
        updateWallAdapter();
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellayItems.setVisibility(View.GONE);
                rellaynoItem.setVisibility(View.GONE);
                rellayDetailItem.setVisibility(View.VISIBLE);
                rellaybtnTask.setVisibility(View.VISIBLE);
            }
        });

        btnObjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objectsActivity = new Intent(MainActivity.this, ObjectsActivity.class);
                startActivity(objectsActivity);
            }
        });


        btnQuizGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quizActivity = new Intent(MainActivity.this, QuizGameActivity.class);
                startActivity(quizActivity);
            }
        });

        btnAFHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                Query help = refDB.child("Users").orderByChild("email").equalTo(currentUser.getEmail());
                help.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            User user = ds.getValue(User.class);
                            idCarer = user.getCarer();
                            if (!user.getCarer().equals("Default")) {
                                idCarer = user.getCarer();
                                refDB.child("Help").child(idCarer).child(currentUser.getUid())
                                        .setValue(new HelpModel(currentUser.getEmail(),currentUser.getUid(), locationS, "¡Necesito Ayuda!"))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this,
                                                            "Hemos avisado a su cuidador",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MainActivity.this,
                                                            "Fallo al pedir ayuda",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {

                                Toast.makeText(MainActivity.this,
                                        "No hay personal a su cuidado",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        btnRemoveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refDB.child("Tasks").child(currentUser.getUid()).child(selectedTask.getId())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cancelTask();
                                    Toast.makeText(MainActivity.this,
                                            "Tarea eliminada con éxito",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "Fallo de eliminación de registro en la BD",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        btnCancelRemoveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTask();
            }
        });

        btnPinUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query help = refDB.child("Users").orderByChild("email").equalTo(currentUser.getEmail());
                help.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()>0){
                            User u = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                            showAlertDialog("Información para vinculación de usuario",
                                    "Email: "+ u.getEmail() + "\n" + "Pin: " + u.getPinUser());
                        }else{
                            Toast.makeText(MainActivity.this,
                                    "No se puede recuperar el pin de usuario",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask(new TaskModel(inTitle.getText().toString(), inDate.getText().toString(),
                        inTime.getText().toString(), inDescription.getText().toString()));

                rellaynoItem.setVisibility(View.GONE);
                rellayDetailItem.setVisibility(View.GONE);
                rellaybtnTask.setVisibility(View.GONE);
                rellayItems.setVisibility(View.VISIBLE);
                clearForms("New Task");

            }
        });

        btnCancelTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTask();
            }
        });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
    }

    private void updateWallAdapter() {

        refDB.child("Tasks").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TaskModel t = ds.getValue(TaskModel.class);
                    if (t != null && !t.getTitle().equals("Default")) {
                        taskList.add(t);
                    }
                }
                if (taskList.isEmpty()) {
                    rellayDetailItem.setVisibility(View.GONE);
                    rellayItems.setVisibility(View.GONE);
                    rellaynoItem.setVisibility(View.VISIBLE);
                } else {

                    rellayDetailItem.setVisibility(View.GONE);
                    rellayItems.setVisibility(View.VISIBLE);
                    rellaynoItem.setVisibility(View.GONE);
                    adapter = new AdapterWallDataTaks(taskList);
                    adapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rellayItems.setVisibility(View.GONE);
                            rellaynoItem.setVisibility(View.GONE);
                            rellayDetailItem.setVisibility(View.VISIBLE);
                            rellaybtnTaskRemove.setVisibility(View.VISIBLE);
                            selectedTask = taskList.get(recycler.getChildAdapterPosition(v));
                            loadTask(selectedTask);
                        }
                    });
                    recycler.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createTask(TaskModel task) {
        refDB.child("Tasks").child(currentUser.getUid()).child(task.getId())
                .setValue(task)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Tarea registrada con éxito",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Fallo de registro en la BD",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //En esta función inicializaremos los elementos de UI definidos
    private void initializeElements() {
        rellaynoItem = (RelativeLayout) findViewById(R.id.rellaynoitem);
        rellayItems = (RelativeLayout) findViewById(R.id.rellayitem);
        rellayDetailItem = (RelativeLayout) findViewById(R.id.rellayitemdetail);
        rellaybtnTask = (RelativeLayout) findViewById(R.id.rellaybtntask);
        rellaybtnTaskRemove = (RelativeLayout) findViewById(R.id.rellaybtntaskremove);
        recycler = (RecyclerView) findViewById(R.id.recyclerVWall);
        taskList = new ArrayList<>();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        btnAddTask = (Button) findViewById(R.id.btnAddTask);
        btnObjects = (Button) findViewById(R.id.btnObjects);
        btnQuizGame = (Button) findViewById(R.id.btnQuizGame);
        btnAFHelp = (Button) findViewById(R.id.btnAskForHelp);
        btnCreateTask = (Button) findViewById(R.id.btn_createTask);
        btnCancelTask = (Button) findViewById(R.id.btn_cancelAddTask);
        btnCancelRemoveTask = (Button) findViewById(R.id.btn_cancelRemoveTask);
        btnRemoveTask = (Button) findViewById(R.id.btn_removeTask);
        btnPinUser = (ImageButton) findViewById(R.id.imageButtonPinUser);
        inTitle = (EditText) findViewById(R.id.intitleTask);
        inDescription = (EditText) findViewById(R.id.indescriptionTask);
        inDate = (EditText) findViewById(R.id.indateTask);
        inTime = (EditText) findViewById(R.id.intimeTask);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        client = LocationServices.getFusedLocationProviderClient(this);
        locationS = null;
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer() {
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void loadTask(TaskModel t) {
        if (t != null) {
            inTitle.setText(t.getTitle());
            inDescription.setText(t.getDescription());
            inTime.setText(t.getTimeLimit());
            inDate.setText(t.getDateLimit());
        }
    }

    private void cancelTask() {
        rellayDetailItem.setVisibility(View.GONE);
        rellaybtnTask.setVisibility(View.GONE);
        rellaybtnTaskRemove.setVisibility(View.GONE);
        if (taskList.isEmpty()) {
            rellayItems.setVisibility(View.GONE);
            rellaynoItem.setVisibility(View.VISIBLE);
        } else {
            rellaynoItem.setVisibility(View.GONE);
            rellayItems.setVisibility(View.VISIBLE);
        }

        clearForms("New Task");
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            //Toast.makeText(MainActivity.this, currentUser.getUid(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Sesión cerrada, vuelva a logearse", Toast.LENGTH_SHORT).show();
            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginActivity);
        }
    }

    //Función para limpiar formulario
    private void clearForms(String form) {
        switch (form) {
            case "New Task":
                inTitle.setText("");
                inDescription.setText("");
                inDate.setText("");
                inTime.setText("");
                break;
        }
    }

    private void showAlertDialog(String title, String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("¡Listo!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    locationS = "geo:"+location.getLatitude()+","+location.getLongitude();
                }
            }
        });
    }

}
