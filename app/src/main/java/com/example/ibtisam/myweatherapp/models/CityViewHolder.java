package com.example.ibtisam.myweatherapp.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.ibtisam.myweatherapp.R;

public class CityViewHolder extends RecyclerView.ViewHolder {
    public TextView itemCity;
    public TextView itemDate;
    public TextView itemTemperature;
    public TextView itemDescription;
    public TextView itemWind;
    public TextView itemPressure;
    public TextView itemHumidity;
    public TextView itemSunrise;
    public TextView itemSunset;
    public TextView itemIcon;
    public View lineView;

    public CityViewHolder(View view) {
        super(view);
        this.itemCity = (TextView) view.findViewById(R.id.itemCity);
        this.itemDate = (TextView) view.findViewById(R.id.itemDate);
        this.itemTemperature = (TextView) view.findViewById(R.id.itemTemperature);
        this.itemDescription = (TextView) view.findViewById(R.id.itemDescription);
        this.itemWind = (TextView) view.findViewById(R.id.itemWind);
        this.itemPressure = (TextView) view.findViewById(R.id.itemPressure);
        this.itemHumidity = (TextView) view.findViewById(R.id.itemHumidity);
        this.itemSunrise = (TextView) view.findViewById(R.id.itemSunrise);
        this.itemSunset = (TextView) view.findViewById(R.id.itemSunset);
        this.itemIcon = (TextView) view.findViewById(R.id.itemIcon);
        this.lineView = view.findViewById(R.id.lineView);
    }
}