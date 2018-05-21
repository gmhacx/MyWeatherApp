package com.example.ibtisam.myweatherapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ibtisam.myweatherapp.R;
import com.example.ibtisam.myweatherapp.events.CityEventModel;
import com.example.ibtisam.myweatherapp.models.City;
import com.example.ibtisam.myweatherapp.sync.InitService;
import com.example.ibtisam.myweatherapp.sync.SyncStatus;

import java.util.Calendar;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 3/28/2018.
 */

public class AddCityActivity extends AppCompatActivity {
    private String TAG = "AddCityActivity";
    private EditText etNameAddCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        etNameAddCity = (EditText) findViewById(R.id.etNameAddCity);
        Button bSave = (Button) findViewById(R.id.bSave);
        Button bCancel = (Button) findViewById(R.id.bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = etNameAddCity.getText().toString();
                if (isValid(cityName)) {
                    City newCity = new City();
                    newCity.setCity(cityName);
                    newCity.setSyncStatus(SyncStatus.SYNC_STATUS_CITY_ADD_NOT_SYNCED);
                    newCity.setDate(Calendar.getInstance().getTime());
                    newCity.save();
                    finish();
                    TinyBus.from(getApplicationContext()).post(new CityEventModel());
                    Intent intentInitService = new Intent(AddCityActivity.this, InitService.class);
                    startService(intentInitService);
                    Toast.makeText(AddCityActivity.this, "Added City: " + cityName, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValid(String name) {
        etNameAddCity.setError(null);
        if (name.equals("") || name.length() < 3) {
            etNameAddCity.setError("Invalid Name!");
            return false;
        }
        return true;
    }

}
