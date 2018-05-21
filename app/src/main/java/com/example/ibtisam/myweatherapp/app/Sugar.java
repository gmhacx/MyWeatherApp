package com.example.ibtisam.myweatherapp.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.orm.SugarApp;
import com.orm.SugarContext;

/**
 * Created by ibtisam on 5/17/2017.
 */

public class Sugar extends SugarApp {
    private static final String TAG = "SugarApplicationClass";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Sugar Class");
        super.onCreate();
        SugarContext.init(getApplicationContext());

        // create table if not exists
//        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
//        schemaGenerator.createDatabase(new SugarDb(this).getDB());

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory: ");
//        Toast.makeText(this, "Memory Low", Toast.LENGTH_SHORT).show();
        super.onLowMemory();
    }
}