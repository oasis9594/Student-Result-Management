package com.example.dell.studentresultmanagement;

import java.util.ArrayList;

public class UserDetails {

    private String id;
    private String name;


    private String password;

    private double maxMarks;//Maximum marks for each subject
    private String request;//Can be accepted, rejected, or waiting
    private boolean isAdmin;//Is my user administrator
    private ArrayList<Double> result;//marks for each subject

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public ArrayList<Double> getResult() {
        return result;
    }

    public void setResult(ArrayList<Double> result) {
        this.result = result;
    }
    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public UserDetails(){
        int i;
        result=new ArrayList<>(6);
        for(i=0;i<6;i++)
            result.add(-1.0);
        this.isAdmin=false;
        this.maxMarks=-1.0;
    }
    public UserDetails(String _id, String _name, String _password, String request)
    {
        this.id=_id;
        this.name=_name;
        this.password=_password;
        this.request=request;
        this.result=new ArrayList<>(6);
        for(int i=0;i<6;i++)
            result.add(-1.0);
        this.isAdmin=false;
        this.maxMarks=-1.0;
    }
}

