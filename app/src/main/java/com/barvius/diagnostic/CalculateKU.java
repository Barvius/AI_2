package com.barvius.diagnostic;

import android.util.Log;

import com.barvius.diagnostic.entity.Diagnose;
import com.barvius.diagnostic.entity.Symptom;

import java.util.List;

public class CalculateKU {
    public static float calculate(Diagnose diagnose){
        while(diagnose.getSymptoms().size() > 1){
            Symptom d1 = diagnose.getSymptoms().remove(0);
            Symptom d2 = diagnose.getSymptoms().remove(0);
//            Log.d("MyLog", "d1=" + d1.getMd());
//            Log.d("MyLog", "d2 =" + d2.getMd());

            diagnose.addSymptom(new Symptom(0,null,d1.getMd()+d2.getMd()*(1-d1.getMd()),d1.getMnd()+d2.getMnd()*(1-d1.getMnd())));
//            Log.d("MyLog", "all =" + diagnose.getSymptoms().get(diagnose.getSymptoms().size()-1).getMd());
        }
        try {
            return (float) (diagnose.getSymptoms().get(0).getMd() - diagnose.getSymptoms().get(0).getMnd());
        } catch (Exception e){
            return -1;
        }
    }
}
