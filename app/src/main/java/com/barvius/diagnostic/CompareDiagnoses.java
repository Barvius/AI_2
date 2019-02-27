package com.barvius.diagnostic;

import com.barvius.diagnostic.entity.Diagnose;
import com.barvius.diagnostic.entity.Symptom;

import java.util.ArrayList;
import java.util.List;

public class CompareDiagnoses {
    public static boolean inSequence(List<Symptom> sequence, Symptom symptom){
        for (Symptom i:sequence) {
            if(i.getId() == symptom.getId()){
                return true;
            }
        }
        return false;
    }

    public static boolean symptomCompare(List<Symptom> A, List<Symptom> B){
        List<Symptom> a = new ArrayList<>();
        List<Symptom> b = new ArrayList<>();
        boolean result = true;
        if(A.size() < B.size()){
            a.addAll(A);
            b.addAll(B);
        } else {
            a.addAll(B);
            b.addAll(A);
        }
        for (Symptom i: a) {
            if(!CompareDiagnoses.inSequence(b,i)){
                result = false;
            }
        }
        return result;
    }

    public static boolean compare(List<Diagnose> diagnoseList, Diagnose diagnose){
        for (Diagnose i: diagnoseList) {
            if(!i.getSymptoms().isEmpty() && !diagnose.getSymptoms().isEmpty()){
                if(i.getId() != diagnose.getId()){
                    boolean r = CompareDiagnoses.symptomCompare(diagnose.getSymptoms(),i.getSymptoms());
                    if(r){
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public static long compareTestResult(List<Diagnose> diagnoseList, List<Symptom> symptomList){
        boolean exist = true;
        long id = -1;
        for (Diagnose i: diagnoseList) {
            if(i.getSymptoms().size() == symptomList.size() && i.getSymptoms().size() > 0 && symptomList.size() > 0){
                exist = true;
                for (Symptom j: symptomList) {
                    if(!CompareDiagnoses.inSequence(i.getSymptoms(),j)){
                        exist = false;
                    }
                }
                if(exist){
                    id = i.getId();
                }
            }
        }
        return id;
    }

    public static Diagnose inSet(List<Symptom> selected, Diagnose diagnose){
        List<Symptom> s = new ArrayList<>();
        for (Symptom i: diagnose.getSymptoms()) {
            if(CompareDiagnoses.inSequence(selected,i)){
                s.add(i);
            }
        }
        diagnose.clearSymptoms();
        diagnose.addSymptoms(s);
        return diagnose;
    }
}
