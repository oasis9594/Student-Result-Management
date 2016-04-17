package com.example.dell.studentresultmanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    MyDBHandler dbHandler;
    String mUser, mName;
    Toolbar toolbar;
    TextView resultStatus;
    String [] subjects;
    TextView[] sub;
    TextView[] marks;
    TextView subTotal, subPercent;
    TextView marksTotal, marksPercent;
    ArrayList<Double> mlist;
    SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        dbHandler=MyDBHandler.getInstance(this);
        Bundle bundle=getIntent().getExtras();
        shared=getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        if(bundle!=null)
        {
            mUser=bundle.getString(myUtils.USER_KEY);
            if(mUser==null)
                mUser=shared.getString(myUtils.USER_KEY, myUtils.NotExists);
            Log.w(myUtils.TAG, "Username: "+mUser);
        }
        else
            Log.w(myUtils.TAG, "Bundle Null!!!!!!!!");
        mName=dbHandler.getName(mUser);
        if(mName==null) {
            SharedPreferences.Editor editor=shared.edit();
            editor.clear();
            editor.apply();
            Intent intent=new Intent(this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        //Set Toolbar
        toolbar=(Toolbar)findViewById(R.id.userToolbar);
        toolbar.setTitle(mName);
        Log.w(myUtils.TAG, mName);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        resultStatus=(TextView)findViewById(R.id.resultStatus);
        subjects=getResources().getStringArray(R.array.subjects);
        sub=new TextView[6];
        sub[0]=(TextView)findViewById(R.id.sub1);
        sub[1]=(TextView)findViewById(R.id.sub2);
        sub[2]=(TextView)findViewById(R.id.sub3);
        sub[3]=(TextView)findViewById(R.id.sub4);
        sub[4]=(TextView)findViewById(R.id.sub5);
        sub[5]=(TextView)findViewById(R.id.sub6);
        subTotal=(TextView)findViewById(R.id.subTotal);
        subPercent=(TextView)findViewById(R.id.subPercent);
        marks=new TextView[6];
        marks[0]=(TextView)findViewById(R.id.marks1);
        marks[1]=(TextView)findViewById(R.id.marks2);
        marks[2]=(TextView)findViewById(R.id.marks3);
        marks[3]=(TextView)findViewById(R.id.marks4);
        marks[4]=(TextView)findViewById(R.id.marks5);
        marks[5]=(TextView)findViewById(R.id.marks6);
        marksTotal=(TextView)findViewById(R.id.marksTotal);
        marksPercent=(TextView)findViewById(R.id.marksPercent);
        //Get List Of Marks
        mlist=dbHandler.getMarks(mUser);
        if(mlist.get(0)==-1.0)//Admin has not declared the result yet
            resultStatus.setText(R.string.RESULT_NOT_DECLARED);
        else
        {
            resultStatus.setText(R.string.RESULT_DECLARED);
            showResult();
        }
    }
    public void showResult()
    {
        int i;
        Double tot_marks=0.0;
        for(i=0;i<6;i++) {
            sub[i].setText(subjects[i]);
            marks[i].setText(String.valueOf(mlist.get(i)));
            tot_marks+=mlist.get(i);
        }
        subTotal.setText(R.string.tot_marks);
        subPercent.setText(R.string.tot_percent);
        marksTotal.setText(String.valueOf(tot_marks+"/"+dbHandler.getMaxMarks(mUser)));
        marksPercent.setText(String.valueOf((tot_marks/dbHandler.getMaxMarks(mUser)*100)+"%"));
    }

    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit!!!")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_logOut)
        {
            SharedPreferences.Editor editor=shared.edit();
            editor.clear();
            editor.apply();
            Intent intent=new Intent(this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}