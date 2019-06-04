package e.oscarjimfer.pruebatfg;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private RelativeLayout rellay_selectorType;
    private RelativeLayout rellay_typeConsumer;
    private Button btn_signUp;
    private Button btn_signIn;
    private Button btn_setRegConsumerView;
    private Button btn_setRegProducerView;
    private Button btn_changeType;
    private TextView in_birthday;
    private EditText in_email;
    private EditText in_userName;
    private EditText in_password;
    private EditText in_telephone;
    private EditText in_NIF;
    private int age;
    private static String typeAccount;
    private static final String TYPE_USER = "USER";
    private static final String TYPE_BUSINESS = "BUSINESS";


    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeElements();
        in_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar birthday = Calendar.getInstance();
                int year = birthday.get(Calendar.YEAR);
                int month = birthday.get(Calendar.MONTH);
                int day = birthday.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialogBirthday = new DatePickerDialog(
                        SignUpActivity.this,
                        android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                        mDateSetListener,
                        year, month, day);
                dialogBirthday.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar today = Calendar.getInstance();
                age = today.get(Calendar.YEAR) - year;
                String birthday = dayOfMonth + "/" + month + "/" + year;
                in_birthday.setText(birthday);
            }
        };

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInActivity = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(signInActivity);
                finish();
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btn_setRegConsumerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellay_selectorType.setVisibility(View.GONE);
                rellay_typeConsumer.setVisibility(View.VISIBLE);
                typeAccount = TYPE_USER;
            }
        });

        btn_setRegProducerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellay_selectorType.setVisibility(View.GONE);
                rellay_typeConsumer.setVisibility(View.VISIBLE);
                in_NIF.setHint(getResources().getString(R.string.Input_CIF));
                in_birthday.setVisibility(View.INVISIBLE);
                typeAccount = TYPE_BUSINESS;
            }
        });

        btn_changeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rellay_typeConsumer.setVisibility(View.GONE);
                rellay_selectorType.setVisibility(View.VISIBLE);
            }
        });


    }

    private void initializeElements(){
        btn_signIn = (Button) findViewById(R.id.btnSignInReg);
        btn_signUp = (Button) findViewById(R.id.btnSignUpReg);
        btn_setRegConsumerView = (Button) findViewById(R.id.btnRegConsumer);
        btn_setRegProducerView = (Button) findViewById(R.id.btnRegProducer);
        btn_changeType = (Button) findViewById(R.id.btnChangeType);
        in_birthday = (TextView) findViewById(R.id.inBirthdayReg);
        in_email = (EditText) findViewById(R.id.inEmailReg);
        in_userName = (EditText) findViewById(R.id.inUserNameRegReg);
        in_password = (EditText) findViewById(R.id.inPasswordReg);
        in_telephone = (EditText) findViewById(R.id.inTelephoneReg);
        in_NIF = (EditText) findViewById(R.id.inNIFReg);
        rellay_selectorType = (RelativeLayout) findViewById(R.id.rellay_selectorType);
        rellay_typeConsumer = (RelativeLayout) findViewById(R.id.rellay_signUpConsumer);


    }

    private boolean cheackRegisterFields(){

        Boolean required = true;
        if(in_email.getText().toString().equals("")){
            in_email.setError(getString(R.string.required_field));
            required = false;
        }
        if(in_userName.getText().toString().equals("")){
            in_userName.setError(getString(R.string.required_field));
            required = false;
        }
        if(in_password.getText().toString().equals("")){
            in_password.setError(getString(R.string.required_field));
            required = false;
        }
        if(in_telephone.getText().toString().equals("")){
            in_telephone.setError(getString(R.string.required_field));
            required = false;
        }
        return required;
    }

    private void registerUser() {
        if(cheackRegisterFields()){
            MyApolloClient.getMyApolloClient().mutate(RegisterUserMutation.builder()
                    .username(in_userName.getText().toString())
                    .password(in_password.getText().toString())
                    .email(in_email.getText().toString())
                    .build())
                    .enqueue(new ApolloCall.Callback<RegisterUserMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<RegisterUserMutation.Data> response) {
                            if(response.data().registerUser.success){
                                if(typeAccount.equals(TYPE_USER)) {
                                    registerConsumer(response.data().registerUser.userResponse._id);
                                }else{
                                    if(typeAccount.equals(TYPE_BUSINESS)){
                                        registerProducer(response.data().registerUser.userResponse._id);
                                    }
                                }
                            }else{
                                if(!response.data().registerUser.errors.isEmpty()){
                                    final String msgRE = response.data().registerUser.errors.get(0).message;
                                    SignUpActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SignUpActivity.this, msgRE,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            final String msgFE = e.getMessage();
                            SignUpActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignUpActivity.this, msgFE,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }
                    });


        }
    }

    private void registerConsumer(final String user_id){
        if(!in_NIF.getText().equals("")) {
            MyApolloClient.getMyApolloClient().mutate(RegisterConsumerMutation.builder()
                    .user_id(user_id)
                    .age(age)
                    .nIF(in_NIF.getText().toString())
                    .telephone(in_telephone.getText().toString())
                    .build())
                    .enqueue(new ApolloCall.Callback<RegisterConsumerMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<RegisterConsumerMutation.Data> response) {
                            if(response.data().registerConsumer.success) {
                                SignUpActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignUpActivity.this, R.string.Msg_userCreated,
                                                Toast.LENGTH_LONG).show();
                                        btn_signIn.callOnClick();
                                    }
                                });
                            }else{
                                if(!response.data().registerConsumer.errors.isEmpty()){
                                    final String msgRE = response.data().registerConsumer.errors.get(0).message;
                                    SignUpActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SignUpActivity.this, msgRE,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            final String msgFE = e.getMessage();
                            SignUpActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignUpActivity.this, msgFE,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void registerProducer(final String user_id){
        if(!in_NIF.getText().equals("")) {
            MyApolloClient.getMyApolloClient().mutate(RegisterProducerMutation.builder()
                    .user_id(user_id)
                    .cIF(in_NIF.getText().toString())
                    .telephone(in_telephone.getText().toString())
                    .build())
                    .enqueue(new ApolloCall.Callback<RegisterProducerMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<RegisterProducerMutation.Data> response) {
                            if(response.data().registerProducer.success) {
                                SignUpActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignUpActivity.this, R.string.Msg_userCreated,
                                                Toast.LENGTH_LONG).show();
                                        btn_signIn.callOnClick();
                                    }
                                });
                            }else{
                                if(!response.data().registerProducer.errors.isEmpty()){
                                    final String msgRE = response.data().registerProducer.errors.get(0).message;
                                    SignUpActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SignUpActivity.this, msgRE,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            final String msgFE = e.getMessage();
                            SignUpActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignUpActivity.this, msgFE,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                            e.printStackTrace();
                        }
                    });
        }
    }
}
