package com.barvius.diagnostic;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.barvius.diagnostic.entity.Diagnose;
import com.barvius.diagnostic.entity.Symptom;
import com.barvius.diagnostic.ui.StatusBarTools;

import java.util.ArrayList;
import java.util.List;

public class DiagnoseEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose_edit);
        long id = getIntent().getLongExtra("id",0);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(DBHandler.getInstance().selectDiagnoseById(id).getName());
        setSupportActionBar(myToolbar);
        StatusBarTools.setStatusBarColor(getWindow(),getResources().getColor(R.color.backgroundAppBarDark));

        createArea(id);
    }

    protected void createArea(final long id){
        LinearLayout container = (LinearLayout) findViewById(R.id.diagnose_symptoms_list);
        final List<CheckBox> checkBoxList = new ArrayList<>();
        final List<Symptom> symptomList = new ArrayList<>();
        final Diagnose currentDiagnose = DBHandler.getInstance().selectDiagnoseById(id);
        final Button button = findViewById(R.id.btn_diagnose_save);

//        TextView name = (TextView) findViewById(R.id.diagnose_name);
//        name.setText(currentDiagnose.getName());


        symptomList.addAll(DBHandler.getInstance().selectSymptomNames());

        for (Symptom i : symptomList) {
            CheckBox tmp = new CheckBox(new ContextThemeWrapper(this, R.style.uiCheckBoxStyle));
            tmp.setText(i.getName());
            tmp.setId(symptomList.indexOf(i));
            if(currentDiagnose.symptomAvailable(i)){
                i.setConfidence(currentDiagnose.getSymptomById(i.getId()).getConfidence());
                tmp.setChecked(true);
            }

            tmp.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    double currentProgress;
                    final AlertDialog.Builder popDialog = new AlertDialog.Builder(DiagnoseEditActivity.this);
                    popDialog.setCancelable(false);
                    popDialog.setPositiveButton("OK",
                            (dialog, which) -> dialog.dismiss());
                    final SeekBar seek = new SeekBar(new ContextThemeWrapper(DiagnoseEditActivity.this, R.style.uiSeekBar));
                    seek.setMax(100);
                    seek.setProgress(50);
                    final AlertDialog handle = popDialog.create();
                    handle.setTitle("Уровень доверия (50%)");
                    symptomList.get(buttonView.getId()).setConfidence(0.5);
                    handle.setView(seek);
                    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                            handle.setTitle("Уровень доверия ("+progress+"%)");
                            symptomList.get(buttonView.getId()).setConfidence((double)progress/100);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar arg0) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });
                    handle.show();
                }

                currentDiagnose.clearSymptoms();
                currentDiagnose.addSymptoms(getSelectedSymptoms(checkBoxList, symptomList));
                button.setEnabled(true);
                if(CompareDiagnoses.compare(DBHandler.getInstance().selectDiagnoses(),currentDiagnose)){
                    button.setEnabled(false);
                }
            });


            checkBoxList.add(tmp);
            container.addView(tmp);
        }


        button.setOnClickListener(v -> {
            DBHandler.getInstance().updateDiagnoseSymptoms(currentDiagnose);
            finish();
        });
    }

    List<Symptom> getSelectedSymptoms(List<CheckBox> checkBoxList, List<Symptom> symptomList){
        List<Symptom> list = new ArrayList<>();
        for (CheckBox i: checkBoxList) {
            if (i.isChecked()){
                list.add(symptomList.get(i.getId()));
            }
        }
        return list;
    }


}
