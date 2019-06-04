package e.quarks.alzhelp;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    RelativeLayout rellay1;
    RelativeLayout rellay2;
    RelativeLayout rellayLogin;
    RelativeLayout rellayRegister;
    Handler handler;

    //Botones de login
    Button btnLogin;
    Button btnRegister;
    Button btnForgotPassword;
    //Botones de registro
    Button btnSignUp;
    ImageButton imgbtnReturn;
    Switch swTypeUser;

    //EditText de login
    EditText inEmail;
    EditText inPassword;
    //EditText de registro
    EditText inEmailReg;
    EditText inPasswordReg;
    EditText inConfPasswordReg;
    EditText inUserNameReg;
    EditText inSurnameReg;
    EditText inPhoneUserReg;

    //Variables de estado
    private boolean regInitialized;
    private static final int  NUMERLENGTH = 9;

    //Declaración de elementos de FIREBASE
    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD
    FirebaseAuth mAuth;             //Autorización de usuario
    FirebaseUser currentUser;       //Usuario actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeElements("login");                    //Inicializamos elementos de la UI
        handler.postDelayed(splashScreen,1000);       //Ejecutamos splash screen animation
        fireBaseInitializer();  //Inicializamos referencias a Firebase y BD


        //Métodos OnClickListener para botones definidos
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this,"Login Button.", Toast.LENGTH_SHORT).show();
                if(cheackLog()) {
                    signIn();
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!regInitialized) {
                    initializeElements("registro");
                }
                rellayLogin.setVisibility(View.GONE);
                rellayRegister.setVisibility(View.VISIBLE);
                //Toast.makeText(LoginActivity.this,"Register Button.", Toast.LENGTH_SHORT).show();
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this,"Forgot Password Button.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validation()) {                  //Si se valida el formulario, procedemos al registro
                    String typerUser = "A";
                    if(swTypeUser.isChecked()){
                        typerUser = "C";
                    }
                    User usuario = new User(inUserNameReg.getText().toString(),
                            inSurnameReg.getText().toString(),
                            inEmailReg.getText().toString(),
                            typerUser,
                            inPhoneUserReg.getText().toString());
                    createAccount(usuario);   //Si se registra con exito, limpiamos y vamos al login
                }
            }
        });

        imgbtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellayRegister.setVisibility(View.GONE);
                rellayLogin.setVisibility(View.VISIBLE);
                //Toast.makeText(LoginActivity.this,"Volviendo al login", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(inEmail.getText().toString(), inPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            currentUser = mAuth.getCurrentUser();
                            Query help = refDB.child("Users").orderByChild("email").equalTo(currentUser.getEmail());
                            help.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                        User user = ds.getValue(User.class);
                                        //Toast.makeText(LoginActivity.this,"Acceso autorizado.", Toast.LENGTH_SHORT).show();
                                        if(user.getTypeUser().equals("C")){

                                            Intent mainUserCActivity = new Intent(LoginActivity.this,CarerMainActivity.class);
                                            startActivity(mainUserCActivity);
                                        }else{
                                            Intent mainUserAActivity = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(mainUserAActivity);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    //Registro del usuario en la BD de Firebase
    //Devolvemos el estado del registro
    private void createAccount(final User usuario) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(inEmailReg.getText().toString(), inPasswordReg.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            //Creamos un usuario en la colección 'users'
                            refDB.child("Users").child(currentUser.getUid())
                                    .setValue(usuario)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                            }else{
                                                Toast.makeText(LoginActivity.this,
                                                        "Fallo de registro en la BD de users",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            if(usuario.getTypeUser().equals("C")){
                                refDB.child("Help").child(currentUser.getUid())
                                        .child(UUID.randomUUID().toString())
                                        .setValue(new HelpModel("Default","Default Alert"))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                }else{
                                                    Toast.makeText(LoginActivity.this,
                                                            "Fallo de registro en la BD de users",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            refDB.child("Tasks").child(currentUser.getUid())
                                    .child("Default-Task-Initialized")
                                    .setValue(new TaskModel("Default","Default Task"))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                            }else{
                                                Toast.makeText(LoginActivity.this,
                                                        "Fallo de registro en la BD de tasks",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            refDB.child("Results").child(currentUser.getUid())
                                    .child("Quiz")
                                    .setValue("Default Quiz")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                            }else{
                                                Toast.makeText(LoginActivity.this,
                                                        "Fallo de registro en la BD de tasks",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            refDB.child("Results").child(currentUser.getUid())
                                    .child("Objects")
                                    .setValue("Default Objects")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(LoginActivity.this,
                                                        "Usuario registrado con éxito",
                                                        Toast.LENGTH_SHORT).show();
                                                rellayRegister.setVisibility(View.GONE);
                                                rellayLogin.setVisibility(View.VISIBLE);
                                                inEmail.setText(inEmailReg.getText().toString());
                                                inPassword.setText(inPasswordReg.getText().toString());
                                                clearForms("registro");
                                            }else{
                                                Toast.makeText(LoginActivity.this,
                                                        "Fallo de registro en la BD de tasks",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,
                                    "Fallo de registro de autorización",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    //Inicialización de instancia de la BD en FireBase
    private void fireBaseInitializer(){
        FirebaseApp.initializeApp(this);
        databaseFB = FirebaseDatabase.getInstance();
        refDB = databaseFB.getReference();
    }

    private void initializeElements(String vista){

        switch (vista){
            case "login":

                //Elementos de UI de Login
                rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
                rellay2 = (RelativeLayout) findViewById(R.id.rellay2);
                rellayLogin = (RelativeLayout) findViewById(R.id.rellayLogin);
                rellayRegister = (RelativeLayout) findViewById(R.id.rellayRegister);
                inEmail = (EditText) findViewById(R.id.inEmailLog);
                inPassword = (EditText) findViewById(R.id.inPasswordLog);
                btnLogin = (Button) findViewById(R.id.btnLogIn);
                btnRegister = (Button) findViewById(R.id.btnRegister);
                btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
                handler = new Handler();
                regInitialized = false;

                //Elementos de UI de registro
                btnSignUp = (Button) findViewById(R.id.btnSignUp);
                imgbtnReturn = (ImageButton) findViewById(R.id.imgbtn_ReturnReg);

                //Inicializamos instancia de Auth
                mAuth = FirebaseAuth.getInstance();
                break;
            case "registro":

                //Elementos de UI de registro
                inEmailReg = (EditText) findViewById(R.id.inEmailReg);
                inPasswordReg = (EditText) findViewById(R.id.inPasswordReg);
                inConfPasswordReg = (EditText) findViewById(R.id.inConfPasswordReg);
                inUserNameReg = (EditText) findViewById(R.id.inUserNameReg);
                inSurnameReg = (EditText) findViewById(R.id.inSurnameReg);
                inPhoneUserReg = (EditText) findViewById(R.id.inPhoneNumberReg);
                swTypeUser = (Switch) findViewById(R.id.sw_typeUserReg);
                regInitialized = true;
                break;
        }
    }

    Runnable splashScreen = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    //Función para limpiar formulario
    private void clearForms(String form){
        switch (form){
            case "login":
                inEmail.setText("");
                inPassword.setText("");
                break;
            case "registro":
                inUserNameReg.setText("");
                inSurnameReg.setText("");
                inEmailReg.setText("");
                inPasswordReg.setText("");
                inConfPasswordReg.setText("");
                break;
        }
    }

    private boolean cheackLog(){

        Boolean required = true;
        if(inEmail.getText().toString().equals("")){
            inEmail.setError("Campo Requerido");
            required = false;
        }
        if(inPassword.getText().toString().equals("")){
            inPassword.setError("Campo Requerido");
            required = false;
        }
        return required;
    }

    //Validación de registro
    private boolean validation(){

        Boolean validate = true;
        Boolean required = true;
        if(inUserNameReg.getText().toString().equals("")){
            inUserNameReg.setError("Campo Requerido");
            required = false;
        }
        if(inEmailReg.getText().toString().equals("")){
            inEmailReg.setError("Campo Requerido");
            required = false;
        }
        if(inPasswordReg.getText().toString().equals("")){
            inPasswordReg.setError("Campo Requerido");
            required = false;
        }
        if(inConfPasswordReg.getText().toString().equals("")){
            inConfPasswordReg.setError("Campo Requerido");
            required = false;
        }


        //Si todos los campos estaán completos, validamos el formato y existencia en BD
        if(required){
            if(!inPasswordReg.getText().toString().equals(inConfPasswordReg.getText().toString())){
                inConfPasswordReg.setError("Las contraseñas no coinciden");
                validate = false;
            }
            if(!inPhoneUserReg.getText().toString().equals("") && inPhoneUserReg.getText().toString().length() != NUMERLENGTH){
                inPhoneUserReg.setError("Introduce un número de teléfono válido");
                validate = false;
            }
        }else{
            validate = false;
        }

        return validate;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

}


