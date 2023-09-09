package com.example.weatherapp;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private TextView weatherTextView;
    private Button getWeatherButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        weatherTextView = findViewById(R.id.weatherTextView);
        getWeatherButton = findViewById(R.id.getWeatherButton);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEditText.getText().toString();
                new GetWeatherTask().execute(city);
            }
        });
    }

    private class GetWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String apiKey = "2a0b99469a76770540f5effd78a04897"; // Replace with your API key
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    JSONObject main = json.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    String cityName = json.getString("name");

                    // Convert temperature from Kelvin to Celsius
                    double celsius = temperature - 273.15;

                    String unitSymbol = "°C"; // Always show in Celsius

                    // Determine weather conditions based on temperature in Celsius
                    String weatherConditions = getWeatherConditions(celsius);

                    String weatherText = "Weather in " + cityName + ": " + String.format("%.1f%s", celsius, unitSymbol)
                            + "\n" + weatherConditions;

                    weatherTextView.setText(weatherText);

                } catch (Exception e) {
                    e.printStackTrace();
                    weatherTextView.setText("Error fetching weather data.");
                }
            } else {
                weatherTextView.setText("Error fetching weather data.");
            }
        }

        private String getWeatherConditions(double temperatureCelsius) {
            if (temperatureCelsius > 30) {
                return "It's hot!";
            } else if (temperatureCelsius < 10) {
                return "It's cold!";
            } else {
                return "It's a pleasant day!";
            }
        }
    }
}
