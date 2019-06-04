package e.quarks.whoismore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

public class MatchActivity extends AppCompatActivity {

    //Declaración de elementos de IU
    private RelativeLayout rellaybtnInits;
    private ArrayList<User> userList;
    private RecyclerView recyclerUsers;
    private AdapterWallDataUsers adapterUsers;
    private Button btnInitMatch;
    private TextView questionShowed;

    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD

    private User currentUser;
    private Match currentMatch;
    private String currentQuestion;
    private boolean pass = true;
    ClasificationResult clasificationRes;

    private Status STATE;
    private boolean response;
    private ArrayMap<String,ArrayList<Pair<String,String>>> results;
    private Semaphore available = new Semaphore(0,true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        initializeElements();
        fireBaseInitializer();
        userListener();
        questionListener();
        stateListener();



        btnInitMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellaybtnInits.setVisibility(View.GONE);
                initMatch();
            }
        });
    }

    private void initializeElements() {
        STATE = Status.CREATED;
        currentQuestion = "Default";
        response = false;
        rellaybtnInits = (RelativeLayout) findViewById(R.id.rellaybtnInits);
        userList = new ArrayList<>();
        recyclerUsers = (RecyclerView) findViewById(R.id.recyclerUsers);
        recyclerUsers.setLayoutManager(new GridLayoutManager(this,2));
        currentMatch = getIntent().getParcelableExtra("currentMatch");
        currentUser = getIntent().getParcelableExtra("currentUser");
        btnInitMatch = (Button) findViewById(R.id.btnInitMatch);
        questionShowed = (TextView) findViewById(R.id.idquestionshow);
        results = new ArrayMap<>();
        if(currentUser.isHost()){
            rellaybtnInits.setVisibility(View.VISIBLE);
        }
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer(){
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
    }

    private void initMatch(){

        refDB.child("Matches").child(currentMatch.getName()).child("state")
                .setValue(Status.INITIALIZED)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Query  q = refDB.child("Questions").orderByChild("date");
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount()>0) {
                                        boolean cq = false;
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            if(!cq){
                                                currentQuestion = ds.getKey();
                                                cq = true;
                                            }
                                            Question question = ds.getValue(Question.class);
                                            refDB.child("Matches").child(currentMatch.getUid()).child("Answers")
                                                    .child(ds.getKey())
                                                    .setValue("Default")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                            } else {

                                                            }
                                                        }
                                                    });
                                        }
                                        refDB.child("Matches").child(currentMatch.getName())
                                                .child("currentQuestion")
                                                .setValue(currentQuestion)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            STATE = Status.INITIALIZED;
                                                            Toast.makeText(MatchActivity.this,
                                                                    "Partida inicializada correctamente",
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            STATE = Status.INITIALIZED;
                                                            Toast.makeText(MatchActivity.this,
                                                                    "No se inicializó correctamente",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }else{
                                        Toast.makeText(MatchActivity.this,
                                                "No hay preguntas en la BD",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        } else {
                            Toast.makeText(MatchActivity.this,
                                    "Error al inicializar la partida",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void userListener(){
        refDB.child("Matches").child(currentMatch.getUid()).child("Jugadores")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            User u = ds.getValue(User.class);
                            if(u!=null ){
                                userList.add(u);
                            }
                        }
                        if(!userList.isEmpty()) {
                            adapterUsers = new AdapterWallDataUsers(userList);
                            adapterUsers.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //RESPUESTA
                                    Log.d("STATE58","AQUI2"+STATE.toString()+currentQuestion);
                                    if(STATE == Status.INITIALIZED && currentQuestion!="Default"){
                                        Log.d("STATE58","AQUI"+STATE.toString()+currentQuestion);
                                        User responseUser = userList.get(recyclerUsers.getChildAdapterPosition(v));
                                        setResponse(responseUser);
                                    }
                                }
                            });
                            recyclerUsers.setAdapter(adapterUsers);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void setResponse(User user){
        if(!response) {
            refDB.child("Matches").child(currentMatch.getUid()).child("Answers")
                    .child(currentQuestion).child(currentUser.getUid()).child("respuesta")
                    .setValue(user.getUid())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                response = true;
                            } else {

                            }
                        }
                    });
        }
    }

    private void questionListener() {
        refDB.child("Matches").child(currentMatch.getName()).child("currentQuestion")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentQuestion = dataSnapshot.getValue(String.class);

                if(currentUser.isHost()){
                    allResponseListener();
                }
                loadCurrentQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCurrentQuestion() {
        if(currentQuestion!="Default"){
            Query  q = refDB.child("Questions").orderByKey().equalTo(currentQuestion);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()>0) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Question question = ds.getValue(Question.class);
                            questionShowed.setText(question.getQuestion());
                        }
                        response = false;
                    }else{
                        /*Toast.makeText(MatchActivity.this,
                                "No hay ninguna pregunta con este identificador: " + currentQuestion,
                                Toast.LENGTH_SHORT).show();*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


        }else{
            Toast.makeText(MatchActivity.this,
                    "Partida Finalizada",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void allResponseListener() {
        refDB.child("Matches").child(currentMatch.getUid()).child("Answers").child(currentQuestion)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Query  qans = refDB.child("Matches").child(currentMatch.getUid()).child("Answers")
                                .child(currentQuestion).orderByValue();
                        qans.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final long number_response = dataSnapshot.getChildrenCount();
                                //Log.d("PARTIDA",String.valueOf(number_response));
                                Query  qusers = refDB.child("Matches").child(currentMatch.getUid())
                                        .child("Jugadores").orderByKey();
                                qusers.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        /*Toast.makeText(MatchActivity.this,
                                                String.valueOf(number_response) + " - " +
                                                        String.valueOf(dataSnapshot.getChildrenCount()),
                                                Toast.LENGTH_SHORT).show();*/
                                        ////Log.d("PARTIDA",String.valueOf(dataSnapshot.getChildrenCount()));
                                        /*Log.d("PARTIDAEQUAL",String.valueOf(number_response)
                                                + " - " + String.valueOf(dataSnapshot.getChildrenCount()));*/
                                        if (dataSnapshot.getChildrenCount() == number_response){
                                            /*Log.d("PARTIDAEQUAL2",String.valueOf(number_response)
                                                    + " - " + String.valueOf(dataSnapshot.getChildrenCount()));*/
                                            getResults();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void nextQuestion() {
        Query  q = refDB.child("Matches").child(currentMatch.getUid()).child("Answers").orderByKey()
                .equalTo(String.valueOf((Integer.valueOf(currentQuestion)+1)));

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("NEXT","fuera");
                if(dataSnapshot.getChildrenCount()>0) {
                    Log.d("NEXT","entra");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        currentQuestion = ds.getKey();
                    }
                }else{
                    //Log.d("NEXT","else");
                    currentQuestion = "Default";
                    refDB.child("Matches").child(currentMatch.getName()).child("state")
                            .setValue(Status.FINISHED)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    } else {

                                    }
                                }
                            });
                }
                refDB.child("Matches").child(currentMatch.getName()).child("currentQuestion")
                        .setValue(currentQuestion)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                } else {

                                }
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void stateListener(){
        refDB.child("Matches").child(currentMatch.getName()).child("state")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        STATE = dataSnapshot.getValue(Status.class);
                        if(dataSnapshot.getValue(String.class).equals("FINISHED")){

                            Intent resultActivity = new Intent(
                                    MatchActivity.this,
                                    ResultActivity.class);
                            resultActivity.putExtra("currentMatch",currentMatch);
                            resultActivity.putExtra("currentUser",currentUser);
                            startActivity(resultActivity);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getResults() {
        Query q = refDB.child("Matches").child(currentMatch.getUid()).child("Answers").orderByKey();
        pass = true;
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(pass) {
                        String idQuestion = ds.getKey();
                        ArrayList<Pair<String, String>> usersResponses = new ArrayList<>();
                        for (DataSnapshot ds2 : ds.getChildren()) {
                            Log.d("CHIL", String.valueOf(ds.getChildrenCount()));
                            String idUser = ds2.getKey();
                            String responseUser = ds2.child("respuesta").getValue(String.class);
                            Pair<String, String> userResponse = new Pair<>(idUser, responseUser);
                            usersResponses.add(userResponse);
                        }

                        Log.d("resul", "antes" + String.valueOf(results.size()));
                        if (idQuestion.equals(currentQuestion)) {
                            results.put(idQuestion, usersResponses);
                            pass = false;
                        }
                        Log.d("resul", "despues" + String.valueOf(results.size()));
                    }
                }
                Log.d("resul","adespues2"+String.valueOf(results.size()));
                setWinnerResponseForQuestion();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setWinnerResponseForQuestion() {
        ArrayList<Pair<String,String>> result;
        ArrayMap<String, Integer> votes = new ArrayMap<>();
        Iterator<Pair<String,String>> itr;
        String idMostVoted;
        Integer cntMostVoted;
        String idv;
        Integer cntv;
        //Log.d("WINNERFUERA", String.valueOf(results.size()));
        for(int i=0 ; i<results.size(); i++){
            //Log.d("WINNER", String.valueOf(results.size()));
            votes.clear();
            result = results.valueAt(i);
            itr = result.iterator();


            while (itr.hasNext()){
                votes.put((itr.next()).first, 0);
            }
            itr = result.iterator();
            while (itr.hasNext()){
                Pair<String,String> p = itr.next();
                votes.put((p.second),votes.valueAt(votes.indexOfKey(p.second))+1);
            }

            idMostVoted = votes.keyAt(0);
            cntMostVoted = votes.get(idMostVoted);
            for(int j=0; j < votes.size(); j++){
                idv = votes.keyAt(j);
                cntv = votes.get(idv);

                if(cntv > cntMostVoted){
                    idMostVoted = idv;
                    cntMostVoted = cntv;
                }
            }

            clasificationRes = new ClasificationResult(idMostVoted,cntMostVoted);
            Query  q = refDB.child("Matches").child(currentMatch.getUid())
                    .child("Jugadores").orderByKey().equalTo(clasificationRes.getIdUser());

            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);
                    showAlertDialog("Bebe si no has votado a:",
                                "Jugador más votado: "+ user.getName() + "\n"
                                        + "Votes: " + clasificationRes.getVotes());
                    results.clear();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    }

    private void showAlertDialog(String title, String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("¡Ya he bebido!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        nextQuestion();
                    }
                });
        dialog.show();
    }

}
