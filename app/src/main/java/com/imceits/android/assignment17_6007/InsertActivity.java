package com.imceits.android.assignment17_6007;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class InsertActivity extends AppCompatActivity  {

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
    Button btnNew;
    Button btnClose;
    ImageView imgView;
    boolean value = false;

    Calendar myCalendar = Calendar.getInstance();

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_insert);

        txtEmpID = (EditText) findViewById(R.id.txtEmpID);
        txtName = (EditText) findViewById(R.id.txtName);
        txtNRC = (EditText) findViewById(R.id.txtNRCNo);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPhone = (EditText) findViewById(R.id.txtPhone);


        radGroup = (RadioGroup) findViewById(R.id.radGroup);
        radMale = (RadioButton) findViewById(R.id.radMale);
        radFemale = (RadioButton) findViewById(R.id.radFemale);

        btnDOB = (Button)findViewById(R.id.btnDOB);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnNew = (Button)findViewById(R.id.btnNew);
        btnClose = (Button)findViewById(R.id.btnClose);
        imgView = (ImageView)findViewById(R.id.imgView);
        imgView.setImageResource(R.drawable.ic_android);

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewData();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()) {
                  insert();
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dateDialog;
                dateDialog = new DatePickerDialog(InsertActivity.this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dateDialog.show();
            }
        });

        Intent myIntentInsert = getIntent();
        Bundle myBundle = myIntentInsert.getExtras();

        myIntentInsert.putExtras(myBundle);
        setResult(Activity.RESULT_OK, myIntentInsert);
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

    private void setNewData(){
        txtEmpID.setText("");
        txtEmpID.requestFocus();
        txtName.setText("");
        txtNRC.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        radMale.setChecked(false);
        radFemale.setChecked(false);
        btnDOB.setText("");
    }

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

    private void setObject( EmployeeData data){
        String id = txtEmpID.getText().toString().trim();
        String name = txtName.getText().toString().trim();
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
        data.setSerialNo(0);
        data.setEmpID(id);
        data.setEmpName(name);
        data.setEmail(email);
        data.setNrc(nrc);
        data.setPhone(ph);
        data.setGender(gender);
        data.setDob(dob);
        data.setStatus(1);
        data.setRecordStatus(1);
    }

    private void insert(){
       value = false;
       new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL("http://192.168.56.103:9090/Assignment17/EmployeeServlet");
                    httpURLConnection = (HttpURLConnection)  url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream (httpURLConnection.getOutputStream());
                    EmployeeData postData = new EmployeeData();
                    setObject(postData);
                    out.writeBytes(new Gson().toJson(postData));
                    out.flush();
                    out.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String returnVal = "";

                    while((returnVal = reader.readLine()) != null){
                        value = Boolean.valueOf(returnVal);
                    }
                    reader.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(value){
                                Toast.makeText(getApplicationContext(), "saved Successful!", Toast.LENGTH_SHORT).show();
                                setNewData();
                                txtEmpID.requestFocus();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "saved failed!", Toast.LENGTH_SHORT).show();
                                //finish();
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
