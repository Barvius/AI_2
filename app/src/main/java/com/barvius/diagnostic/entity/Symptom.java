package com.barvius.diagnostic.entity;

public class Symptom {
    private long id;
    private String name;
    private double confidence;

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

    public Symptom(long id, String name, double confidence) {
        this.id = id;
        this.name = name;
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return name;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
