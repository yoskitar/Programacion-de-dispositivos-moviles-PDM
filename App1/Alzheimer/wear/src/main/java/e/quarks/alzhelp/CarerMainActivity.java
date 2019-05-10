package e.quarks.alzhelp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CarerMainActivity extends Activity {

    private final static String CHANNEL_ID = "NOTIFICATION";
    private final static int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_main);
    }

    public void notifyme(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Create an intent for the reply action
        Intent actionIntent = new Intent(this, NotificationDetails.class);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(this, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        String replyLabel = "My reply";
        String[] replyChoices = getResources().getStringArray(R.array.reply_choices);

        android.support.v4.app.RemoteInput remoteInput =
                new android.support.v4.app.RemoteInput.Builder(NotificationUtils.EXTRA_VOICE_REPLY)
                        .setLabel(replyLabel)
                        .setChoices(replyChoices)
                        .build();



        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.common_google_signin_btn_icon_light,
                        replyLabel, actionPendingIntent).addRemoteInput(remoteInput)
                        .build();


        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                        .setContentTitle("Help!")
                        .setContentText("An user need your help!")
                        .setContentIntent(actionPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().addAction(action))
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFICATION_ID,notification);
    }
}
