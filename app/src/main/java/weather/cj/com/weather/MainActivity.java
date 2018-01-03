package weather.cj.com.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private LocationManager locationManager;
    private String provider;
    private int MY_PERSMISSION_REQUEST_LOCATION;
    Typeface weatherFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons.ttf");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        int checkLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION );
        System.out.println("Internet permission:: "+checkLocationPermission);
        //onLocationChanged();
        if(checkLocationPermission == PackageManager.PERMISSION_GRANTED){
            //Location location = locationManager.getLastKnownLocation(provider);
            Location location = getLocation();
            System.out.println(location);
            if(location == null){

            }else{
                onLocationChanged(location);
            }
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERSMISSION_REQUEST_LOCATION);
          if(MY_PERSMISSION_REQUEST_LOCATION == PackageManager.PERMISSION_GRANTED){
              Location location = getLocation();
              System.out.println(location);
              if(location == null){

              }else{
                  onLocationChanged(location);
              }
          }
        }

    }

    private Location getLocation(){
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION );
        for(String provider: providers){
            Location l = locationManager.getLastKnownLocation(provider);
            if(l == null){
                continue;
            }
            if(location == null || l.getAccuracy() < location.getAccuracy()){
                location = l;
            }
        }
        return location;
    }

    @Override
    public void onLocationChanged (Location location){
        float latitude = (float) (location.getLatitude());
        float longitude = (float) (location.getLongitude());
        String lat = Float.toString(latitude);
        String lon = Float.toString(longitude);
        System.out.println("latitude:::"+lat);
        System.out.println("longitude:::"+lon);
        final TextView cityInfo = (TextView) findViewById(R.id.cityInfo);
        final TextView date = (TextView) findViewById(R.id.dateInfo);
        final TextView weatherIconMain = (TextView) findViewById(R.id.mainIconDisplay);
        final TextView desc = (TextView) findViewById(R.id.description);
        final TextView pressureText = (TextView) findViewById(R.id.pressure);
        final TextView humidityText = (TextView) findViewById(R.id.humidity);
        final TextView tempText = (TextView) findViewById(R.id.tempDisplay);
       final TextView dayTwoMin = (TextView) findViewById(R.id.dayTwoMin);
        final TextView dayTwoMax = (TextView) findViewById(R.id.dayTwoMax);
        final TextView dayTwoIcon = (TextView) findViewById(R.id.dayTwoIcon);
        final TextView dayTwoDate = (TextView) findViewById(R.id.dayTwoDate);
        final TextView dayThreeMin = (TextView) findViewById(R.id.dayThreeMin);
        final TextView dayThreeMax = (TextView) findViewById(R.id.dayThreeMax);
        final TextView dayThreeIcon = (TextView) findViewById(R.id.dayThreeIcon);
        final TextView dayThreeDate = (TextView) findViewById(R.id.dayThreeDate);
        final TextView dayFourMin = (TextView) findViewById(R.id.dayFourMin);
        final TextView dayFourMax = (TextView) findViewById(R.id.dayFourMax);
        final TextView dayFourIcon = (TextView) findViewById(R.id.dayFourIcon);
        final TextView dayFourDate = (TextView) findViewById(R.id.dayFourDate);
        final TextView dayFiveMin = (TextView) findViewById(R.id.dayFiveMin);
        final TextView dayFiveMax = (TextView) findViewById(R.id.dayFiveMax);
        final TextView dayFiveIcon = (TextView) findViewById(R.id.dayFiveIcon);
        final TextView dayFiveDate = (TextView) findViewById(R.id.dayFiveDate);
        weatherIconMain.setTypeface(weatherFont);
        //CallAPI.getData(latitude, longitude);
        callAPIinBG aysncTask = new callAPIinBG(new AsyncResponse() {
            @Override
            public void updateInUI(String city, String dateInfo, String mainIcon, int id, String description, String pressure, String humidity, int temp, JSONObject dayTwoInfo, JSONObject dayThreeInfo, JSONObject dayFourInfo, JSONObject dayFiveInfo) {

                try {
                    cityInfo.setText(city);
                    date.setText(dateInfo);
                    String mainImage = getWeatherIcon(id, mainIcon);
                    String pressure_tem = "Pressure: "+pressure+" hpa";
                    String humidity_tem = "Humidity: "+humidity+" %";
                    weatherIconMain.setText(mainImage);
                    desc.setText(description);
                    System.out.println(description);
                    pressureText.setText(pressure_tem);
                    humidityText.setText(humidity_tem);
                    tempText.setText(Integer.toString(temp)+(char) 0x00B0+"C");
                    dayTwoMin.setText(Integer.toString(dayTwoInfo.getInt("temp_min"))+(char) 0x00B0+"C Lo");
                    dayTwoMax.setText(Integer.toString(dayTwoInfo.getInt("temp_max"))+(char) 0x00B0+"C Hi");
                    dayTwoIcon.setText(dayTwoInfo.getString("description"));
                    dayTwoDate.setText(dayTwoInfo.getString("date"));
                    dayThreeMin.setText(Integer.toString(dayThreeInfo.getInt("temp_min"))+(char) 0x00B0+"C Lo");
                    dayThreeMax.setText(Integer.toString(dayThreeInfo.getInt("temp_max"))+(char) 0x00B0+"C Hi");
                    dayThreeIcon.setText(dayThreeInfo.getString("description"));
                    dayThreeDate.setText(dayThreeInfo.getString("date"));
                    dayFourMin.setText(dayFourInfo.getString("temp_min")+(char) 0x00B0+"C Lo");
                    dayFourMax.setText(dayFourInfo.getString("temp_max")+(char) 0x00B0+"C Hi");
                    dayFourIcon.setText(dayFourInfo.getString("description"));
                    dayFourDate.setText(dayFourInfo.getString("date"));
                    dayFiveMin.setText(dayFiveInfo.getString("temp_min")+(char) 0x00B0+"C Lo");
                    dayFiveMax.setText(dayFiveInfo.getString("temp_max")+(char) 0x00B0+"C Hi");
                    dayFiveIcon.setText(dayFiveInfo.getString("description"));
                    dayFiveDate.setText(dayFiveInfo.getString("date"));

                }catch (JSONException je){
                    Log.e("Error", "JSON Exception");
                    System.out.println(je);
                }

            }
        });
        aysncTask.execute(lat, lon);

    }

    @Override
    protected void onResume() {
        super.onResume();
       // int checkLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION );
       // locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
    @Override
    public void onStatusChanged(String provider, int Status, Bundle extras){

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onProviderDisabled(String provider){

    }
    public static JSONObject getData (String latitude, String longitude){

        String base_url = "https://api.openweathermap.org/data/2.5/forecast/daily?";
        String data = "lat=" + latitude + "&lon=" + longitude;
        String apikey = "&units=metric&appid="+"d922845dcd4513501382bea63c99bc40&cnt=5";

        try {
            Log.d("Debug", "Inside Get data()");
            URL url = new URL(base_url + data + apikey);
            System.out.println(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.addRequestProperty("x-api-key", apikey);

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject output = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            System.out.println(output);
            return output;
        } catch (MalformedURLException me) {
            System.out.println("MalformedURLException" + me);
            return null;
        } catch (IOException ie) {
            System.out.println("IOException" + ie);
            return null;
        } catch (JSONException je) {
            System.out.println("JSON Exception" + je);
            return null;
        } catch (Exception e) {
            System.out.println("General Error" + e);
            return null;
        }

    }


    public interface AsyncResponse {
        void updateInUI(String city, String dateInfo, String mainIcon, int id, String description, String pressure, String humidity, int temp,JSONObject dayTwoInfo, JSONObject dayThreeInfo, JSONObject dayFourInfo, JSONObject dayFiveInfo);

    }

    public class callAPIinBG extends AsyncTask<String, String, JSONObject> {


        public AsyncResponse delegate = null;

        public callAPIinBG(AsyncResponse asyncResponse){
            delegate = asyncResponse;
        }


        protected JSONObject doInBackground(String... params){

            JSONObject weatherData = null;
            try{
                weatherData = getData(params[0], params[1]);
            }catch (Exception e){
                Log.d("Error", "Exception");
            }

            return weatherData;
        }

        @Override
        protected  void onPostExecute (JSONObject json){
            Log.d("onPostExecute", "JSON");
            try{
                if(json != null){
                    System.out.println(json);
                    JSONObject details = json.getJSONArray("list").getJSONObject(0);
                    JSONObject dayTwo = json.getJSONArray("list").getJSONObject(1);
                    JSONObject dayThree = json.getJSONArray("list").getJSONObject(2);
                    JSONObject dayFour = json.getJSONArray("list").getJSONObject(3);
                    JSONObject dayFive = json.getJSONArray("list").getJSONObject(4);
                    JSONObject cityInfo = json.getJSONObject("city");
                    String city = cityInfo.getString("name")+ ", " + cityInfo.getString("country");
                    String dateInfo = getDate(details.getLong("dt"), "EEEE MMM dd,yyyy");
                    JSONObject weatherInfo = details.getJSONArray("weather").getJSONObject(0);
                    JSONObject dayTwoWeatherInfo = dayTwo.getJSONArray("weather").getJSONObject(0);
                    JSONObject dayThreeWeatherInfo = dayThree.getJSONArray("weather").getJSONObject(0);
                    JSONObject dayFourWeatherInfo = dayFour.getJSONArray("weather").getJSONObject(0);
                    JSONObject dayFiveWeatherInfo = dayFive.getJSONArray("weather").getJSONObject(0);
                    JSONObject dayTwoTempInfo = dayTwo.getJSONObject("temp");
                    JSONObject dayThreeTempInfo = dayThree.getJSONObject("temp");
                    JSONObject dayFourTempInfo = dayFour.getJSONObject("temp");
                    JSONObject dayFiveTempInfo = dayFive.getJSONObject("temp");

                    String mainIcon = weatherInfo.getString("icon");
                    String description = (weatherInfo.getString("description")).toUpperCase();
                    int temp = details.getJSONObject("temp").getInt("day");
                    int weatherId = weatherInfo.getInt("id");
                    String pressure = details.getString("pressure");
                    String humidity = details.getString("humidity");
                    JSONObject dayTwoInfo = new JSONObject();
                    dayTwoInfo.put("date", getDate(dayTwo.getLong("dt"), "EEE"));
                    dayTwoInfo.put("temp_min", dayTwoTempInfo.getInt("min"));
                    dayTwoInfo.put("temp_max", dayTwoTempInfo.getInt("max"));
                    dayTwoInfo.put("description", dayTwoWeatherInfo.getString("description").toUpperCase());
                    dayTwoInfo.put("id", dayTwoWeatherInfo.getInt("id"));
                    JSONObject dayThreeInfo = new JSONObject();
                    dayThreeInfo.put("date", getDate(dayThree.getLong("dt"), "EEE"));
                    dayThreeInfo.put("temp_min", dayThreeTempInfo.getInt("min"));
                    dayThreeInfo.put("temp_max", dayThreeTempInfo.getInt("max"));
                    dayThreeInfo.put("description", dayThreeWeatherInfo.getString("description").toUpperCase());
                    dayThreeInfo.put("id", dayThreeWeatherInfo.getInt("id"));
                    JSONObject dayFourInfo = new JSONObject();
                    dayFourInfo.put("date", getDate(dayFour.getLong("dt"), "EEE"));
                    dayFourInfo.put("temp_min", dayFourTempInfo.getInt("min"));
                    dayFourInfo.put("temp_max", dayFourTempInfo.getInt("max"));
                    dayFourInfo.put("description", dayFourWeatherInfo.getString("description").toUpperCase());
                    dayFourInfo.put("id", dayFourWeatherInfo.getInt("id"));
                    JSONObject dayFiveInfo = new JSONObject();
                    dayFiveInfo.put("date", getDate(dayFive.getLong("dt"), "EEE"));
                    dayFiveInfo.put("temp_min", dayFiveTempInfo.getInt("min"));
                    dayFiveInfo.put("temp_max", dayFiveTempInfo.getInt("max"));
                    dayFiveInfo.put("description", dayFiveWeatherInfo.getString("description").toUpperCase());
                    dayFiveInfo.put("id", dayFiveWeatherInfo.getInt("id"));

                    delegate.updateInUI(city, dateInfo, mainIcon, weatherId, description, pressure, humidity, temp, dayTwoInfo, dayThreeInfo, dayFourInfo, dayFiveInfo);
                }
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }
    public static String getDate(long time, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        TimeZone timezone = TimeZone.getDefault();
        calendar.setTimeInMillis(time*1000);
        calendar.add(Calendar.MILLISECOND, timezone.getOffset(calendar.getTimeInMillis()));
        Date date = (Date) calendar.getTime();
        return formatter.format(date);
    }

    public String getWeatherIcon(int actualId, String icon){
        int id = actualId / 100;
        String [] dayNight = icon.split("");
        String day = dayNight[2];
        String iconTemp ="";
        if(actualId == 800){
            if(day == "n") {
                iconTemp = getString(R.string.weather_sunny);
            } else {
                iconTemp = getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : iconTemp = getString(R.string.weather_thunder);
                    break;
                case 3 : iconTemp = getString(R.string.weather_drizzle);
                    break;
                case 7 : iconTemp = getString(R.string.weather_foggy);
                    break;
                case 8 : iconTemp = getString(R.string.weather_cloudy);
                    break;
                case 6 : iconTemp = getString(R.string.weather_snowy);
                    break;
                case 5 : iconTemp = getString(R.string.weather_rainy);
                    break;
            }
        }
        return iconTemp;
    }
}
