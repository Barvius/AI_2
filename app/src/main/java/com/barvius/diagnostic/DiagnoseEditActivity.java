package com.barvius.diagnostic;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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
        symptomList.addAll(DBHandler.getInstance().selectSymptomNames());

        for (Symptom i : symptomList) {
            CheckBox tmp = new CheckBox(new ContextThemeWrapper(this, R.style.uiCheckBoxStyle));
            tmp.setText(i.getName());
            tmp.setId(symptomList.indexOf(i));
            if(currentDiagnose.symptomAvailable(i)){
                i.setMd(currentDiagnose.getSymptomById(i.getId()).getMd());
                i.setMnd(currentDiagnose.getSymptomById(i.getId()).getMnd());
                tmp.setChecked(true);
            }

            tmp.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final View customLayout = getLayoutInflater().inflate(R.layout.dialog_layout_md, null);
                    builder.setView(customLayout);
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    });

                    TextView mnd = (TextView) customLayout.findViewById(R.id.dialog_md_mnd);
                    SeekBar sbmnd = (SeekBar) customLayout.findViewById(R.id.dialog_md_mndp);

                    TextView md = (TextView) customLayout.findViewById(R.id.dialog_md_md);
                    SeekBar sbmd = (SeekBar) customLayout.findViewById(R.id.dialog_md_mdp);

                    if(symptomList.get(buttonView.getId()).getMd() == 0 && symptomList.get(buttonView.getId()).getMnd() == 0){
                        symptomList.get(buttonView.getId()).setMd(0.5);
                        symptomList.get(buttonView.getId()).setMnd(0.25);
                    }
                    md.setText(Double.toString(symptomList.get(buttonView.getId()).getMd()));
                    mnd.setText(Double.toString(symptomList.get(buttonView.getId()).getMnd()));
                    sbmnd.setProgress((int) (symptomList.get(buttonView.getId()).getMnd() * 100));
                    sbmd.setProgress((int) (symptomList.get(buttonView.getId()).getMd() * 100));

                    sbmnd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                            symptomList.get(buttonView.getId()).setMnd(((double)progress/100)*symptomList.get(buttonView.getId()).getMd());
                            mnd.setText(Double.toString(((double)progress/100)*symptomList.get(buttonView.getId()).getMd()));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar arg0) { }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) { }
                    });

                    sbmd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                            symptomList.get(buttonView.getId()).setMd((double)progress/100);
                            md.setText(Double.toString((double)progress/100));
                            sbmnd.setProgress(sbmnd.getProgress()+1);
                            sbmnd.setProgress(sbmnd.getProgress()-1);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar arg0) { }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) { }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
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
