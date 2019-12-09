package com.example.fraccionamiento.Classes;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


// Clase para oftener la información de los pagos de los usuarios

public class PaymentInfoClass {
    private String arrived;
    private int maintenance;
    private int paymentDay;
    private int rent;
    private int rentTotal;
    public PaymentInfoClass() {
    }

    public PaymentInfoClass(String arrived, int maintenance, int paymentDay, int rent, int rentTotal) {
        this.arrived = arrived;
        this.maintenance = maintenance;
        this.paymentDay = paymentDay;
        this.rent = rent;
        this.rentTotal = rentTotal;
    }

    public PaymentInfoClass(int maintenance, int paymentDay, int rent, int rentTotal) {
        this.maintenance = maintenance;
        this.paymentDay = paymentDay;
        this.rent = rent;
        this.rentTotal = rentTotal;
    }
    public String getArrived() {
        return arrived;
    }

    public void setArrived(String arrived) {
        this.arrived = arrived;
    }

    public int getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(int maintenance) {
        this.maintenance = maintenance;
    }

    public int getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(int paymentDay) {
        this.paymentDay = paymentDay;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public int getRentTotal() {
        return rentTotal;
    }

    public void setRentTotal(int rentTotal) {
        this.rentTotal = rentTotal;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("maintenance", maintenance);
        result.put("paymentDay", paymentDay);
        result.put("rent", rent);
        result.put("arrived", arrived);
        result.put("rentTotal", rentTotal);
        return result;
    }
}
