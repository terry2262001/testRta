package com.example.testrta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class MainActivity extends AppCompatActivity implements DataAdapter.onClickItem{

    RecyclerView rvData;
    ArrayList<Data> dataList;
    DataAdapter dataAdapter;
    Data clickData ;
    int IMPORT_REQUEST_CODE = 1;
    Button btImport ;
    private ArrayList<String> selectedXmlFilesList;
    private final String DATA_MAIN = "datamain";
    List<String> filenames = new ArrayList<String>();
    List<File> selectedFiles = new ArrayList<File>();
    File[] files;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvData = findViewById(R.id.rvData);
        btImport = findViewById(R.id.btImport);

        dataList = new ArrayList<>();

        rvData.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
        rvData.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        dataAdapter = new DataAdapter(MainActivity.this, dataList, MainActivity.this  );
        rvData.setAdapter(dataAdapter);

        btImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSelectedXmlFiles();
            }
        });





        try {
            reachData();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }





    public void reachData() throws IOException {
       // File directory = new File(Environment.getExternalStorageDirectory() + "/data");
        File folder = new File(getFilesDir(), "data");
            files = folder.listFiles();
        if (files != null) {
            for (File file : files) {

                if (file.isFile() && file.getName().endsWith(".xml")) {
                    dataList.add(new Data(file.getName(),folder.getPath()+"/"+file.getName()));
                    dataAdapter.notifyDataSetChanged();
                }
            }
        }







    }
    private void processSelectedXmlFiles() {
        Toast.makeText(this, "thád", Toast.LENGTH_SHORT).show();


        File officialDir = new File(getFilesDir() + "/datamain");
        officialDir.mkdirs();
        for (File file : selectedFiles) {
            InputStream fis = null;
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
                   Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println(file.toPath() + "tho123_old");
                    System.out.println(newFile.toPath() + "tho123_new");

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
//        for (File file : selectedFiles) {
//            InputStream fis = null;
//            try {
//                fis = new FileInputStream(file.getPath());
//
//                XmlPullParser parser = Xml.newPullParser();
//                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//                parser.setInput(fis, null);
//                String instanceId = null;
//                while (true) {
//                    if (!(parser.next() != XmlPullParser.END_DOCUMENT)) break;
//                    if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("instanceID")) {
//                        instanceId = parser.nextText();
//                        break;
//                    }
//                }
//                fis.close();
//                if (instanceId != null) {
//                    File newFile = new File(officialDir, instanceId + ".xml");
//                System.out.println(file.toPath() + "tho123_old");
//                System.out.println(newFile.toPath() + "tho123_new");
//         //           Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        }

//        for (File file : selectedFiles) {
//
//            InputStream fis = null;
//            try {
//                fis = new FileInputStream(file.getPath());
//                System.out.println(fis + "tho123123");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            XmlPullParser parser = Xml.newPullParser();
//            try {
//                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//            }
////            try {
////                parser.setInput(fis, null);
////            } catch (XmlPullParserException e) {
////                e.printStackTrace();
////            }
//            String instanceId = null;
//            while (true) {
//                try {
//                    if (!(parser.next() != XmlPullParser.END_DOCUMENT)) break;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (XmlPullParserException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("instanceID")) {
//                        instanceId = parser.nextText();
//                        break;
//                    }
//                } catch (XmlPullParserException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                fis.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (instanceId != null) {
//                File newFile = new File(officialDir, instanceId + ".xml");
//                System.out.println(file.toPath() + "tho123_old");
//                System.out.println(newFile.toPath() + "tho123_new");
//
//                try {
//                    Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                // Lưu thông tin vào cơ sở dữ liệu SQLite
//            }
//        }
    }



    @Override
    public void onItemOnClick(Data data) {
        clickData =data;
        if(!clickData.isSelected()){
           selectedFiles.add(new File(clickData.getPath()));
           // selectedFiles.add(new File(clickData.getName()));

        }else   {
          selectedFiles.remove(new File(clickData.getPath()));
           // selectedFiles.add(new File(clickData.getPath()));
        }

        File inputFile = new File(getFilesDir(),"data/"+data.getName());

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
            if(instanceIdList != null){
                for (int i = 0; i < instanceIdList.getLength(); i++) {
                    Node instanceIdNode = instanceIdList.item(i);
                    String instanceIdValue = instanceIdNode.getTextContent();

                    System.out.println("Instance ID: " + instanceIdValue);

                }
            }else {
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
//        intent.setType("*/*");
//        startActivityForResult(intent,  PICK_FILE_REQUEST_CODE);




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMPORT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Lấy URI của tệp đã chọn
            Uri fileUri = data.getData();
            System.out.println(fileUri + "tho123123");

            // Mở InputStream để đọc tệp
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);

                // Xử lý tệp và sao chép vào thư mục mới tại đây
                // ...

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}





