package com.barvius.diagnostic;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.barvius.diagnostic.entity.Diagnose;
import com.barvius.diagnostic.entity.Symptom;
import com.barvius.diagnostic.entity.TestAnswer;
import com.barvius.diagnostic.ui.StatusBarTools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestActivity extends AppCompatActivity {
    private int current;
    private List<TestAnswer> answersList = new ArrayList<>();
    Button btn_test_back;
    Button btn_test_next;
    RadioButton a_y_test;
    RadioButton a_n_test;
    TextView q_text;
    ProgressBar pb_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        StatusBarTools.setStatusBarColor(getWindow(),getResources().getColor(R.color.backgroundAppBarDark));

        if(DBHandler.getInstance().selectDiagnoses().size() > 0 && DBHandler.getInstance().selectSymptomNames().size() > 0){
            for (Symptom i: DBHandler.getInstance().selectSymptomNames()) {
                answersList.add(new TestAnswer(i));
            }
            current = 0;
            createTest();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "В базе отсутствуют диагнозы или симптомы!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    protected void createTest(){
        q_text = (TextView) findViewById(R.id.q_test);
        a_y_test = (RadioButton) findViewById(R.id.a_y_test);
        a_n_test = (RadioButton) findViewById(R.id.a_n_test);
        btn_test_back = findViewById(R.id.btn_test_back);
        btn_test_next = findViewById(R.id.btn_test_next);
        pb_test = findViewById(R.id.pb_test);
        pb_test.setMax(answersList.size());
        a_y_test.setOnClickListener(v -> {
            answersList.get(current).setSelect(true);
            a_n_test.setChecked(false);
        });

        a_n_test.setOnClickListener(v -> {
            answersList.get(current).setSelect(false);
            a_y_test.setChecked(false);
        });
        btn_test_back.setOnClickListener(v -> prev());
        btn_test_next.setOnClickListener(v -> next());
        test();
    }

    protected void next(){
        if (current == answersList.size()-1){
            searchDiagnose();
        } else {
            current++;
        }
        test();
    }

    protected void prev(){
        if(current == 0){
            finish();
        } else {
            current--;
        }
        test();
    }

    protected void test(){
        q_text.setText(answersList.get(current).getSymptom().getName()+"?");
        a_y_test.setChecked(answersList.get(current).isSelect());
        a_n_test.setChecked(!answersList.get(current).isSelect());
        pb_test.setProgress(current+1);
        if (current == 0){
            btn_test_back.setText("Завершить");
        } else {
            btn_test_back.setText("Назад");
        }
        if (current == answersList.size()-1) {
            btn_test_next.setText("Диагноз");
        } else {
            btn_test_next.setText("Далее");
        }
    }

    protected void searchDiagnose(){
        List<Symptom> d = new ArrayList<>();
        for (TestAnswer i:answersList) {
            if(i.isSelect()){
                d.add(i.getSymptom());
            }
        }
        TreeMap<Double,String> treeMap = new TreeMap<Double,String>((o1, o2) -> {
            if (o1 == o2) return 0;
            if (o1 > o2) return -1;
            else return 1;
        });
        for (Diagnose i: DBHandler.getInstance().selectDiagnoses()) {
            treeMap.put((double) Math.round(CalculateKU.calculate(CompareDiagnoses.inSet(d,i).getSymptoms()) * 100) / 100,i.getName());
        }
        String text = "";
        for (Map.Entry<Double,String> i : treeMap.entrySet()) {
            if(i.getKey() > 0.5){
                text += i.getValue() + " ("+ i.getKey()+")\n";
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Результат теста")
                .setMessage(text)
                .setCancelable(false)
                .setNegativeButton("Назад",
                        (dialog, id1) -> dialog.cancel())
                .setPositiveButton("Завершить",
                        (dialog, id12) -> finish());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
