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

import java.text.DecimalFormat;
import java.util.List;

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

        customViewHolder.itemCity.setText(cityItem.getCity() + ", " + cityItem.getCountry());
        customViewHolder.itemDate.setText(cityItem.getLastUpdated());
        customViewHolder.itemDescription.setText(cityItem.getDescription());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // Temperature
        float temperature = UnitConvertor.convertTemperature(Float.parseFloat(cityItem.getTemperature()), sp);
        if (sp.getBoolean("temperatureInteger", false)) {
            temperature = Math.round(temperature);
        }
            customViewHolder.itemTemperature.setText(new DecimalFormat("0.0").format(temperature) + " " + sp.getString("unit", "°C"));

//        customViewHolder.itemTemperature.setText(cityItem.getTemperature());


        customViewHolder.itemWind.setText(context.getString(R.string.wind) + ": " + cityItem.getWind() + " m/s");
        customViewHolder.itemPressure.setText(context.getString(R.string.pressure) + ": " + cityItem.getPressure() + " hPa");
        customViewHolder.itemHumidity.setText(context.getString(R.string.humidity) + ": " + cityItem.getHumidity() + " %");
        customViewHolder.itemHumidity.setText(context.getString(R.string.humidity) + ": " + cityItem.getHumidity() + " %");
        customViewHolder.itemSunrise.setText(context.getString(R.string.sunrise) + ": " + cityItem.getSunrise() + "");
        customViewHolder.itemSunset.setText(context.getString(R.string.sunset) + ": " + cityItem.getSunset() + "");

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
