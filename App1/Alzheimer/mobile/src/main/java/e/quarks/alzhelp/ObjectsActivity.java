package e.quarks.alzhelp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ObjectsActivity extends AppCompatActivity {

    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD
    private FirebaseUser currentUser;       //Usuario actual

    private ArrayList<ObjectModel> objects;
    private ArrayList<Pair<String,Boolean>> answers;
    private Iterator<ObjectModel> ito;
    private ObjectModel actualObject;
    private Button btnYes;
    private Button btnNo;
    private Button btnNotNeedIt;
    private ImageButton btnReturn;
    private ImageView objectView;
    private ResultModel result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objects);
        fireBaseInitializer();
        generateObjects();
        answers = new ArrayList<>();
        result = new ResultModel();

        btnReturn = (ImageButton) findViewById(R.id.imgbtn_ReturnObj);
        btnYes = (Button) findViewById(R.id.btnYes);
        btnNo = (Button) findViewById(R.id.btnNo);
        btnNotNeedIt = (Button) findViewById(R.id.btnNotNeedIt);
        objectView = (ImageView) findViewById(R.id.imgVObj);

        actualObject = ito.next();
        objectView.setImageResource(actualObject.getPhoto());

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(ObjectsActivity.this,MainActivity.class);
                startActivity(mainActivity);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers.add(new Pair<String, Boolean>(actualObject.getNameObject(),false));
                result.incrementIncorrect();
                checkResultObject();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers.add(new Pair<String, Boolean>(actualObject.getNameObject(),true));
                result.incrementCorrect();
                checkResultObject();
            }
        });

        btnNotNeedIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers.add(new Pair<String, Boolean>(actualObject.getNameObject(),true));
                result.incrementCorrect();
                checkResultObject();
            }
        });
    }

    private void checkResultObject(){
        if(ito.hasNext()){
            actualObject = ito.next();
            objectView.setImageResource(actualObject.getPhoto());
        }else{
            refDB.child("ResultsDetails").child(currentUser.getUid())
                    .child("Objects").child(new Date().toString())
                    .setValue(answers)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                            }else{
                                Toast.makeText(ObjectsActivity.this,
                                        "Fallo de registro en la BD de objects results",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            refDB.child("Results").child(currentUser.getUid())
                    .child("Objects").child(new Date().toString())
                    .setValue(result)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ObjectsActivity.this,
                                        "Resultado registrado con éxito",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ObjectsActivity.this,
                                        "Fallo de registro en la BD de objects results",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            Intent mainActivity = new Intent(ObjectsActivity.this,MainActivity.class);
            startActivity(mainActivity);
        }
    }

    private void generateObjects(){
        objects = new ArrayList<>();
        objects.add(new ObjectModel("Llaves",R.drawable.llaves ));
        objects.add(new ObjectModel("Cartera",R.drawable.cartera ));
        objects.add(new ObjectModel("Gafas",R.drawable.gafas ));
        objects.add(new ObjectModel("Mochila",R.drawable.mochila ));
        ito = objects.iterator();
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer(){
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
