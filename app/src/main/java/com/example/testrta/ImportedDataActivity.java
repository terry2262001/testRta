package com.example.testrta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Xml;
import android.view.View;

import com.example.testrta.Adapter.DataAdapter;
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

public class ImportedDataActivity extends AppCompatActivity implements DataAdapter.onClickItem {
    RecyclerView rvImportedData ;
    ArrayList<Data> dataList;
    DataAdapter dataAdapter;
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
        dataAdapter = new DataAdapter(ImportedDataActivity.this, dataList ,ImportedDataActivity.this);
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


    @Override
    public void onItemOnClick(Data data) {
        clickData = data;

        try { FileInputStream inputStream = new FileInputStream(clickData.getPath());

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        // Xử lý khi bắt đầu đọc file
                        break;

                    case XmlPullParser.START_TAG:
                        // Xử lý thẻ bắt đầu
                        System.out.println("Start tag: " + parser.getName());
                        break;

                    case XmlPullParser.END_TAG:
                        // Xử lý thẻ kết thúc
                        System.out.println("End tag: " + parser.getName());
                        break;

                    case XmlPullParser.TEXT:
                        // Xử lý nội dung của thẻ
                        String text = parser.getText();
                        if (text.trim().length() > 0) {
                            System.out.println("Text: " + text);
                        }
                        break;

                    default:
                        break;
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }


//        try {
//            FileInputStream inputStream = new FileInputStream(clickData.getPath());
//
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//            parser.setInput(inputStream, null);
//
//            while (parser.next() != XmlPullParser.END_DOCUMENT) {
//                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("All_in_One_GEN007")) {
//                    contentData = parser.nextText();
//
//                }
//                if (parser.getEventType() == XmlPullParser.TEXT) {
//                    // xử lý nội dung ở đây
//                }
//            }
//            System.out.println(contentData + "123123");
//            parser.setInput(null);
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            FileInputStream inputStream = new FileInputStream(new File(data.getPath()));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        });
//        File copiedFile = new File(clickData.getPath());
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(copiedFile));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line + "tho12312323");
//            }
//            reader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
}
