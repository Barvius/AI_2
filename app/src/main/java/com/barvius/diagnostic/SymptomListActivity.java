package com.barvius.diagnostic;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.barvius.diagnostic.entity.Diagnose;
import com.barvius.diagnostic.entity.Symptom;
import com.barvius.diagnostic.ui.StatusBarTools;

import java.util.ArrayList;
import java.util.List;

public class SymptomListActivity extends AppCompatActivity {
    List<Symptom> symptomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Симптомы");
        setSupportActionBar(myToolbar);
        StatusBarTools.setStatusBarColor(getWindow(),getResources().getColor(R.color.backgroundAppBarDark));

        loadList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_add) {
            addNew();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.symptom_dynamic_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
//            menu.setHeaderTitle("");
            menu.add(Menu.NONE, 0, 0,"Удалить");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()){
            case 0:
                symptomList.get((int) info.id).getName();
                List<Diagnose> list = DBHandler.getInstance().selectDiagnoses();
                boolean fl =false;
//                if (list.size() >= 2){
                for (Diagnose i:list) {
                    i.deleteSymptom(symptomList.get((int) info.id));
                }
                for (Diagnose i:list) {
                    if(CompareDiagnoses.compare(list,i)){
                        fl = true;
                    }
                }
//                }
                if(fl){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Обнаружены вхождения. Отмена удаления.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    DBHandler.getInstance().removeSymptom(symptomList.get((int) info.id).getId());
//                    for (Diagnose i:list) {
//                        DBHandler.getInstance().updateDiagnoseSymptoms(i);
//                    }
                }



                loadList();
                break;
        }
        return true;
    }

    protected void loadList(){
        this.symptomList = DBHandler.getInstance().selectSymptomNames();
        ListView list = (ListView) findViewById(R.id.symptom_dynamic_list);
        ArrayAdapter<Symptom> adapter = new ArrayAdapter<Symptom>(this,
                android.R.layout.simple_list_item_1, this.symptomList);
        list.setAdapter(adapter);
        registerForContextMenu(list);
    }

    protected void addNew(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавление симптома");

        final EditText name = new EditText(this);
        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        name.setHint("Симптом");

        builder.setView(name);

        builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(name.getText().length() > 0){
                    DBHandler.getInstance().insertSymptom(new Symptom(name.getText().toString()));
                    loadList();
                }
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
