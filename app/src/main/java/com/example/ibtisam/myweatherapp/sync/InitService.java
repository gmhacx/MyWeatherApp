package com.example.ibtisam.myweatherapp.sync;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ibtisam.myweatherapp.R;
import com.example.ibtisam.myweatherapp.app.Constants;
import com.example.ibtisam.myweatherapp.events.CityEventModel;
import com.example.ibtisam.myweatherapp.models.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.halfbit.tinybus.TinyBus;

public class InitService extends IntentService {
    public static final String TAG = "InitService";
    private int result = Activity.RESULT_CANCELED;
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.example.ibtisam.myweatherapp";

    private Context mContext;
    private static RequestQueue queue;
    AtomicInteger requestsCounter;

//    boolean initError = false;

    public InitService() {
        super("InitService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        queue = Volley.newRequestQueue(mContext);
        requestsCounter = new AtomicInteger(0);
        return super.onStartCommand(intent, flags, startId);
    }

    // called asynchronously be Android
    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        fetchCityData();

        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                Log.d(TAG, "onRequestFinished: " + requestsCounter.get());
                requestsCounter.decrementAndGet();
                if (requestsCounter.get() == 0) {
                    Log.d(TAG, "onRequestFinished: +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    publishResults(result);
                }
            }
        });

    }

    private void fetchCityData() {
        List<City> cityList = null;
        if (City.count(City.class) > 0) {
            cityList = City.find(City.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_CITY_ADD_NOT_SYNCED);
            Log.d(TAG, "fetchCityData: count : " + cityList.size());
            for (City oneCity : cityList) {
                fetchCityDataFunc(oneCity);
            }
        }
    }

    private void fetchCityDataFunc(final City city) {
        Log.d(TAG, "fetchCityDataFunc: Fetching City...");
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_CITY_TEMP_DATA;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("q", city.getCity())
                .appendQueryParameter("lang", "en")
                .appendQueryParameter("mode", "json")
                .appendQueryParameter("appid", Constants.API_KEY)
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() getCity: response = [" + response + "]");

                try {
                    JSONObject reader = new JSONObject(response);

                    final String code = reader.optString("cod");
                    if ("404".equals(code)) {
                        Log.d(TAG, "onResponse: 404");
                    }

                    final String idString = reader.getJSONArray("weather").getJSONObject(0).getString("id");

//                    if (City.getCityFromServerId(idString) == null) {

                        City newCity = city;

                        newCity.setServerId(idString);
                        String city = reader.getString("name");
                        String lastUpdatedAt = reader.getString("dt");
                        newCity.setLastUpdated(lastUpdatedAt);
                        String country = "";
                        JSONObject countryObj = reader.optJSONObject("sys");
                        if (countryObj != null) {
                            country = countryObj.getString("country");
                            newCity.setSunrise(countryObj.getString("sunrise"));
                            newCity.setSunset(countryObj.getString("sunset"));
                        }
                        newCity.setCity(city);
                        newCity.setCountry(country);

                        JSONObject main = reader.getJSONObject("main");

                        newCity.setTemperature(main.getString("temp"));
                        newCity.setDescription(reader.getJSONArray("weather").getJSONObject(0).getString("description"));
                        JSONObject windObj = reader.getJSONObject("wind");
                        newCity.setWind(windObj.getString("speed"));
                        if (windObj.has("deg")) {
                            newCity.setWindDirectionDegree(windObj.getDouble("deg"));
                        } else {
                            Log.e("parseTodayJson", "No wind direction available");
                            newCity.setWindDirectionDegree(null);
                        }
                        newCity.setPressure(main.getString("pressure"));
                        newCity.setHumidity(main.getString("humidity"));

                        JSONObject rainObj = reader.optJSONObject("rain");
                        String rain;
                        if (rainObj != null) {
                            rain = getRainString(rainObj);
                        } else {
                            JSONObject snowObj = reader.optJSONObject("snow");
                            if (snowObj != null) {
                                rain = getRainString(snowObj);
                            } else {
                                rain = "0";
                            }
                        }
                        newCity.setRain(rain);
                        newCity.setIcon(setWeatherIcon(Integer.parseInt(idString), Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                        newCity.save();
                    TinyBus.from(getApplicationContext()).post(new CityEventModel());
//                    }
                } catch (JSONException e) {
                    Log.e("JSONException Data", response);
                    e.printStackTrace();
                }

                result = Activity.RESULT_OK;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: fetchCityDataFunc");
            }
        });
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
        requestsCounter.incrementAndGet();
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }


    private String setWeatherIcon(int actualId, int hourOfDay) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = this.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = this.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = this.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = this.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    public static String getRainString(JSONObject rainObj) {
        String rain = "0";
        if (rainObj != null) {
            rain = rainObj.optString("3h", "fail");
            if ("fail".equals(rain)) {
                rain = rainObj.optString("1h", "0");
            }
        }
        return rain;
    }
}