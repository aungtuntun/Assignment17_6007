package com.imceits.android.assignment17_6007;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class InfoListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner listView;
    TextView txtHeader;
    EmployeeData employeeData;
    String[] items ;
    Button btnOkay;
    ArrayList<EmployeeData> empDataList;
    ArrayList<EmployeeData> empDataBundleList;
    Intent myIntentView;
    Bundle myBundle;
    String idNo;
    String name;
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_show);

        listView = (Spinner) findViewById(R.id.listView);
        txtHeader = (TextView)findViewById(R.id.txtHeader);
        btnOkay = (Button) findViewById(R.id.btnOkay);

        myIntentView = getIntent();
        myBundle = myIntentView.getExtras();
        empDataList = ( ArrayList<EmployeeData>) myBundle.getSerializable("employee");
        items = new String[empDataList.size()];

        for(int i=0;i<empDataList.size();i++){
            items[i] = empDataList.get(i).getEmpID() + "---" + empDataList.get(i).getEmpName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        listView.setAdapter(adapter);
        listView.setOnItemSelectedListener(this);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBundle();
                finish();
            }
        });
    }

    public void setBundle(){
        empDataBundleList = new ArrayList<EmployeeData>();
        employeeData = new EmployeeData();
        for(int i=0;i<empDataList.size();i++){
            String sName = empDataList.get(i).getEmpName();
            String id = empDataList.get(i).getEmpID();
            if(sName.equalsIgnoreCase(name) && id.equalsIgnoreCase(idNo)){
                employeeData = empDataList.get(i); break;
            }
        }
        empDataBundleList.add(employeeData);
        myBundle.putSerializable("employee", empDataBundleList);
        myIntentView.putExtras(myBundle);
        setResult(Activity.RESULT_OK, myIntentView);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
        idNo = items[index].split("---")[0];
        name = items[index].split("---")[1];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
