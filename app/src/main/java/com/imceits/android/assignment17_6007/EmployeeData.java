package com.imceits.android.assignment17_6007;

import java.io.Serializable;

public class EmployeeData implements Serializable {

    private static final long serialVersionUID = 1L;

    private int serialNo;
    private String empID;
    private String empName;
    private String email;
    private String nrc;
    private String phone;
    private String dob;
    private int gender;
    private int RecordStatus;
    private int status;

    public EmployeeData(){
        super();
        clearProperties();
    }
    private void clearProperties(){
        this.serialNo = 0;
        this.empID = "";
        this.empName = "";
        this.email = "";
        this.nrc = "";
        this.phone = "";
        this.dob = "";
        this.RecordStatus = 0;
        this.status = 0;
        this.gender = 0;
    }
    public int getSerialNo() {
        return serialNo;
    }
    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }
    public String getEmpID() {
        return empID;
    }
    public void setEmpID(String empID) {
        this.empID = empID;
    }
    public String getEmpName() {
        return empName;
    }
    public void setEmpName(String empName) {
        this.empName = empName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNrc() {
        return nrc;
    }
    public void setNrc(String nrc) {
        this.nrc = nrc;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public int getRecordStatus() {
        return RecordStatus;
    }
    public void setRecordStatus(int recordStatus) {
        RecordStatus = recordStatus;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getGender() {
        return gender;
    }
    public void setGender(int gender) {
        this.gender = gender;
    }
    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }


}
