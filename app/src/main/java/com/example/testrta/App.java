package com.example.testrta;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DbHelper db;
        db = new DbHelper(this);
        db.createTableImportedData();

    }
}
