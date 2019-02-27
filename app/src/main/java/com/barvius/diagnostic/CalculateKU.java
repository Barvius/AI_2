package com.barvius.diagnostic;

import com.barvius.diagnostic.entity.Symptom;

import java.util.List;

public class CalculateKU {
    public static Double calculate(List<Symptom> symptoms){
        while(symptoms.size() > 1){
            Symptom d1 = symptoms.remove(0);
            Symptom d2 = symptoms.remove(0);
//            Log.d("MyLog", "d1=" + d1.getMd());
//            Log.d("MyLog", "d2 =" + d2.getMd());

            symptoms.add(new Symptom(0,null,d1.getMd()+d2.getMd()*(1-d1.getMd()),d1.getMnd()+d2.getMnd()*(1-d1.getMnd())));
//            Log.d("MyLog", "all =" + diagnose.getSymptoms().get(diagnose.getSymptoms().size()-1).getMd());
        }
        if (symptoms.size() == 0){
            return 0.0;
        }
        try {
            return (symptoms.get(0).getMd() - symptoms.get(0).getMnd());
        } catch (Exception e){
            return 0.0;
        }
    }
}
