//package com.example.ibtisam.myweatherapp.sync;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.ibtisam.myweatherapp.models.City;
//import com.example.ibtisam.myweatherapp.utils.NetworkAccess;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import de.halfbit.tinybus.TinyBus;
//
//public class DataSenderAsync {
//    public static final String TAG = "DataSenderAsync";
////    private static final String TAG = "AppInitializationTest";
//
//    private static DataSenderAsync instance = null;
//    private static int currentState = 1;
//    private static final int IDLE = 1;
//    private static final int PENDING = 2;
//    private static boolean firstThreadIsRunning = false;
//    private Context mContext;
//    private long totalSize = 0;
//    private static RequestQueue queue;
//    private final int MY_TIMEOUT_MS = 30000;
//    private final int MY_MAX_RETRIES = 0;
//
//
//    protected DataSenderAsync(Context context) {
//        Log.d(TAG, "DataSenderAsync: ==========================================================================================================================");
//        mContext = context;
//        queue = Volley.newRequestQueue(mContext);
//        firstThreadIsRunning = false;
//    }
//
//    public static DataSenderAsync getInstance(Context context) {
//        final Context appContext = context.getApplicationContext();
//        if (instance == null) {
////            synchronized ((Object) firstThreadIsRunning){
//            if (!firstThreadIsRunning) {
//                firstThreadIsRunning = true;
//                instance = new DataSenderAsync(appContext);
//            }
////            }
//        }
//        return instance;
//    }
//
//    public void run() {
//        Log.d(TAG, "run: ");
//        if (currentState == IDLE) {
//            currentState = PENDING;
//            Log.d(TAG, "run: InsideRUNING" + this.toString());
//            queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<DataSenderAsync>() {
//                @Override
//                public void onRequestFinished(Request request) {
//                    currentState = IDLE;
//                    Log.d(TAG, "onRequestFinished: EVERYTHING COMPLETED");
//                }
//            });
//            new AsyncTask<Object, Void, Void>() {
//                @Override
//                protected void onPreExecute() {
//                    super.onPreExecute();
//                    Log.d(TAG, "DataSenderAsync onPreExecute: ");
//                }
//
//                @Override
//                protected Void doInBackground(Object... params) {
//                    try {
//                        if (NetworkAccess.isNetworkAvailable(mContext)) {
//                                Log.d(TAG, "Syncing");
//                                addContactsToServer();
//                        } else {
//                            Log.d(TAG, "SyncNoInternet");
//                        }
//                    } catch (Exception e) {
//                        Log.d(TAG, "SyncingException: " + e.getMessage());
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void onProgressUpdate(Void... values) {
//                    super.onProgressUpdate(values);
//                }
//
//                //this method is executed when doInBackground function finishes
//                @Override
//                protected void onPostExecute(Void result) {
//                }
//            }.execute();
//        } else {
//            Log.d(TAG, "run: NotRunning");
//        }
//    }
//
//    private void addContactsToServer() {
//        List<City> contactsList = null;
//        if (City.count(City.class) > 0) {
//            contactsList = City.find(City.class, "sync_status = ? ", SyncStatus.SYNC_STATUS_CITY_ADD_NOT_SYNCED);
//            Log.d(TAG, "addContactsToServer: count : " + contactsList.size());
//            for (City oneContact : contactsList) {
//                addContactToServerSync(oneContact);
//            }
//        }
//    }
//
//    private void addContactToServerSync(final City city) {
//        currentState = PENDING;
//        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_CONTACT, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "onResponse() addContact: response = [" + response + "]");
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    JSONObject responseObject = jObj.getJSONObject("response");
//                    city.setServerId(responseObject.getString("id"));
//                    Log.d(TAG, "onResponse: LeadServerID : " + responseObject.getString("id"));
//                    city.setSyncStatus(SyncStatus.SYNC_STATUS_CITY_ADD_SYNCED);
//                    city.save();
//
////                    int responseCode = jObj.getInt("responseCode");
////                    if (responseCode ==  200) {
////                        JSONObject responseObject = jObj.getJSONObject("response");
////                        city.setServerId(responseObject.getString("id"));
////                        Log.d(TAG, "onResponse: ServerID : " +responseObject.getString("id"));
////                        city.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
////                        city.save();
////                    } else if (responseCode ==  409) {
////                        JSONObject responseObject = jObj.getJSONObject("response");
////                        city.setServerId(responseObject.getString("id"));
////                        city.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
////                        city.save();
////                        Log.d(TAG, "onResponse: Lead already Exists");
////                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "onErrorResponse: CouldNotSyncAddContact");
//                try {
//                    if (error != null) {
//                        if (error.networkResponse != null) {
//                            Log.d(TAG, "onErrorResponse: error.networkResponse: " + error.networkResponse);
//                            if (error.networkResponse.statusCode == 409) {
//                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
//                                int responseCode = jObj.getInt("responseCode");
//                                if (responseCode == 409) {
//                                    Log.d(TAG, "onErrorResponse: responseCode == 409");
//                                    JSONObject responseObject = jObj.getJSONObject("response");
//                                    city.setServerId(responseObject.getString("id"));
//                                    city.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED);
//                                    city.save();
//                                }
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("name", "" + city.getContactName());
//
//                if (city.getContactEmail() != null) {
//                    params.put("email", "" + city.getContactEmail());
//                }
//                if (city.getContactAddress() != null) {
//                    params.put("address", "" + city.getContactAddress());
//                }
//                if (city.getDynamic() != null) {
//                    params.put("dynamic_values", "" + city.getDynamic());
//                }
//                params.put("phone", "" + city.getPhoneOne());
//                Log.d(TAG, "getParams: addContactToServerSync " + params);
//                return params;
//            }
//        };
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                MY_TIMEOUT_MS,
//                MY_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//    }
//}