package e.quarks.alzhelp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationDetails extends Activity {

    private FirebaseDatabase databaseFB;    //Base de datos de FireBase
    private DatabaseReference refDB;        //Referencia a BD
    private FirebaseUser currentUser;       //Usuario actual
    private String alertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);
        fireBaseInitializer();
        CharSequence charSequence = getMessageText(getIntent());
        String result = charSequence.toString().toLowerCase();
        String alertGeo = getIntent().getStringExtra("GEO");
        alertId = getIntent().getStringExtra("IDALERT");
        switch (result){
            case "localizar y eliminar aviso":
                Intent mapIntent2 = new Intent(Intent.ACTION_VIEW);
                if (alertGeo!=null){
                    mapIntent2.setData(Uri.parse(alertGeo));
                    startActivity(mapIntent2);
                    deleteAlert();
                }else{
                    Toast.makeText(this,"La localización del usuario" +
                            " no se encuentra disponible.",Toast.LENGTH_SHORT).show();
                }
                break;

            case "localizar":
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                if (alertGeo!=null){
                    mapIntent.setData(Uri.parse(alertGeo));
                    startActivity(mapIntent);
                    onBackPressed();
                }else{
                    Toast.makeText(this,"La localización del usuario" +
                            " no se encuentra disponible.",Toast.LENGTH_SHORT).show();
                }
                break;
            case "eliminar aviso":
                deleteAlert();

                break;
            case "llamar":
                Toast.makeText(this,
                        "llamar: función no implementada " +
                                "para evitar llamadas a desconocidos" +
                                "por seguridad",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void deleteAlert() {
        refDB.child("Help").child(currentUser.getUid()).child(alertId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    onBackPressed();
                } else {

                }
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

    private CharSequence getMessageText(Intent intent){
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        if(bundle != null){
            return bundle.getCharSequence(NotificationUtils.EXTRA_VOICE_REPLY);
        }
        return null;
    }
}
