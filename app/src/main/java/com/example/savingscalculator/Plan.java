package com.example.savingscalculator;

import java.io.Serializable;

public class Plan implements Serializable {

    private String name;
    private double rate, inflation;
    private int principal, years, compFreq;

    //Constructors
    public Plan(String name, int principal, double rate, double inflation, int years, int compFreq) {
        this.name = name;
        this.principal = principal;
        this.rate = rate;
        this.inflation = inflation;
        this.years = years;
        this.compFreq = compFreq;
    }

    public Plan() {
    }

    //Setters And Getters
    public int getPrincipal() {
        return principal;
    }

    public void setPrincipal(int principal) {
        this.principal = principal;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public int getCompFreq() {
        return compFreq;
    }

    public void setCompFreq(int compFreq) {
        this.compFreq = compFreq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getInflation() {
        return inflation;
    }

    public void setInflation(double inflation) {
        this.inflation = inflation;
    }

}
