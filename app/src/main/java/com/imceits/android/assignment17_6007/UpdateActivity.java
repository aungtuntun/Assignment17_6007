package com.imceits.android.assignment17_6007;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateActivity extends AppCompatActivity {
    EditText txtName;
    EditText txtEmpID;
    EditText txtNRC;
    EditText txtEmail;
    EditText txtPhone;
    RadioGroup radGroup;
    RadioButton radMale;
    RadioButton radFemale;
    Button btnDOB;
    Button btnSave;
    ImageView imgView;
    boolean isValue = false;

    Calendar myCalendar = Calendar.getInstance();
    Intent myIntentUpdate;
    Bundle myBundle;
    EmployeeData empData;

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_update);

        txtEmpID = (EditText) findViewById(R.id.txtEmpIDUp);
        txtName = (EditText) findViewById(R.id.txtNameUp);
        txtNRC = (EditText) findViewById(R.id.txtNRCNoUp);
        txtEmail = (EditText) findViewById(R.id.txtEmailUp);
        txtPhone = (EditText) findViewById(R.id.txtPhoneUp);
        radGroup = (RadioGroup) findViewById(R.id.radGroupUp);
        radMale = (RadioButton) findViewById(R.id.radMaleUp);
        radFemale = (RadioButton) findViewById(R.id.radFemaleUp);
        btnDOB = (Button)findViewById(R.id.btnDOBUp);
        btnSave = (Button)findViewById(R.id.btnSaveUp);
        imgView = (ImageView)findViewById(R.id.imgViewUp);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    update();
                   // Toast.makeText(getApplicationContext(), "updated Successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dateDialog;
                dateDialog = new DatePickerDialog(UpdateActivity.this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dateDialog.show();
            }
        });

        myIntentUpdate = getIntent();
        myBundle = myIntentUpdate.getExtras();
        empData = (EmployeeData) myBundle.getSerializable("empInfo");
        updateScreen(empData);
        myIntentUpdate.putExtras(myBundle);
        setResult(Activity.RESULT_OK, myIntentUpdate);
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int daysOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, daysOfMonth);
            Date date = myCalendar.getTime();
            String srtDate = DateFormat.getDateInstance().format(date);
            btnDOB.setText(srtDate);
        }
    };

    private boolean isValid(){
        String name = txtName.getText().toString().trim();
        String id = txtEmpID.getText().toString().trim();
        String nrc = txtNRC.getText().toString().trim();
        String ph = txtPhone.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String dob = btnDOB.getText().toString();
        if(id.equals("")){
            Toast.makeText(this, "ID is required!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(name.equals("")){
            Toast.makeText(this, "Name is required!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(nrc.equals("")){
            Toast.makeText(this, "NRC is required!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(ph.equals("")){
            Toast.makeText(this, "Phone is required!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(email.equals("")){
            Toast.makeText(this, "Email is required!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(dob.equals("")){
            Toast.makeText(this, "Date of birth is required!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void updateScreen(EmployeeData data){
        txtEmpID.setText(data.getEmpID());
        txtName.setText(data.getEmpName());
        txtNRC.setText(data.getNrc());
        txtEmail.setText(data.getEmail());

        if(data.getGender() == 1)
            radMale.setChecked(true);
        else if(data.getGender() == 2)
            radFemale.setChecked(true);
        txtPhone.setText(data.getPhone());
        btnDOB.setText(data.getDob());

    }

    private void setObject(EmployeeData data){
        String name = txtName.getText().toString().trim();
        String id = txtEmpID.getText().toString().trim();
        String nrc = txtNRC.getText().toString().trim();
        String ph = txtPhone.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String dob = btnDOB.getText().toString();
        int gender = 0;
        if(radGroup.getCheckedRadioButtonId() == radMale.getId()){
            gender = 1;
        }
        else if(radGroup.getCheckedRadioButtonId() == radFemale.getId()){
            gender = 2;
        }
        data.setEmpID(id);
        data.setEmpName(name);
        data.setNrc(nrc);
        data.setEmail(email);
        data.setPhone(ph);
        data.setDob(dob);
        data.setGender(gender);

    }

    private void update(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL("http://192.168.56.103:9090/Assignment17/EmployeeServlet");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("POST");
                    DataOutputStream writer = new DataOutputStream(httpURLConnection.getOutputStream());
                    EmployeeData data = new EmployeeData();
                    setObject(data);
                    data.setRecordStatus(empData.getRecordStatus());
                    data.setSerialNo(empData.getSerialNo());
                    data.setStatus(2);
                    writer.writeBytes(new Gson().toJson(data));
                    writer.flush();
                    writer.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String readLine = "";
                    isValue = false;
                    while((readLine = reader.readLine()) != null){
                        isValue = Boolean.valueOf(readLine);
                    }
                    reader.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isValue){
                                Toast.makeText(getApplicationContext(), "saved Successful!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "update failed!", Toast.LENGTH_SHORT).show();
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
