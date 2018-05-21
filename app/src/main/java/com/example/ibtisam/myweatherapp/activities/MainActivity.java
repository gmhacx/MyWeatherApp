package com.example.ibtisam.myweatherapp.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ibtisam.myweatherapp.adapters.CityRecyclerAdapter;
import com.example.ibtisam.myweatherapp.events.CityEventModel;
import com.example.ibtisam.myweatherapp.models.City;
import com.example.ibtisam.myweatherapp.R;
import com.example.ibtisam.myweatherapp.sync.InitService;
import com.example.ibtisam.myweatherapp.utils.NetworkAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    protected static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;

    ProgressDialog progressDialog;
    private CityRecyclerAdapter adapter;
    private List<City> allCities;
    private TinyBus bus;

    private static Map<String, Integer> speedUnits = new HashMap<>(3);
    private static Map<String, Integer> pressUnits = new HashMap<>(3);
    private static boolean mappingsInitialised = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            handleResult(bundle);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
        setContentView(R.layout.activity_main);

        Intent intentInitService = new Intent(this, InitService.class);
        startService(intentInitService);

        progressDialog = new ProgressDialog(MainActivity.this);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        // Specify a linear layout manager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // Specify an adapter.
        adapter = new CityRecyclerAdapter(this, allCities);
        mRecyclerView.setAdapter(adapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add_city);
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsActivityIntent = new Intent(MainActivity.this, AddCityActivity.class);
                    startActivity(detailsActivityIntent);
                    Toast.makeText(MainActivity.this, "Type city name", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
        bus = TinyBus.from(this.getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        registerReceiver(receiver, new IntentFilter(InitService.NOTIFICATION));
        allCities = City.getCitiesInOrder();
        adapter.updateList(allCities);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: called");
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStart: called");
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onCityEventModel (CityEventModel event) {
        Log.d(TAG, "onCityEventModel: ");
        allCities = City.getCitiesInOrder();
        adapter.updateList(allCities);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            if (NetworkAccess.isNetworkAvailable(this)) {
            } else {
                Toast.makeText(this, "Connection not available.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_about) {
//            aboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleResult(Bundle bundle) {
        if (bundle != null) {
            int resultCode = bundle.getInt(InitService.RESULT);
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "handleResult: RESULT OK");
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }

            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "handleResult: RESULT CANCELED");
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
            }
        }
    }

    public static void initMappings() {
        if (mappingsInitialised)
            return;
        mappingsInitialised = true;
        speedUnits.put("m/s", R.string.speed_unit_mps);
        speedUnits.put("kph", R.string.speed_unit_kph);
        speedUnits.put("mph", R.string.speed_unit_mph);
        speedUnits.put("kn", R.string.speed_unit_kn);

        pressUnits.put("hPa", R.string.pressure_unit_hpa);
        pressUnits.put("kPa", R.string.pressure_unit_kpa);
        pressUnits.put("mm Hg", R.string.pressure_unit_mmhg);
    }

    private String localize(SharedPreferences sp, String preferenceKey, String defaultValueKey) {
        return localize(sp, this, preferenceKey, defaultValueKey);
    }

    public static String localize(SharedPreferences sp, Context context, String preferenceKey, String defaultValueKey) {
        String preferenceValue = sp.getString(preferenceKey, defaultValueKey);
        String result = preferenceValue;
        if ("speedUnit".equals(preferenceKey)) {
            if (speedUnits.containsKey(preferenceValue)) {
                result = context.getString(speedUnits.get(preferenceValue));
            }
        } else if ("pressureUnit".equals(preferenceKey)) {
            if (pressUnits.containsKey(preferenceValue)) {
                result = context.getString(pressUnits.get(preferenceValue));
            }
        }
        return result;
    }
}
