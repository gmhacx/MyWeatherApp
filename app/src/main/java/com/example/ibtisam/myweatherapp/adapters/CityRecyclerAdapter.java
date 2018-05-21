package com.example.ibtisam.myweatherapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ibtisam.myweatherapp.R;
import com.example.ibtisam.myweatherapp.activities.MainActivity;
import com.example.ibtisam.myweatherapp.models.City;
import com.example.ibtisam.myweatherapp.models.CityViewHolder;
import com.example.ibtisam.myweatherapp.utils.UnitConvertor;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityViewHolder> {
    private List<City> itemList;
    private Context context;

    public CityRecyclerAdapter(Context context, List<City> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_city, null);

        CityViewHolder viewHolder = new CityViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CityViewHolder customViewHolder, int i) {
        final City cityItem = itemList.get(i);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        customViewHolder.itemCity.setText(cityItem.getCity() + ", " + cityItem.getCountry());

        TimeZone tz = TimeZone.getDefault();
        String defaultDateFormat = context.getResources().getStringArray(R.array.dateFormatsValues)[0];
        String dateFormat = sp.getString("dateFormat", defaultDateFormat);
        if ("custom".equals(dateFormat)) {
            dateFormat = sp.getString("dateFormatCustom", defaultDateFormat);
        }
        String dateString;
        try {
            SimpleDateFormat resultFormat = new SimpleDateFormat(dateFormat);
            resultFormat.setTimeZone(tz);
            dateString = resultFormat.format(cityItem.getDate());
        } catch (IllegalArgumentException e) {
            dateString = context.getResources().getString(R.string.error_dateFormat);
        }
        if (cityItem.getWind() != null) {
            customViewHolder.itemDate.setText(dateString);
        } else {
            customViewHolder.itemDate.setText(dateString);
        }

        customViewHolder.itemDescription.setText(cityItem.getDescription());

        if (cityItem.getTemperature() != null) {
            float temperature = UnitConvertor.convertTemperature(Float.parseFloat(cityItem.getTemperature()), sp);
            if (sp.getBoolean("temperatureInteger", false)) {
                temperature = Math.round(temperature);
            }
            customViewHolder.itemTemperature.setText(new DecimalFormat("0.0").format(temperature) + " " + sp.getString("unit", "°C"));
        } else {
            customViewHolder.itemTemperature.setText("not synced");
        }

        if (cityItem.getWind() != null) {
            customViewHolder.itemWind.setText(context.getString(R.string.wind) + ": " + cityItem.getWind() + " m/s");
        } else {
            customViewHolder.itemWind.setText(context.getString(R.string.wind) + ": " + 0 + " m/s");
        }


        if (cityItem.getWind() != null) {
            // Pressure
            double pressure = UnitConvertor.convertPressure((float) Double.parseDouble(cityItem.getPressure()), sp);
            customViewHolder.itemPressure.setText(context.getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format(pressure) + " " +
                    MainActivity.localize(sp, context, "pressureUnit", "hPa"));
        } else {
            customViewHolder.itemPressure.setText(context.getString(R.string.pressure) + ": " + 0 + " hPa");
        }

        if (cityItem.getWind() != null) {
            customViewHolder.itemHumidity.setText(context.getString(R.string.humidity) + ": " + cityItem.getHumidity() + " %");
        } else {
            customViewHolder.itemHumidity.setText(context.getString(R.string.humidity) + ": " + 0 + " %");
        }

        if (cityItem.getWind() != null) {
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context.getApplicationContext());
            customViewHolder.itemSunrise.setText(context.getString(R.string.sunrise) + ": " + timeFormat.format(cityItem.getSunrise()));
//            customViewHolder.itemSunrise.setText(context.getString(R.string.sunrise) + ": " + cityItem.getSunrise() + "");
        } else {
            customViewHolder.itemSunrise.setText(context.getString(R.string.sunrise) + ": " + 0 + "");
        }

        if (cityItem.getWind() != null) {
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context.getApplicationContext());
            customViewHolder.itemSunset.setText(context.getString(R.string.sunset) + ": " + timeFormat.format(cityItem.getSunset()));
//            customViewHolder.itemSunset.setText(context.getString(R.string.sunset) + ": " + cityItem.getSunset() + "");
        } else {
            customViewHolder.itemSunset.setText(context.getString(R.string.sunset) + ": " + 0 + "");
        }

        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
        customViewHolder.itemIcon.setTypeface(weatherFont);
        customViewHolder.itemIcon.setText(cityItem.getIcon());

//        customViewHolder.rl_city.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, CityDetailActivity.class);
//                intent.putExtra("city_name", cityItem.getDescription());
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    public void updateList(List<City> newlist) {
        itemList = newlist;
        this.notifyDataSetChanged();
    }
}
