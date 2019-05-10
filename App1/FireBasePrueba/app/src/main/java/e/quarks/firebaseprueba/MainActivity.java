package e.quarks.firebaseprueba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //Declaración de elementos de FIREBASE
    FirebaseDatabase databaseFB;    //Base de datos de FireBase
    DatabaseReference refDB;        //Referencia a BD
    //Declaración de elementos de UI
    EditText inUserName;
    EditText inEmail;
    EditText inPassword;
    Button btnAddNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeElements();
        fireBaseInitializer();

        btnAddNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    User usuario = new User(inUserName.getText().toString(), inEmail.getText().toString(),
                            inPassword.getText().toString());
                    refDB.child("User").child(usuario.getUid()).setValue(usuario);
                    Toast.makeText(MainActivity.this,"Registrado con éxito.", Toast.LENGTH_SHORT).show();
                    clearForms("registro");
                }

            }
        });
    }


    //Validación de entrada
    private boolean validation(){

        Boolean validate = true;
        if(inUserName.getText().toString().equals("")){
            inUserName.setError("Campo Requerido");
            validate = false;
        }
        if(inEmail.getText().toString().equals("")){
            inEmail.setError("Campo Requerido");
            validate = false;
        }
        if(inPassword.getText().toString().equals("")){
            inPassword.setError("Campo Requerido");
            validate = false;
        }

        return validate;
    }

    //Función para limpiar formulario
    private void clearForms(String id){
        switch (id){
            case "registro":
                inUserName.setText("");
                inEmail.setText("");
                inPassword.setText("");
                break;
        }
    }

    //En esta función inicializaremos los elementos de UI definidos
    private void initializeElements(){
        inUserName = findViewById(R.id.inUserName);
        inEmail = findViewById(R.id.inEmail);
        inPassword = findViewById(R.id.inPassword);
        btnAddNewUser = findViewById(R.id.btnAddNewUser);
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer(){
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
    }
}
