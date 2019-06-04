package e.quarks.whoismore;

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
import android.widget.LinearLayout;
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

public class ResultActivity extends AppCompatActivity {


    //Declaración de elementos de IU
    private ArrayList<User> userList;
    private RecyclerView recyclerUsers;
    private AdapterWallDataUsers adapterUsers;
    private Button btnNewGame;

    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD

    private User currentUser;
    private Match currentMatch;
    private long numUsers;

    private ArrayMap<String,ArrayList<Pair<String,String>>> results;
    private Integer scoreUser;
    private boolean clasificated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initializeElements();
        fireBaseInitializer();
        getNumUsers();
        if(currentUser.isHost()){
            getResults();
        }

        getClasification();
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newgame = new Intent(
                        ResultActivity.this,
                        LobbyActivity.class);
                startActivity(newgame);
            }
        });
    }

    private void getNumUsers() {

        Query  qusers = refDB.child("Matches").child(currentMatch.getUid())
                .child("Jugadores").orderByKey();
        qusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numUsers = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void initializeElements() {

        scoreUser = 0;
        clasificated = false;
        results = new ArrayMap<>();
        btnNewGame = (Button) findViewById(R.id.btnNewGame);
        userList = new ArrayList<>();
        recyclerUsers = (RecyclerView) findViewById(R.id.recyclerResultUsers);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        currentMatch = getIntent().getParcelableExtra("currentMatch");
        currentUser = getIntent().getParcelableExtra("currentUser");
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer(){
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
    }

    private void getResults() {
        Query q = refDB.child("Matches").child(currentMatch.getUid()).child("Answers").orderByKey();

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String idQuestion = ds.getKey();
                    ArrayList<Pair<String,String>> usersResponses = new ArrayList<>();
                    for(DataSnapshot ds2 : ds.getChildren()){
                        Log.d("CHIL", String.valueOf(ds.getChildrenCount()));
                        String idUser = ds2.getKey();
                        String responseUser = ds2.child("respuesta").getValue(String.class);
                        Pair<String,String> userResponse = new Pair<>(idUser,responseUser);
                        usersResponses.add(userResponse);
                    }

                    results.put(idQuestion,usersResponses);
                    //Log.d("resul",String.valueOf(results.size()));
                }
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

            ClasificationResult clasificationRes = new ClasificationResult(idMostVoted,cntMostVoted);
            refDB.child("Matches").child(currentMatch.getUid()).child("Clasification").
                    child(String.valueOf(i))
                    .setValue(clasificationRes)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Log.d("CLASIFICATION", "Pregunta votada");
                            } else {

                            }
                        }
                    });

        }

        setUserScore();
    }

    private void setUserScore() {

        Query  q = refDB.child("Matches").child(currentMatch.getUid()).child("Clasification")
                .orderByKey();

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    final ClasificationResult r = ds.getValue(ClasificationResult.class);
                    //Log.d("SCORE", "NO ENTREA2");

                    Query  qa = refDB.child("Matches").child(currentMatch.getUid()).child("Answers")
                            .child(ds.getKey())
                            .child(currentUser.getUid()).child("respuesta").orderByKey();

                    qa.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            if(r.getIdUser().equals(dataSnapshot2.getValue(String.class))){
                                //Log.d("SCORE", "entraaa");
                                scoreUser++;
                                refDB.child("Matches").child(currentMatch.getUid()).child("ClasificationScore").
                                        child(currentUser.getUid())
                                        .setValue(new ClasificationResult(currentUser.getUid(),scoreUser))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Log.d("SCORE", "Score creada");
                                                } else {

                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void getClasification() {

        refDB.child("Matches").child(currentMatch.getUid()).child("ClasificationScore")
                .orderByChild("votes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!currentUser.isHost() && !clasificated){
                            setUserScore();
                            clasificated = true;
                        }
                        if(dataSnapshot.getChildrenCount() == numUsers){
                            ArrayList<ClasificationResult> userResultOrdered = new ArrayList<>();
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                ClasificationResult r = ds.getValue(ClasificationResult.class);
                                if(r!=null ){
                                    userResultOrdered.add(r);
                                }
                            }
                            if(!userResultOrdered.isEmpty()) {
                                for(ClasificationResult res : userResultOrdered){
                                    Query  q = refDB.child("Matches").child(currentMatch.getUid())
                                            .child("Jugadores").orderByKey().equalTo(res.getIdUser());

                                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(final DataSnapshot ds3 : dataSnapshot.getChildren()){
                                                User user = ds3.getValue(User.class);
                                                boolean existe = false;
                                                for(User u : userList){
                                                    if(u.getUid().equals(user.getUid())){
                                                        existe = true;
                                                    }
                                                }
                                                if(!existe){
                                                    userList.add(user);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                                adapterUsers = new AdapterWallDataUsers(userList);
                                recyclerUsers.setAdapter(adapterUsers);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
