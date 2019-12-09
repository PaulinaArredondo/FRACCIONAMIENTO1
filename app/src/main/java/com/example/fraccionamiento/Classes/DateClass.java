package com.example.fraccionamiento.Classes;

// Clase que convierte el formato de mes numerico a alfabetico


public class DateClass {
    private String monthS;
    private int month;

    public DateClass(int month) {
        this.month = month;
    }

    public String parseStringMonth() {
        switch (month) {
            case 1:  this.monthS = "Enero";
                return monthS;
            case 2:  this.monthS = "Febrero";
                return monthS;
            case 3:  this.monthS = "Marzo";
                return monthS;
            case 4:  this.monthS = "Abril";
                return monthS;
            case 5:  this.monthS = "Mayo";
                return monthS;
            case 6:  this.monthS = "Junio";
                return monthS;
            case 7:  this.monthS = "Julio";
                return monthS;
            case 8:  this.monthS = "Agosto";
                return monthS;
            case 9:  this.monthS = "Septiembre";
                return monthS;
            case 10: this.monthS = "Octubre";
                return monthS;
            case 11: this.monthS = "Noviembre";
                return monthS;
            case 12: this.monthS = "Diciembre";
                return monthS;
            default: this.monthS = "Invalid monthS";
                return monthS;
        }
    }
}

