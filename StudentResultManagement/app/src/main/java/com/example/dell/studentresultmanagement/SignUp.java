package com.example.dell.studentresultmanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {

    CheckBox showPassword;
    EditText myName;
    EditText myUser;
    EditText myPassword;
    Button register;
    TextView myError;
    MyDBHandler dbHandler;
    Toolbar toolbar;

    String mUser, mName, mPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Set Toolbar as action bar
        toolbar=(Toolbar)findViewById(R.id.SignUpToolbar);
        toolbar.setTitle("Sign Up!");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        //Reference ids
        myName=(EditText)findViewById(R.id.myName);
        myUser=(EditText)findViewById(R.id.myUser);
        myPassword=(EditText)findViewById(R.id.myPassword);
        myError=(TextView)findViewById(R.id.myError);
        register=(Button)findViewById(R.id.register);
        showPassword=(CheckBox)findViewById(R.id.checkBox);
        //Get an instance of MyDBHandler
        dbHandler=MyDBHandler.getInstance(this);
        //Check for screen rotations or other possible reasons for recreation of activity
        updateFromBundle(savedInstanceState);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.w(myUtils.TAG, "onCheckedChanged: "+isChecked);
                if (!isChecked)
                    myPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                else
                    myPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

    }
    public void updateFromBundle(Bundle savedInstanceState)
    {
        if(savedInstanceState!=null)//Activity was recreated(may be due to rotation of screen)
        {
            if(savedInstanceState.getString(myUtils.USER_KEY)!=null) {
                mUser = savedInstanceState.getString(myUtils.USER_KEY);
                myUser.setText(mUser);
            }
            if(savedInstanceState.getString(myUtils.PASS_KEY)!=null) {
                mPass=savedInstanceState.getString(myUtils.PASS_KEY);
                myPassword.setText(mPass);
            }
            if(savedInstanceState.getString(myUtils.NAME_KEY)!=null) {
                mName=savedInstanceState.getString(myUtils.NAME_KEY);
                myName.setText(mName);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(myUtils.USER_KEY, mUser);
        outState.putString(myUtils.NAME_KEY, mName);
        outState.putString(myUtils.PASS_KEY, mPass);
        super.onSaveInstanceState(outState);
    }

    //Handle Register Button Clicks
    public void onRegister(View view) {
        mUser=myUser.getText().toString();
        mName=myName.getText().toString();
        mPass=myPassword.getText().toString();
        boolean b=dbHandler.userExists(mUser);//true if database does contains that username
        Log.w(myUtils.TAG, "onRegister");
        if(b)
        {
            Log.w(myUtils.TAG, "onRegister: user exists");
            myError.setText(R.string.ERROR_USERNAME_EXISTS);
        }
        else {
            try {
                SharedPreferences shared=getSharedPreferences("userDetails", Context.MODE_PRIVATE);
                int cntr=shared.getInt(myUtils.CNTR_KEY, 0);
                if(dbHandler.containsAnyData()) {
                    dbHandler.addUser(new UserDetails(mUser, mName, mPass, myUtils.USER_WAITING));
                    cntr++;
                    SharedPreferences.Editor editor=shared.edit();
                    editor.putInt(myUtils.CNTR_KEY, cntr);
                    editor.apply();
                }
                else
                {
                    Log.w(myUtils.TAG, "onRegister: No user in database");
                    UserDetails user=new UserDetails(mUser, mName, mPass, myUtils.USER_ACCEPTED);
                    user.setIsAdmin(true);
                    dbHandler.addUser(user);
                }
                Log.w(myUtils.TAG, "onRegister");
                finish();
            }catch (Exception e)
            {
                Log.w(myUtils.TAG, e.getMessage());
            }

        }
    }
}
