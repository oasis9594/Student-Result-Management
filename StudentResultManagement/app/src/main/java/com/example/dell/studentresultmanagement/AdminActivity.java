package com.example.dell.studentresultmanagement;

import android.app.Activity;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    Activity context;
    ArrayList<String> mList;
    ListView listView;
    ArrayAdapter<String> adapter;
    MyDBHandler dbHandler;
    SharedPreferences shared;
    String mUser, mName;
    private static final int REQUEST_CODE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar=(Toolbar)findViewById(R.id.adminToolbar);
        toolbar.setTitle("Admin");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        dbHandler=MyDBHandler.getInstance(this);
        Log.w(myUtils.TAG, "AdminActivity");
        mUser=getIntent().getExtras().getString(myUtils.USER_KEY);
        shared=getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        if(mUser==null)
            mUser=shared.getString(myUtils.USER_KEY, myUtils.NotExists);
        mName=dbHandler.getName(mUser);
        if(mName==null) {
            Log.w(myUtils.TAG, "AdminActivity: Name null");
            SharedPreferences.Editor editor=shared.edit();
            editor.clear();
            editor.apply();
            Intent intent=new Intent(this, LogIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        Log.w(myUtils.TAG, "AdminActivity");
        context=this;
        mList=dbHandler.getAllUsers();
        listView=(ListView)findViewById(R.id.myList);
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username=String.valueOf(parent.getItemAtPosition(position));
                Intent intent=new Intent(context, ShowUserDetails.class);
                intent.putExtra(myUtils.USER_KEY, username);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE&&resultCode==Activity.RESULT_OK)
        {
            String username=data.getStringExtra(myUtils.USER_KEY);
            Boolean removeItem=data.getBooleanExtra(myUtils.RMV_KEY, false);
            if(removeItem) {
                mList.remove(username);
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
