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
import java.util.UUID;

public class QuizGameActivity extends AppCompatActivity {

    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD
    private FirebaseUser currentUser;       //Usuario actual
    private ArrayList<QuizModel> quiz;
    private ArrayList<Pair<String,Boolean>> answers;
    private Iterator<QuizModel> itq;
    private QuizModel actualQuiz;
    private EditText answer;
    private TextView quizShow;
    private TextView resultQuizShow;
    private Button btnAnswer;
    private Button btnReturn;
    private ResultModel result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_game);
        fireBaseInitializer();
        generateQuiz();
        answers = new ArrayList<>();
        result = new ResultModel();

        answer = (EditText) findViewById(R.id.inAnswer);
        quizShow = (TextView) findViewById(R.id.idquizshow);
        resultQuizShow = (TextView) findViewById(R.id.outQuiz);
        btnReturn = (Button) findViewById(R.id.ibtnCancel);
        btnAnswer = (Button) findViewById(R.id.ibtnAnswer);


        actualQuiz = itq.next();
        quizShow.setText(actualQuiz.getQuiz());

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(QuizGameActivity.this,MainActivity.class);
                startActivity(mainActivity);
            }
        });

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(answer.getText().toString().equalsIgnoreCase(actualQuiz.getAnswer())){
                    answers.add(new Pair<String, Boolean>(actualQuiz.getIdQuiz(),true));
                    resultQuizShow.setText("Respuesta correcta!");
                    result.incrementCorrect();
                }else{
                    answers.add(new Pair<String, Boolean>(actualQuiz.getIdQuiz(),false));
                    resultQuizShow.setText("Incorrecto, la respuesta era: " + actualQuiz.getAnswer());
                    result.incrementIncorrect();
                }
                if(itq.hasNext()){
                    actualQuiz = itq.next();
                    answer.setText("");
                    quizShow.setText(actualQuiz.getQuiz());
                }else{
                    Toast.makeText(QuizGameActivity.this,"Ocultar Botones y terminar", Toast.LENGTH_SHORT).show();
                    refDB.child("ResultsDetails").child(currentUser.getUid())
                            .child("Quiz").child(new Date().toString())
                            .setValue(answers)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                    }else{
                                        Toast.makeText(QuizGameActivity.this,
                                                "Fallo de registro en la BD de quiz results",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    refDB.child("Results").child(currentUser.getUid())
                            .child("Quiz").child(new Date().toString())
                            .setValue(result)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(QuizGameActivity.this,
                                                "Resultado registrado con éxito",
                                                Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(QuizGameActivity.this,
                                                "Fallo de registro en la BD de quiz results",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    Intent mainActivity = new Intent(QuizGameActivity.this,MainActivity.class);
                    startActivity(mainActivity);
                }
            }
        });


    }

    private void generateQuiz(){
        quiz = new ArrayList<>();
        quiz.add(new QuizModel("¿Cuál es la capital de Andalucía?","Sevilla","1"));
        quiz.add(new QuizModel("¿Cuál es la capital de España?","Madrid","2"));
        quiz.add(new QuizModel("¿Dónde se originaron los juegos olímpicos?","Grecia","3"));
        quiz.add(new QuizModel("¿En qué año terminó la II Guerra Mundial?","1945","4"));
        quiz.add(new QuizModel("¿En qué país se encuentra la torre de Pisa?","Madrid","5"));
        itq = quiz.iterator();
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer(){
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
