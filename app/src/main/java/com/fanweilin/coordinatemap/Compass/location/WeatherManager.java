package com.fanweilin.coordinatemap.Compass.location;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;


import com.fanweilin.coordinatemap.Compass.location.model.LocationData;
import com.fanweilin.coordinatemap.Compass.location.model.Sunshine;
import com.fanweilin.coordinatemap.Compass.utils.DLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherManager {


    private static final String TAG = "WeatherManager";
    private Context mContext;
    static String forecastJsonStr = null;

    public WeatherManager(Context context) {
        this.mContext = context;
    }

    public static Sunshine getSunTimeFromJson(String forecastJsonStr) throws JSONException {
        DLog.d(TAG, "getSunTimeFromJson() called with: forecastJsonStr = [" + forecastJsonStr + "]");

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONObject sysJson = forecastJson.getJSONObject("sys");
        long sunrise = sysJson.getLong("sunrise");
        long sunset = sysJson.getLong("sunset");
        return new Sunshine(sunset * 1000, sunrise * 1000);
    }

 private static String  getWeatherForecastData(int adcode){
     HttpURLConnection urlConnection = null;
     BufferedReader reader = null;
     String forecastJsonStr = null;
     //Wil contain the raw JSON response as a string
     String format = "json";
     String units = "metric";
     int numDays = 14;
     String key = "45135a10a5ceba75903ccd3866a958b1";
     try {
         StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                 .permitAll().build();
         StrictMode.setThreadPolicy(policy);

         // Construct the URL for the OpenWeatherMap query
         // Possible parameters are avaiable at OWM's forecast API page, at
         // http://openweathermap.org/API#forecast
         final String forecastBaseUrl = "http://restapi.amap.com/v3/weather/weatherInfo?";
         final String city = "city";
         final String extensions = "extensions";
         final String apiKey = "key";

         Uri builtUri = Uri.parse(forecastBaseUrl).buildUpon()
                 .appendQueryParameter(apiKey, key)
                 .appendQueryParameter(city, String.valueOf(adcode))
                 .appendQueryParameter(extensions ,"base")
                 .build();

         URL url = new URL(builtUri.toString());

         // Create the request to OpenWeatherMap, and open the connection
         urlConnection = (HttpURLConnection) url.openConnection();
         urlConnection.setRequestMethod("GET");
         urlConnection.connect();

         //Read the input stream into a String
         InputStream inputStream = urlConnection.getInputStream();
         StringBuffer buffer = new StringBuffer();
         if (inputStream == null) {
             //Nothing to do
             return null;
         }
         reader = new BufferedReader(new InputStreamReader(inputStream));

         String line;
         while ((line = reader.readLine()) != null) {
             //Since it's JSON, adding a newline isn't necessary (it won't affect
             //parsing) But it does make debugging a lot easier if you print out the
             //completed buffer for debugging
             buffer.append(line + "\n");
         }

         if (buffer.length() == 0) {
             //Stream was empty. No point in parsing
             return null;
         }
         forecastJsonStr = buffer.toString();

     } catch (IOException e) {
         Log.e("MainFragment", "Error: ", e);
         //If the code didn't successfully get the weather data, there's no point in attempting to parse it
         forecastJsonStr = null;
     } finally {
         if (urlConnection != null)
             urlConnection.disconnect();

         if (reader != null) {
             try {
                 reader.close();
             } catch (final IOException e) {
                 Log.e("MainFragment", "Error closing stream", e);
             }
         }
     }

     return forecastJsonStr;
 }
    @Nullable
    public static LocationData getWeatherData( int adcode) {

        try {
            String weatherForecast = getWeatherForecastData(adcode);
            return getWeatherDataFromJson(weatherForecast);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private static LocationData getWeatherDataFromJson(String forecastJsonStr )

            throws JSONException {
        DLog.d(TAG, "getWeatherDataFromJson() called with: forecastJsonStr = [" + forecastJsonStr + "]");
        LocationData locationData=new LocationData();
        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray("lives");
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        locationData.setTemp( Integer.valueOf(weatherObject.getString("temperature")));
        locationData.setHumidity(Float.valueOf(weatherObject.getString("humidity" )));
        return locationData;
    }
    public Context getContext() {
        return mContext;
    }

}
