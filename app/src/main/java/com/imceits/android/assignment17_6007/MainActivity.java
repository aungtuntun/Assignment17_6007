package com.imceits.android.assignment17_6007;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btnInsert;
    Button btnUpdate;
    Button btnDelete;
    Button btnView;
    TextView txtInfo;
    TextView txtList;
    private final int REQUEST_CODE = (int) (1000 * Math.random());

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private int butNumber = 0;
    boolean isCalled = false;
    ArrayList<EmployeeData> empDataList = new ArrayList<EmployeeData>();
    EmployeeData employeeData;
    String textInfo = "Employee Information";
    StringBuilder jsonStr = new StringBuilder() ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtList = (TextView) findViewById(R.id.txtList);
        btnInsert = (Button) findViewById(R.id.btnInsertMain);
        btnUpdate = (Button) findViewById(R.id.btnUpdateMain);
        btnDelete = (Button) findViewById(R.id.btnDeleteMain);
        btnView = (Button) findViewById(R.id.btnViewMain);
        txtInfo.setText(textInfo);
        verifyStoragePermission(this);
        getEmployeeInfo();
        btnInsert.setOnClickListener(new Clicker(1));
        btnUpdate.setOnClickListener(new Clicker(2));
        btnDelete.setOnClickListener(new Clicker(3));
        btnView.setOnClickListener(new Clicker(4));
    }


    public static void verifyStoragePermission(Activity act)  {
        int permission= ActivityCompat.checkSelfPermission(act, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act,PERMISSION_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }
    }

    public class Clicker implements View.OnClickListener {
        private int btnNo;
        @Override
        public void onClick(View v) {
            try{
                switch(v.getId()){
                    case R.id.btnInsertMain:  // insert button
                        insertEmployee();break;
                    case R.id.btnUpdateMain:  // update button
                        showEmployee();isCalled = false;break;
                    case R.id.btnDeleteMain:  // delete button
                        showEmployee();isCalled = false;break;
                    case R.id.btnViewMain:   // view button
                        vieEmployee();break;
                    default:break;
                }

                butNumber = btnNo;
               /* if(btnNo ==1){ // button Insert click
                    insertEmployee();
                }
                else if(btnNo == 2){ // button Update click
                    showEmployee();
                    isCalled = false;
                }
                else if(btnNo == 3){ // button Delete click
                    showEmployee();
                    isCalled = false;
                }
                else if(btnNo == 4){ // button View click
                    vieEmployee();
                }*/
            }catch(Exception e){
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        public Clicker(){

        }
        public Clicker(int buttonNo){
            btnNo = buttonNo;
        }
    }

    private void insertEmployee(){  // called when insert clicked
        Intent myIntent = new Intent(MainActivity.this, InsertActivity.class);
        Bundle myData = new Bundle();
        myData.putInt("RequestCode", REQUEST_CODE);
        myIntent.putExtras(myData);
        startActivityForResult(myIntent, REQUEST_CODE);
    }

    private void updateEmployee(){  // call when update
        Intent myIntent = new Intent(MainActivity.this, UpdateActivity.class);
        Bundle myData = new Bundle();
        myData.putInt("RequestCode", REQUEST_CODE);
        myData.putSerializable("empInfo", employeeData);
        myIntent.putExtras(myData);
        startActivityForResult(myIntent, REQUEST_CODE);
    }

    private void deleteEmployee(){ // call when delete
        Intent myIntent = new Intent(MainActivity.this, DeleteActivity.class);
        Bundle myData = new Bundle();
        myData.putInt("RequestCode", REQUEST_CODE);
        myData.putSerializable("empInfo", employeeData);
        myIntent.putExtras(myData);
        startActivityForResult(myIntent, REQUEST_CODE);
    }

    private void vieEmployee(){ // called when view clicked
        Intent myIntent = new Intent(MainActivity.this, ViewActivity.class);
        Bundle myData = new Bundle();
        myData.putInt("RequestCode", REQUEST_CODE);
        myData.putSerializable("employee", empDataList);
        myIntent.putExtras(myData);
        startActivityForResult(myIntent, REQUEST_CODE);
    }

    private void showEmployee(){  // called when click update or delete
        Intent myIntent = new Intent(MainActivity.this, InfoListActivity.class);
        Bundle myData = new Bundle();
        myData.putInt("RequestCode", REQUEST_CODE);
        myData.putSerializable("employee", empDataList);
        myIntent.putExtras(myData);
        startActivityForResult(myIntent, REQUEST_CODE);
    }
    public void getEmployeeInfo(){
        empDataList = new ArrayList<EmployeeData>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL("http://192.168.56.1:9090/Assignment17/EmployeeServlet");
                    httpURLConnection = (HttpURLConnection)  url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream (httpURLConnection.getOutputStream());
                    EmployeeData postData = new EmployeeData();
                    setObject(postData);
                    out.writeBytes(new Gson().toJson(postData));

                    out.flush();
                    out.close();
                    BufferedReader reader = null;
                    if(httpURLConnection.getResponseCode() == 200 || httpURLConnection.getResponseCode() == 201){
                        reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    }
                    String returnVal = "";
                    jsonStr = new StringBuilder() ;
                    while((returnVal = reader.readLine()) != null){
                        jsonStr.append(returnVal);
                    }
                    reader.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtList.setText("");
                            txtList.append("ID\t\tName\n");
                            Map<String,Object> map = new HashMap<String, Object>();
                            Gson gson = new Gson();
                            map = gson.fromJson(jsonStr.toString(), HashMap.class);
                            ArrayList<EmployeeData> dataList =  (ArrayList<EmployeeData>) map.get("list");

                            for (int i = 0; i < dataList.size(); i++) {
                                try {
                                    JSONObject jsonObject = new JSONObject(gson.toJson(dataList.get(i)));
                                    getJsonObject(jsonObject);
                                    txtList.append(jsonObject.get("empID")+"\t\t"+jsonObject.get("empName")+"\n");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    private void setObject( EmployeeData data){
        data.setSerialNo(0);
        data.setEmpID("");
        data.setEmpName("");
        data.setEmail("");
        data.setNrc("");
        data.setPhone("");
        data.setGender(0);
        data.setDob("");
        data.setStatus(3);
        data.setRecordStatus(1);
    }

    private void getJsonObject(JSONObject jsonObject)  {
        EmployeeData data = new EmployeeData();
        try {
            data.setSerialNo(jsonObject.getInt("serialNo"));
            data.setEmpID(jsonObject.getString("empID"));
            data.setEmpName(jsonObject.getString("empName"));
            data.setEmail(jsonObject.getString("email"));
            data.setNrc(jsonObject.getString("nrc"));
            data.setPhone(jsonObject.getString("phone"));
            data.setGender(jsonObject.getInt("gender"));
            data.setRecordStatus(jsonObject.getInt("RecordStatus"));
            data.setDob(jsonObject.getString("dob"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        empDataList.add(data);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(REQUEST_CODE == requestCode){
                if(resultCode == Activity.RESULT_OK){
                    Bundle myBundle = data.getExtras();

                    if(butNumber == 1){
                        getEmployeeInfo();
                    }
                    else if(butNumber == 2){
                        if(!isCalled){
                            ArrayList<EmployeeData> empList = ( ArrayList<EmployeeData>) myBundle.getSerializable("employee");
                            employeeData = empList.get(0);
                            updateEmployee();
                            isCalled = true;
                        }
                        else if(isCalled)
                            getEmployeeInfo();
                    }
                    else if(butNumber == 3){
                        if(!isCalled){
                            ArrayList<EmployeeData> empList = ( ArrayList<EmployeeData>) myBundle.getSerializable("employee");
                            employeeData = empList.get(0);
                            deleteEmployee();
                            isCalled = true;
                        }
                        else if(isCalled)
                            getEmployeeInfo();

                    }
                    else if(butNumber == 4){
                        getEmployeeInfo();
                    }
                }
                else{
                    Toast.makeText(getBaseContext(), "Selection Canceled!", Toast.LENGTH_SHORT).show();
                    getEmployeeInfo();
                }
            }
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.insert_menu:
                butNumber = 1;
                insertEmployee();
                return true;
            case R.id.update_menu:
                butNumber = 2;
                showEmployee();
                isCalled = false;
                return true;
            case R.id.delete_menu:
                butNumber = 3;
                showEmployee();
                isCalled = false;
                return true;
            case R.id.view_menu:
                butNumber = 4;
                vieEmployee();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
