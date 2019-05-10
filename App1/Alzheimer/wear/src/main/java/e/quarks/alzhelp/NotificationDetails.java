package e.quarks.alzhelp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.wearable.Wearable;

public class NotificationDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);
        CharSequence charSequence = getMessageText(getIntent());
        Toast.makeText(this,charSequence.toString(),Toast.LENGTH_LONG).show();
    }

    private CharSequence getMessageText(Intent intent){
        Bundle bundle = RemoteInput.getResultsFromIntent(intent);
        if(bundle != null){
            return bundle.getCharSequence(NotificationUtils.EXTRA_VOICE_REPLY);
        }
        return null;
    }
}
