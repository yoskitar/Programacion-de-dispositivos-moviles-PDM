package e.quarks.whoismore;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

public class LobbyActivity extends AppCompatActivity {

    //Declaración de elementos de IU
    RelativeLayout rellayLogin;
    RelativeLayout rellayLobby;
    Handler handler;

    Button btnLogin;
    Button btnCreateMatch;
    Button btnJoinMatch;

    EditText inUserName;
    EditText inMatchName;
    EditText inMatchPass;

    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD

    private User currentUser;
    private Match currentMatch;

    private boolean EXIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        initializeElements();
        fireBaseInitializer();
        handler.postDelayed(splashScreen,1000);       //Ejecutamos splash screen animation

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentUser = new User(inUserName.getText().toString(),false);
                rellayLogin.setVisibility(View.GONE);
                rellayLobby.setVisibility(View.VISIBLE);
            }
        });

        btnCreateMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMatch = new Match(inMatchName.getText().toString(),inMatchPass.getText().toString(),Status.CREATED);
                createMatch(); //Creamos currentMatch
            }
        });

        btnJoinMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMatch = new Match(inMatchName.getText().toString(),inMatchPass.getText().toString(),Status.CREATED);
                joinMatch(); //Accedemos a currentMatch
            }
        });
    }

    private void createMatch(){
        Query q = refDB.child("Matches").orderByKey().equalTo(inMatchName.getText().toString());

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==0) {
                    //Creamos la sala
                    refDB.child("Matches").child(currentMatch.getName())
                            .setValue(currentMatch)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Accedemos a la sala
                                        currentUser.setHost(true);
                                        refDB.child("Matches").child(currentMatch.getUid()).child("Jugadores")
                                                .child(currentUser.getUid())
                                                .setValue(currentUser)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(LobbyActivity.this,
                                                                    "Partida creada con éxito, esperando al" +
                                                                            " resto de jugadores...",
                                                                    Toast.LENGTH_SHORT).show();
                                                            //CARGAR ACTIVITY DE JUEGO
                                                            Intent matchActivity = new Intent(LobbyActivity.this,MatchActivity.class);
                                                            matchActivity.putExtra("currentMatch",currentMatch);
                                                            matchActivity.putExtra("currentUser",currentUser);
                                                            startActivity(matchActivity);

                                                        } else {
                                                            Toast.makeText(LobbyActivity.this,
                                                                    "Error al acceder a la sala",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(LobbyActivity.this,
                                                "Error al crear la partida",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(LobbyActivity.this,
                            "La sala indicada ya existe",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void joinMatch(){

        Query q = refDB.child("Matches").orderByKey().equalTo(inMatchName.getText().toString());

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Match match = ds.getValue(Match.class);
                        if(match.getState() == Status.CREATED) {
                            if (match.getPass().equals(inMatchPass.getText().toString())) {
                                currentMatch = match;
                                refDB.child("Matches").child(match.getUid()).child("Jugadores")
                                        .child(currentUser.getUid())
                                        .setValue(currentUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    EXIST = true;
                                                    Toast.makeText(LobbyActivity.this,
                                                            "Esperando al resto de jugadores...",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent matchActivity = new Intent(LobbyActivity.this,MatchActivity.class);
                                                    matchActivity.putExtra("currentMatch",currentMatch);
                                                    matchActivity.putExtra("currentUser",currentUser);
                                                    startActivity(matchActivity);
                                                } else {
                                                    Toast.makeText(LobbyActivity.this,
                                                            "Error al acceder a la sala",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(LobbyActivity.this,
                                        "Contraseña incorrecta",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(LobbyActivity.this,
                                        "La partida ya se ha iniciado",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(LobbyActivity.this,
                            "La sala indicada no existe",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void initializeElements() {
        //Elementos de UI de Login
        rellayLogin = (RelativeLayout) findViewById(R.id.rellayLogin);
        handler = new Handler();
        btnLogin = (Button) findViewById(R.id.btnLogIn);
        inUserName = (EditText) findViewById(R.id.inUserName);

        //Elementos de UI de registro
        rellayLobby = (RelativeLayout) findViewById(R.id.rellaySelectMatch);
        btnCreateMatch = (Button) findViewById(R.id.btnCreateMatch);
        btnJoinMatch = (Button) findViewById(R.id.btnJoinMatch);
        inMatchName = (EditText) findViewById(R.id.inNameMatch);
        inMatchPass = (EditText) findViewById(R.id.inPassMatch);
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer(){
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
    }

    Runnable splashScreen = new Runnable() {
        @Override
        public void run() {
            rellayLogin.setVisibility(View.VISIBLE);
        }
    };
}
