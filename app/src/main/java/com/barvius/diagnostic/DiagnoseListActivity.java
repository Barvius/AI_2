package com.barvius.diagnostic;

import android.content.Intent;
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

import com.barvius.diagnostic.entity.Diagnose;
import com.barvius.diagnostic.ui.StatusBarTools;

import java.util.ArrayList;
import java.util.List;

public class DiagnoseListActivity extends AppCompatActivity {
    private List<Diagnose> diagnoseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Диагнозы");
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
        if (v.getId()==R.id.diagnose_dynamic_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
//            menu.setHeaderTitle("");
            String[] menuItems = new String[]{"Редактировать","Удалить"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()){
            case 0:
                long id = DBHandler.getInstance().getDiagnoseIdByName(diagnoseList.get((int) info.id).getName());
                if(id == -1){
                    break;
                }
                Intent intent = new Intent(DiagnoseListActivity.this, DiagnoseEditActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                loadList();
                break;
            case 1:
                long removeId = DBHandler.getInstance().getDiagnoseIdByName(diagnoseList.get((int) info.id).getName());
                if(removeId == -1){
                    break;
                }
                DBHandler.getInstance().removeDiagnose(removeId);
                loadList();
                break;
        }
        return true;
    }

    protected void loadList(){
        this.diagnoseList = DBHandler.getInstance().selectDiagnoseNames();
        ListView list = (ListView) findViewById(R.id.diagnose_dynamic_list);
        ArrayAdapter<Diagnose> adapter = new ArrayAdapter<Diagnose>(this,
                android.R.layout.simple_list_item_1, this.diagnoseList);
        list.setAdapter(adapter);
        registerForContextMenu(list);
    }

    protected void addNew(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавление диагноза");

        final EditText name = new EditText(this);
        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        name.setHint("Диагноз");

        builder.setView(name);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            if(name.getText().length() > 0){
                long id = DBHandler.getInstance().insertDiagnoseName(new Diagnose(name.getText().toString()));
                if(id == -1){
                    id = DBHandler.getInstance().getDiagnoseIdByName(name.getText().toString());
                    if(id == -1){
                        return;
                    }
                }
                Intent intent = new Intent(DiagnoseListActivity.this, DiagnoseEditActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                loadList();
            }
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
