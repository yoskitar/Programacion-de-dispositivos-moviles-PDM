package e.quarks.whoismore;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD
    private ArrayList<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fireBaseInitializer();
        initQuestions();
    }

    private void initQuestions() {
        questionList = new ArrayList<>();
        questionList.add(new Question("¿Quién es más fuerte?"));
        questionList.add(new Question("¿Quién es más narciso?"));
        questionList.add(new Question("¿Quién es más aburrido?"));
        questionList.add(new Question("¿Quién es más guapo?"));
        questionList.add(new Question("¿Quién es más atrevido?"));
        questionList.add(new Question("¿Quién es más probable que vomite?"));
        questionList.add(new Question("¿Quién es más probable ligue?"));
        questionList.add(new Question("¿Quién es más tacaño?"));
        questionList.add(new Question("¿Quién es más perro?"));
        questionList.add(new Question("¿Quién es más fiestero?"));

        refDB.child("Questions")
                .setValue(questionList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Preguntas inicializadas correctamente",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }
                });
    }

    private void contarPreguntas(){
        Query qans = refDB.child("Questions").child("preguntas_malas").orderByKey();

        qans.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(MainActivity.this,
                        String.valueOf(dataSnapshot.getChildrenCount()),
                        Toast.LENGTH_SHORT).show();
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
    }
}
