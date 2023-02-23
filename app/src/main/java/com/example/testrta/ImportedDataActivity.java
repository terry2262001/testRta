package com.example.testrta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.testrta.Adapter.DataAdapter;
import com.example.testrta.Model.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportedDataActivity extends AppCompatActivity {
    RecyclerView rvImportedData ;
    ArrayList<Data> dataList;
    DataAdapter dataAdapter;
    Data clickData ;
    List<File> selectedFiles = new ArrayList<File>();
    File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imported_data);

        rvImportedData = findViewById(R.id.rvImportedData);
        dataList = new ArrayList<>();
        rvImportedData.setLayoutManager(new LinearLayoutManager(ImportedDataActivity.this, RecyclerView.VERTICAL, false));
        rvImportedData.addItemDecoration(new DividerItemDecoration(ImportedDataActivity.this, DividerItemDecoration.VERTICAL));
        dataAdapter = new DataAdapter(ImportedDataActivity.this, dataList );
        rvImportedData.setAdapter(dataAdapter);

        try {
            reachDat();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void reachDat() throws IOException {
        // File directory = new File(Environment.getExternalStorageDirectory() + "/data");
        File folder = new File(getFilesDir(), "official-data");
        files = folder.listFiles();
        if (files != null) {
            for (File file : files) {

                if (file.isFile() && file.getName().endsWith(".xml")) {
                    dataList.add(new Data(file.getName(), file.getPath()));
                    dataAdapter.notifyDataSetChanged();
                }
            }
        }
    }


}
