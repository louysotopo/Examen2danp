package com.example.sensores.model;

public class ABS {
    private int count;
    private String horaS;
    private String horaF;
    private String date;

    public ABS(int count, String horaS, String horaF, String date) {
        this.count = count;
        this.horaS = horaS;
        this.horaF = horaF;
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getHoraS() {
        return horaS;
    }

    public void setHoraS(String horaS) {
        this.horaS = horaS;
    }

    public String getHoraF() {
        return horaF;
    }

    public void setHoraF(String horaF) {
        this.horaF = horaF;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "{ \"abs\"{ \"hourstart\" = \""+ horaS + '\"' +
                ", \"hourend\":\"" + horaF + '\"' +
                ", \"date\":\"" + date + '\"' +
                ", \"nabs\":" + count +
                "} }";
    }
}
