package e.oscarjimfer.pruebatfg;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.request.RequestHeaders;

import org.jetbrains.annotations.NotNull;

import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private Button btn_signUp;
    private EditText in_userName;
    private EditText in_password;
    private static String TOKEN_AUTH;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeElements();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logUser();
            }
        });
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpActivity = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(signUpActivity);
                finish();
            }
        });
    }

    private void logUser() {
        if(cheackLogFields()){
            MyApolloClient.getMyApolloClient().mutate(LoginMutation.builder()
                    .username(in_userName.getText().toString())
                    .password(in_password.getText().toString()).build())
                    .enqueue(new ApolloCall.Callback<LoginMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<LoginMutation.Data> response) {
                            if(response.data().login.success){
                                TOKEN_AUTH = response.data().login.token;
                                String user_id = response.data().login.userResponse._id;
                                findUserAndStartActivity(user_id);
                            }else{
                                if(!response.data().login.errors.isEmpty()){
                                    final String msgRE = response.data().login.errors.get(0).message;
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, msgRE,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            final String msgFE = e.getMessage();
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, msgFE,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }
            });

        }
    }

    public void findUserAndStartActivity(final String  idUser) {
       MyApolloClient.getMyApolloClient(TOKEN_AUTH).query(
                ProducersQuery.builder().user_id(idUser).build())
                .enqueue(new ApolloCall.Callback<ProducersQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ProducersQuery.Data> response) {
                        //Por defecto suponemos que se va a logear un usuario consumidor
                        Intent mainActivity = new Intent(LoginActivity.this, MainUserActivity.class);
                        //Si la respuesta a la consulta de si el usuario es productor es acertada,
                        //se lanzará el activity del usuario productor
                        if(response.data().producer != null) {
                            mainActivity = new Intent(LoginActivity.this, MainProducerActivity.class);
                            mainActivity.putExtra(getString(R.string.ID_PRODUCER), response.data().producer._id);
                        }

                        mainActivity.putExtra(getString(R.string.TOKEN_USER), TOKEN_AUTH);
                        mainActivity.putExtra(getString(R.string.ID_USER), idUser);
                        startActivity(mainActivity);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean cheackLogFields(){

        Boolean required = true;
        if(in_userName.getText().toString().equals("")){
            in_userName.setError(getString(R.string.required_field));
            required = false;
        }
        if(in_password.getText().toString().equals("")){
            in_password.setError(getString(R.string.required_field));
            required = false;
        }
        return required;
    }

    private void initializeElements(){
        btn_login = (Button) findViewById(R.id.btnSignIn);
        btn_signUp = (Button) findViewById(R.id.btnSignUp);
        in_userName = (EditText) findViewById(R.id.inUserNameLog);
        in_password = (EditText) findViewById(R.id.inPasswordLog);

    }
}
