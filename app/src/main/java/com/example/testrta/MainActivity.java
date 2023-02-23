package com.example.testrta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testrta.Adapter.DataAdapter;
import com.example.testrta.Model.Data;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements DataAdapter.onClickItem {

    RecyclerView rvData;
    ArrayList<Data> dataList;
    DataAdapter dataAdapter;
    Data clickData;
    int IMPORT_REQUEST_CODE = 1;
    Button btImport;
    private ArrayList<String> selectedXmlFilesList;
    List<String> filenames = new ArrayList<String>();
    List<File> selectedFiles = new ArrayList<File>();
    File[] files;
    DbHelper dbHelper;

    TextView tvProgressbar ;
    ProgressBar  progressBar;
    ConstraintLayout wrapContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvData = findViewById(R.id.rvData);
        btImport = findViewById(R.id.btImport);

        progressBar = findViewById(R.id.progressBar);
        tvProgressbar = findViewById(R.id.tvProgressbar);
        wrapContent = findViewById(R.id.wrapContent);

        dataList = new ArrayList<>();
        dbHelper = new DbHelper(MainActivity.this);

        rvData.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
        rvData.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        dataAdapter = new DataAdapter(MainActivity.this, dataList, MainActivity.this);
        rvData.setAdapter(dataAdapter);

        btImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Move data...");
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        processSelectedXmlFiles();
                      //  startActivity(new Intent(MainActivity.this, ImportedDataActivity.class));

                        pd.dismiss();
                    }
                }, 1000);






            }
        });


        try {
            reachDat();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void reachDat() throws IOException {
        File folder = new File(getFilesDir(), "data");
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

    private void processSelectedXmlFiles() {
        File officialDir = new File(getFilesDir() + "/official-data");
        officialDir.mkdirs();
        for (File file : selectedFiles) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file.getPath());
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(fis, null);
                String instanceId = null;
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("instanceID")) {
                        instanceId = parser.nextText();
                        break;
                    }
                }
                fis.close();
                if (instanceId != null) {
                    File newFile = new File(officialDir, instanceId + ".xml");
                    if (newFile.exists()) {
                        newFile = new File(officialDir, instanceId + "_" + System.currentTimeMillis() + ".xml");
                    }
                    Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    ProgressDialog pd = new ProgressDialog(MainActivity.this);

                   if ( dbHelper.insertImportedData(new Data(instanceId,String.valueOf(newFile.toPath()),file.getName())) > 0){
                       pd.setMessage("Insert Success to database ...");
                       pd.show();
                       Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                                 startActivity(new Intent(MainActivity.this, ImportedDataActivity.class));
                               pd.dismiss();
                           }
                       }, 2000);

                    //   Toast.makeText(this, "Insert Success to database", Toast.LENGTH_SHORT).show();

                   }else {
                       pd.setMessage("Can't insert to database ...");
                       pd.show();
                       Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {

                               pd.dismiss();
                           }
                       }, 2000);
                      // Toast.makeText(this, "Can't insert to database", Toast.LENGTH_SHORT).show();
                   }




                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onItemOnClick(Data data) {
        clickData = data;
        if (!clickData.isSelected()) {
            selectedFiles.add(new File(clickData.getPath()));
        } else {
            selectedFiles.remove(new File(clickData.getPath()));
        }

        File inputFile = new File(getFilesDir(), "data/" + data.getName());

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(inputFile);
            NodeList instanceIdList = doc.getElementsByTagName("instanceID");
            if (instanceIdList != null) {
                for (int i = 0; i < instanceIdList.getLength(); i++) {
                    Node instanceIdNode = instanceIdList.item(i);
                    String instanceIdValue = instanceIdNode.getTextContent();

                    System.out.println("Instance ID: " + instanceIdValue);

                }
            } else {
                Toast.makeText(this, "Don't have InstanceID", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


//        try {
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String line;
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*"); // Hiển thị tất cả các loại tệp
//        startActivityForResult(intent,  PICK_FILE_REQUEST_CODE);


    }
}





