package com.barvius.diagnostic.entity;

public class Symptom {
    private long id;
    private String name;
    private double md;
    private double mnd;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Symptom(){}

    public Symptom(String name) {
        this.name = name;
    }

    public Symptom(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Symptom(long id, String name, double md, double mnd) {
        this.id = id;
        this.name = name;
        this.md = md;
        this.mnd = mnd;
    }

    @Override
    public String toString() {
        return name;
    }

    public double getMd() {
        return md;
    }

    public void setMd(double md) {
        this.md = md;
    }

    public double getMnd() {
        return mnd;
    }

    public void setMnd(double mnd) {
        this.mnd = mnd;
    }
}
