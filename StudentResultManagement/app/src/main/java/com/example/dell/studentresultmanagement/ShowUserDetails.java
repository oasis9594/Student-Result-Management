package com.example.dell.studentresultmanagement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class ShowUserDetails extends AppCompatActivity {

    MyDBHandler dbHandler;
    String mUser, mName;
    Toolbar toolbar;
    TextView resultStatus;
    String [] subjects;
    TextView[] sub;
    Double tot_marks=0.0, max_marks;
    EditText[] marks;
    EditText maxMarks;
    TextView subTotal, subPercent, maxMarksT, errorText;
    TextView marksTotal, marksPercent ;
    Button accept, reject, save;
    ArrayList<Double> mlist;
    TextView myName;
    String requestType;
    SharedPreferences shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_details);
        dbHandler=MyDBHandler.getInstance(this);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
            mUser=bundle.getString(myUtils.USER_KEY);

        mName=dbHandler.getName(mUser);
        //Set Toolbar
        toolbar=(Toolbar)findViewById(R.id.euserToolbar);
        toolbar.setTitle(mName);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        accept=(Button)findViewById(R.id.acceptRequest);
        reject=(Button)findViewById(R.id.rejectRequest);
        save=(Button)findViewById(R.id.eSave);
        resultStatus=(TextView)findViewById(R.id.eresultStatus);
        subjects=getResources().getStringArray(R.array.subjects);
        sub=new TextView[6];
        myName=(TextView)findViewById(R.id.mname);
        myName.setText(mName);
        maxMarksT=(TextView)findViewById(R.id.etotsub);
        maxMarks=(EditText)findViewById(R.id.etotmarks);
        errorText=(TextView)findViewById(R.id.merrorText);
        sub[0]=(TextView)findViewById(R.id.esub1);
        sub[1]=(TextView)findViewById(R.id.esub2);
        sub[2]=(TextView)findViewById(R.id.esub3);
        sub[3]=(TextView)findViewById(R.id.esub4);
        sub[4]=(TextView)findViewById(R.id.esub5);
        sub[5]=(TextView)findViewById(R.id.esub6);
        subTotal=(TextView)findViewById(R.id.esubTotal);
        subPercent=(TextView)findViewById(R.id.esubPercent);
        marks=new EditText[6];
        marks[0]=(EditText)findViewById(R.id.emarks1);
        marks[1]=(EditText)findViewById(R.id.emarks2);
        marks[2]=(EditText)findViewById(R.id.emarks3);
        marks[3]=(EditText)findViewById(R.id.emarks4);
        marks[4]=(EditText)findViewById(R.id.emarks5);
        marks[5]=(EditText)findViewById(R.id.emarks6);
        marksTotal=(TextView)findViewById(R.id.emarksTotal);
        marksPercent=(TextView)findViewById(R.id.emarksPercent);
        //Get List Of Marks
        mlist=dbHandler.getMarks(mUser);
        if(mlist.get(0)==-1.0)//Admin has not declared the result yet
            resultStatus.setText(R.string.RESULT_NOT_DECLARED);
        else
            resultStatus.setText(R.string.RESULT_DECLARED);
        showResult();

        //set action listeners
        for(int i=0;i<6;i++) {
            marks[i].setOnEditorActionListener(editorActionListener);
        }
        maxMarks.setOnEditorActionListener(editorActionListener);

        requestType=dbHandler.requestType(mUser);
        if(requestType.equals(myUtils.USER_WAITING))
        {
            showView(View.GONE, View.VISIBLE);
        }
    }
    public void showResult()
    {
        int i;
        maxMarksT.setText("Enter Maximum Marks");
        for(i=0;i<6;i++) {
            sub[i].setText(subjects[i]);
            if(mlist.get(i)!=-1.0) {
                marks[i].setText(String.valueOf(mlist.get(i)));
                tot_marks+=mlist.get(i);
            }
            else {
                marks[i].setText("0.0");
            }
        }
        max_marks=dbHandler.getMaxMarks(mUser);
        subTotal.setText(R.string.tot_marks);
        subPercent.setText(R.string.tot_percent);
        if(max_marks==-1.0)
            max_marks=600.0;
        maxMarks.setText(String.valueOf(max_marks));
        marksTotal.setText(String.valueOf(tot_marks+"/"+max_marks));
        marksPercent.setText(String.valueOf((tot_marks/max_marks*100)+"%"));
    }
    TextView.OnEditorActionListener editorActionListener=new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId== EditorInfo.IME_ACTION_NONE)
            {
                Log.w(myUtils.TAG, "editorActionListener");
                setMarksTotal();
            }
            if(actionId== EditorInfo.IME_ACTION_DONE)
            {
                Log.w(myUtils.TAG, "editorActionListener: done");
                try{
                    max_marks=Double.parseDouble(maxMarks.getText().toString());
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Enter valid Total Marks" ,Toast.LENGTH_SHORT).show();
                    errorText.setText(R.string.ERROR_MARKS);
                }
                setMarksTotal();
            }
            return false;
        }
    };
    public void setMarksTotal()
    {
        tot_marks=0.0;
        for(int i=0;i<6;i++) {
            try {
                if(!(marks[i].getText().toString().equals("")))
                tot_marks +=Double.parseDouble(marks[i].getText().toString());
            }catch (NumberFormatException e)
            {
                errorText.setText(R.string.ERROR_MARKS);
            }
        }
        if(max_marks==-1.0)
            max_marks=600.0;
        marksTotal.setText(String.valueOf(tot_marks));
        marksPercent.setText(String.valueOf((tot_marks/max_marks*100)+"%"));
    }
    public void acceptRequest(View v) {
        UserDetails user;
        user=dbHandler.getUser(mUser);
        user.setRequest(myUtils.USER_ACCEPTED);
        dbHandler.updateUser(user);
        showView(View.VISIBLE, View.GONE);
    }
    public void rejectRequest(View view) {
        UserDetails user;
        user=dbHandler.getUser(mUser);
        user.setRequest(myUtils.USER_REJECTED);
        dbHandler.updateUser(user);
        accept.setVisibility(View.GONE);
        reject.setVisibility(View.GONE);
        dbHandler.deleteUser(user);
        Intent intent=new Intent();
        //Tell to parent activity that user has been removed from database
        intent.putExtra(myUtils.RMV_KEY, true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void saveUser(View view) {
        UserDetails user;
        user=dbHandler.getUser(mUser);
        user.setMaxMarks(max_marks);
        for(int i=0;i<6;i++) {

            try{
                double d=Double.parseDouble(marks[i].getText().toString());
                mlist.set(i, d);
            }catch (Exception e)
            {
                errorText.setText(R.string.ERROR_MARKS);
                Log.w(myUtils.TAG,"saveUser: "+e.getMessage());
                return;
            }
        }
        user.setResult(mlist);
        dbHandler.updateUser(user);
        Intent intent=new Intent();
        //Tell to parent activity that user has not been removed from database
        intent.putExtra(myUtils.RMV_KEY, false);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void showView(int x, int y)
    {
        for(int i=0;i<6;i++)
        {
            sub[i].setVisibility(x);
            marks[i].setVisibility(x);
        }

        marksTotal.setVisibility(x);
        marksPercent.setVisibility(x);
        maxMarks.setVisibility(x);
        maxMarksT.setVisibility(x);
        save.setVisibility(x);
        marksPercent.setVisibility(x);
        subPercent.setVisibility(x);
        errorText.setVisibility(x);
        subTotal.setVisibility(x);

        accept.setVisibility(y);
        reject.setVisibility(y);
    }
}
