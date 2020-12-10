package com.example.rommies;

import java.util.ArrayList;

public class Payment {
    private double amount;
    private String Payer;
//    private ArrayList<String> Uid3;
    private  String Reason;

    public void payment (){

    }

    public Payment(String u1, double money , String r){//, ArrayList<String> l){
        this.Payer=u1;
        this.amount=money;
        this.Reason=r;
//        this.Uid3=l;

    }

//    public ArrayList<String> getUid3() {
//        return this.Uid3;
//    }
//
//    public void setUid3(ArrayList<String> uid3) {
//        this.Uid3 = uid3;
//    }

    public String getReason() { return this.Reason; }

    public void setReason(String reason) {this.Reason = reason; }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPayer(String Payer) {
        this.Payer = Payer;
    }

    public double getAmount() {
        return amount;
    }

    public String getPayer() {
        return Payer;
    }


}
