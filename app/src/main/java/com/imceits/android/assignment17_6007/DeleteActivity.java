package com.imceits.android.assignment17_6007;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DeleteActivity extends AppCompatActivity {
    EditText txtID;
    EditText txtName;
    EditText txtNRC;
    EditText txtEmail;
    EditText txtGender;
    EditText txtPhone;
    EditText txtDOB;
    Button btnDelete;

    EmployeeData employeeData;
    Intent myIntentDelete;
    Bundle myBundle;
    boolean isSuccess = false;

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_delete);

        txtID = (EditText) findViewById(R.id.txtID1);
        txtName = (EditText) findViewById(R.id.txtName1);
        txtNRC = (EditText) findViewById(R.id.txtNRC1);
        txtEmail = (EditText) findViewById(R.id.txtEmail1);
        txtGender = (EditText) findViewById(R.id.txtGender1);
        txtPhone = (EditText) findViewById(R.id.txtPhone1);
        txtDOB = (EditText) findViewById(R.id.txtDOB1);
        btnDelete = (Button) findViewById(R.id.btnDeleteEmp);

        myIntentDelete = getIntent();
        myBundle = myIntentDelete.getExtras();
        employeeData = (EmployeeData) myBundle.getSerializable("empInfo");
        updateObject(employeeData);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = makeAndShowDialogBox();
                dialog.show();
            }
        });
        myIntentDelete.putExtras(myBundle);
        setResult(Activity.RESULT_OK, myIntentDelete);
    }

    private AlertDialog makeAndShowDialogBox(){
        AlertDialog myDeleteDialog = new AlertDialog.Builder(this).setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete?")
                .setIcon(R.drawable.stat_notify_chat)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEmployee();
                        //Toast.makeText(getApplicationContext(), "deleted Successful!", Toast.LENGTH_SHORT).show();
                       // finish();
                    }
                })  // end of positive button
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();

        return myDeleteDialog;
    }

    private void updateObject(EmployeeData data){
        txtID.setText(data.getEmpID());
        txtName.setText(data.getEmpName());
        txtNRC.setText(data.getNrc());
        txtEmail.setText(data.getEmail());
        if(data.getGender() == 1)
            txtGender.setText("Male");
        else if(data.getGender() == 2)
            txtGender.setText("Female");
        txtPhone.setText(data.getPhone());
        txtDOB.setText(data.getDob());

    }

    private void deleteEmployee(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL("http://192.168.56.103:9090/Assignment17/EmployeeServlet");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
                    EmployeeData data = employeeData;
                    data.setStatus(4); // for delete method in servlet
                    out.writeBytes(new Gson().toJson(data)); // to json format
                    out.flush();
                    out.close();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String returnVal = "";
                    isSuccess = false;
                    while((returnVal = reader.readLine()) != null){
                        isSuccess = Boolean.valueOf(returnVal);
                    }
                    reader.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isSuccess){
                                Toast.makeText(getApplicationContext(), "deleted Successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "deleted failed!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
