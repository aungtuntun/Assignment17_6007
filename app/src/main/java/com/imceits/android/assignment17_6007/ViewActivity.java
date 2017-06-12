package com.imceits.android.assignment17_6007;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    EditText txtSearch;
    RadioGroup radGroup;
    RadioButton radName;
    RadioButton radID;
    TextView txtShowList;
    Button btnSearch;
    Button btnClose;
    ArrayList<EmployeeData> empDataList;

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_view);

        radGroup = (RadioGroup) findViewById(R.id.radGroup1);
        radName = (RadioButton) findViewById(R.id.radName);
        radID = (RadioButton) findViewById(R.id.radID);
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        txtShowList = (TextView) findViewById(R.id.txtShowList);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnClose = (Button) findViewById(R.id.btnSearchClose);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( isValid()){
                    if(radName.getId() == radGroup.getCheckedRadioButtonId()){
                        findEmployee(txtSearch.getText().toString(), 1, empDataList);
                    }
                    else if(radID.getId() == radGroup.getCheckedRadioButtonId()){
                        findEmployee(txtSearch.getText().toString(), 2, empDataList);
                    }
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Intent myIntentView = getIntent();
        Bundle myBundle = myIntentView.getExtras();
        empDataList = ( ArrayList<EmployeeData>) myBundle.getSerializable("employee");
        showEmployeeInfo(empDataList);
        myBundle.putSerializable("employee",empDataList);
        myIntentView.putExtras(myBundle);
        setResult(Activity.RESULT_OK, myIntentView);
    }

    private void showEmployeeInfo(ArrayList<EmployeeData> dataList){
        txtShowList.setText("ID \t Name \t Rank\n\n");
        for(int i=0;i<dataList.size();i++){
            EmployeeData data = dataList.get(i);
            txtShowList.append(data.getEmpID()+"\t\t" + data.getEmpName()+"\n");
        }
    }

    private boolean isValid(){
        String name = txtSearch.getText().toString().trim();
        if(name.equals("")){
            Toast.makeText(this, "ID or Name is required!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!radName.isChecked() && !radID.isChecked()){
            Toast.makeText(this, "Radio button ID or Name is required!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void findEmployee(String param, int callType, ArrayList<EmployeeData> dataList){
        EmployeeData data = null;
        param = param.trim().toLowerCase().replace(" ", "");
        txtShowList.setText("");
        String searResult = "";
        for(int i=0;i<dataList.size();i++){
            if(callType == 1){
                String name = dataList.get(i).getEmpName().trim().toLowerCase().replace(" ", "");
                if(name.contains(param)){
                    data = dataList.get(i);
                    String gender = "";
                    if(data.getGender() == 1)
                        gender = "Male";
                    else if(data.getGender() == 2)
                        gender = "Female";
                    searResult += "ID : "+data.getEmpID()+"\nName : " + data.getEmpName()+"\nNRC : "+data.getNrc()+"\nEmail : "+data.getEmail()
                            +"\nGender : "+gender+"\nPhone : "+data.getPhone()+"\nDOB : "+data.getDob()+"\n";
                }
            }
            else if(callType == 2){
                String roll = dataList.get(i).getEmpID().trim().toLowerCase().replace(" ", "");
                if(roll.equalsIgnoreCase(param)){
                    data = dataList.get(i);
                    String gender = "";
                    if(data.getGender() == 1)
                        gender = "Male";
                    else if(data.getGender() == 2)
                        gender = "Female";
                    searResult += "ID : "+data.getEmpID()+"\nName : " + data.getEmpName()+"\nNRC : "+data.getNrc()+"\nEmail : "+data.getEmail()
                            +"\nGender : "+gender+"\nPhone : "+data.getPhone()+"\nDOB : "+data.getDob()+"\n ";
                }
            }
        }

        if( !searResult.equals("")){
            txtShowList.append(searResult);
        }
        else{
            txtShowList.append("No data found!");
        }

    }
}
