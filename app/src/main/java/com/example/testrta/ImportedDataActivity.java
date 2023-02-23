package com.example.testrta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;

import com.example.testrta.Adapter.DataAdapter;
import com.example.testrta.Adapter.DataImportAdapter;
import com.example.testrta.Model.Data;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportedDataActivity extends AppCompatActivity implements DataImportAdapter.onClickItem {
    RecyclerView rvImportedData ;
    ArrayList<Data> dataList;
    DataImportAdapter dataImportAdapter;
    Data clickData ;
    List<File> selectedFiles = new ArrayList<File>();
    File[] files;
    String contentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imported_data);

        rvImportedData = findViewById(R.id.rvImportedData);
        dataList = new ArrayList<>();
        rvImportedData.setLayoutManager(new LinearLayoutManager(ImportedDataActivity.this, RecyclerView.VERTICAL, false));
        rvImportedData.addItemDecoration(new DividerItemDecoration(ImportedDataActivity.this, DividerItemDecoration.VERTICAL));
        dataImportAdapter = new DataImportAdapter(ImportedDataActivity.this, dataList ,ImportedDataActivity.this);
        rvImportedData.setAdapter(dataImportAdapter);

        try {
            reachData();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void reachData() throws IOException {
        File folder = new File(getFilesDir(), "official-data");
        files = folder.listFiles();
        if (files != null) {
            for (File file : files) {

                if (file.isFile() && file.getName().endsWith(".xml")) {
                    dataList.add(new Data(file.getName(), file.getPath()));
                    dataImportAdapter.notifyDataSetChanged();
                }
            }
        }
    }



    @Override
    public void onItemOnClick(Data data) {
        clickData = data;
        Intent   intent = new Intent(ImportedDataActivity.this, DetailContentActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);



    }
}
