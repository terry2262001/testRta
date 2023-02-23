package com.example.testrta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import org.xmlpull.v1.XmlPullParserFactory;

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
    Button btImport, btGoto;


    List<File> selectedFiles = new ArrayList<File>();
    File[] files;
    DbHelper dbHelper;

    ConstraintLayout wrapContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvData = findViewById(R.id.rvData);
        btImport = findViewById(R.id.btImport);
        btGoto = findViewById(R.id.btGoto);

        wrapContent = findViewById(R.id.wrapContent);

        dataList = new ArrayList<>();
        dbHelper = new DbHelper(MainActivity.this);


        rvData.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
        rvData.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        dataAdapter = new DataAdapter(MainActivity.this, dataList, MainActivity.this);
        rvData.setAdapter(dataAdapter);
        btGoto.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ImportedDataActivity.class));
        });


        btImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File fis = null;
                for (File file : selectedFiles) {

                    fis = new File(file.getPath());
                    FindInstanceIdTask task = new FindInstanceIdTask(MainActivity.this, fis, new FindInstanceIdTask.OnFindInstanceIdListener() {
                        @Override
                        public void onInstanceIdFound(String instanceId) {
                            // Xử lý khi tìm thấy instanceID
                            File officialDir = new File(getFilesDir() + "/official-data");
                            officialDir.mkdirs();

                            try {

                                File newFile = new File(officialDir, instanceId + ".xml");
                                if (newFile.exists()) {
                                    newFile = new File(officialDir, instanceId + "_" + System.currentTimeMillis() + ".xml");
                                }
                                Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                ProgressDialog pd = new ProgressDialog(MainActivity.this);
                                if (dbHelper.insertImportedData(new Data(instanceId, String.valueOf(newFile.toPath()), file.getName())) > 0) {

                                    pd.setMessage("Insert successful data to the database ...");
                                    pd.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            startActivity(new Intent(MainActivity.this, ImportedDataActivity.class));

                                        }
                                    }, 2000);


                                } else {
                                    pd.setMessage("Can't insert to database ...");
                                    pd.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                        }
                                    }, 2000);


                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onInstanceIdNotFound() {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Error").setMessage("Can not import file ! ").setIcon(R.drawable.ic_error);


                            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    });
                    task.execute();


                }


// Bắt đầu thực hiện tìm kiếm


            }
        });


        try {
            reachData();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void reachData() throws IOException {
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



    @Override
    public void onItemOnClick(Data data) {
        clickData = data;

        if (!clickData.isSelected()) {
            selectedFiles.add(new File(clickData.getPath()));
            btGoto.setVisibility(View.GONE);
            btImport.setVisibility(View.VISIBLE);


        } else {
            selectedFiles.remove(new File(clickData.getPath()));
            if (selectedFiles.size() > 0) {
                btImport.setVisibility(View.VISIBLE);
                btGoto.setVisibility(View.GONE);
            } else {
                btGoto.setVisibility(View.VISIBLE);
                btImport.setVisibility(View.GONE);

            }

        }


    }

    public static class FindInstanceIdTask extends AsyncTask<Void, Integer, String> {

        private Context mContext;
        private File mXmlFile;
        private OnFindInstanceIdListener mListener;
        private ProgressDialog mProgressDialog;

        public FindInstanceIdTask(Context context, File xmlFile, OnFindInstanceIdListener listener) {
            mContext = context;
            mXmlFile = xmlFile;
            mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            // Hiển thị dialog tiến trình trước khi thực hiện tìm kiếm
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("Processing File ...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String instanceId = null;
            try {

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser parser = factory.newPullParser();


                FileInputStream inputStream = new FileInputStream(mXmlFile);
                parser.setInput(inputStream, null);


                while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (isCancelled()) {

                        try {
                            Thread.sleep(2000); // Delay 2 giây (1000ms)
                            break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("instanceID")) {

                        instanceId = parser.nextText();

                        if (instanceId != null) {
                            try {
                                Thread.sleep(1000); // Delay 1 giây (1000ms)
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }

                    }


                    int progress = (int) ((float) inputStream.getChannel().position() / (float) mXmlFile.length() * 100);
                    publishProgress(progress);
                    parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return instanceId;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String instanceId) {
            // Ẩn dialog tiến trình
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            }, 1000);

            if (instanceId == null) {
                // Không tìm thấy thẻ instanceID
                mListener.onInstanceIdNotFound();
            } else {
                // Tìm thấy thẻ instanceID
                mListener.onInstanceIdFound(instanceId);
            }
        }

        @Override
        protected void onCancelled() {
            // Huỷ dialog tiến trình nếu tác vụ bị huỷ


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            }, 2000);
        }

        public interface OnFindInstanceIdListener {
            void onInstanceIdFound(String instanceId);

            void onInstanceIdNotFound();
        }
    }

}








