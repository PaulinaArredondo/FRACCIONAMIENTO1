package com.example.fraccionamiento.Classes;

import com.google.firebase.database.Exclude;

import java.util.HashMap;


// Clase para traer los datos de los pagos de los usuarios

public class PaymentsClass {
    private int month;
    private int year;
    private int mountPayed;
    private Boolean payed;
    private Boolean debt;
    private int mountDebt;
    private String key;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public PaymentsClass() {
    }

    public PaymentsClass(int month, int year, int mountPayed, Boolean payed, Boolean debt, int mountDebt) {
        this.month = month;
        this.year = year;
        this.mountPayed = mountPayed;
        this.payed = payed;
        this.debt = debt;
        this.mountDebt = mountDebt;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMountPayed() {
        return mountPayed;
    }

    public void setMountPayed(int mount) {
        this.mountPayed = mount;
    }

    public Boolean isPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public Boolean isDebt() {
        return debt;
    }

    public void setDebt(Boolean debt) {
        this.debt = debt;
    }

    public int getMountDebt() {
        return mountDebt;
    }

    public void setMountDebt(int mountDebt) {
        this.mountDebt = mountDebt;
    }

    @Exclude
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("month", month);
        map.put("year", year);
        map.put("mountPayed", mountPayed);
        map.put("payed", payed);
        map.put("debt", debt);
        map.put("mountDebt", mountDebt);

        return map;
    }

}
