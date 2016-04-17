package com.example.dell.studentresultmanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogIn extends AppCompatActivity {

    Toolbar toolbar;
    EditText username;
    EditText password;
    Button logIn;
    Button signUp;
    private String mUser;
    private String mPass;
    MyDBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        toolbar=(Toolbar)findViewById(R.id.logInToolbar);
        assert toolbar != null;
        toolbar.setTitle("Log In");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        logIn=(Button)findViewById(R.id.logIn);
        signUp=(Button)findViewById(R.id.signUp);

        //Check if user was logged in
        //If user is already logged in go to user activity or admin activity
        SharedPreferences shared=getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        String first;
        Boolean third;
        first=shared.getString(myUtils.USER_KEY, myUtils.NotExists);
        third=shared.getBoolean(myUtils.ADMIN_KEY, false);
        if(!(first.equals(myUtils.NotExists)))
        {
            Intent i;
            if(third)
                i=new Intent(this, AdminActivity.class);
            else
                i= new Intent(this, UserActivity.class);
            i.putExtra("user", first);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        //Update values from bundle
        updateFromBundle(savedInstanceState);
        //Instantiate an object of MyDBHandler to handle database usages
        dbHandler=MyDBHandler.getInstance(this);

    }
    public void updateFromBundle(Bundle savedInstanceState)
    {
        if(savedInstanceState!=null)//Activity was recreated(may be due to rotation of screen)
        {
            if(savedInstanceState.getString(myUtils.USER_KEY)!=null) {
                mUser = savedInstanceState.getString(myUtils.USER_KEY);
                username.setText(mUser);
            }
            if(savedInstanceState.getString(myUtils.PASS_KEY)!=null) {
                mPass=savedInstanceState.getString(myUtils.PASS_KEY);
                password.setText(mPass);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(myUtils.USER_KEY, mUser);
        outState.putString(myUtils.PASS_KEY, mPass);
        super.onSaveInstanceState(outState);
    }

    //Handle button clicks on LogIn button
    public void onLogIn(View view) {
        mUser=username.getText().toString();
        mPass=password.getText().toString();
        Log.w(myUtils.TAG, mUser+" "+mPass);
        TextView errorText=(TextView)findViewById(R.id.errorText);
        assert errorText != null;
        //Check if username and password is valid
        boolean b=dbHandler.validate(mUser, mPass);//true if valid

        if(b)
        {
            //Check if user can access its details i.e request is granted
            switch (dbHandler.requestType(mUser))
            {
                case myUtils.USER_ACCEPTED:
                    boolean isAdmin;
                    isAdmin=dbHandler.isAdmin(mUser);
                    SharedPreferences shared=getSharedPreferences("userDetails", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=shared.edit();
                    editor.putString(myUtils.USER_KEY, mUser);
                    editor.putBoolean(myUtils.ADMIN_KEY, isAdmin);
                    editor.apply();
                    Intent i;
                    if(isAdmin)
                        i= new Intent(this, AdminActivity.class);
                    else
                        i= new Intent(this, UserActivity.class);
                    i.putExtra(myUtils.USER_KEY, mUser);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    break;
                case myUtils.USER_WAITING:
                    errorText.setText(R.string.ERROR_WAITING);
                    break;
                case myUtils.USER_REJECTED:
                    errorText.setText(R.string.ERROR_REJECTED);
            }
        }
        else {
            errorText.setText(R.string.invalidCredentials);
        }
    }
    //Handle button clicks on register button
    public void onSignUp(View view) {
        password.setText("");
        Intent i=new Intent(this, SignUp.class);
        startActivity(i);
    }
}

