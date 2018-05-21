package com.example.ibtisam.myweatherapp.events;

import de.halfbit.tinybus.Produce;

/**
 * Created by ibtisam on 2/5/2018.
 */

public class CityEventModel {

    public CityEventModel() {
    }

    @Produce
    public CityEventModel geLastCallReceivedEvent(){
        return this;
    }

}
