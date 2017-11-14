package kr.linkb.helloworld;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ArduinoActivity extends AppCompatActivity {
//17.11.09
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino);

        Intent intent = getIntent();
        String sensorName = intent.getStringExtra("sensor");
        Toast.makeText(ArduinoActivity.this, sensorName, Toast.LENGTH_SHORT).show();
Log.d("test","ArduinoActivity onCreate sensorName"+sensorName);
        final String sensors[] = { "dht11", "mq2" };
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(ArduinoActivity.this,
                        android.R.layout.simple_spinner_item, sensors);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new LoadSensorLogs().execute("arduino", sensors[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        new LoadSensorLogs().execute("arduino", "dht11");
    }
    class Item {
        int temp, humidity; String created_at;
        Item(int temp, int humidity, String created_at) {
            this.temp = temp; this.humidity = humidity; this.created_at = created_at;
        }
    }
    ArrayList<Item> items = new ArrayList<Item>();
    class ItemAdapter extends ArrayAdapter {
        public ItemAdapter(Context context) {
            super(context, R.layout.list_sensor_item, items);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_sensor_item, null);
            }
            TextView tempText = (TextView)view.findViewById(R.id.temp);
            TextView humidityText = (TextView)view.findViewById(R.id.humidity);
            TextView createdAtText = (TextView)view.findViewById(R.id.created_at);
            tempText.setText(items.get(position).temp+"");
            humidityText.setText(items.get(position).humidity+"");
            createdAtText.setText(items.get(position).created_at);
            return view;
        }
    }
    class LoadSensorLogs extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(ArduinoActivity.this);
        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();
            String sfName = "ServerInfo";

            // 지난번 저장해놨던 사용자 입력값을 꺼내서 보여주기
            SharedPreferences sf = getSharedPreferences(sfName, 0);
            String ipStr = sf.getString("ip", ""); // 키값으로 꺼냄
            String portStr = sf.getString("port", ""); // 키값으로 꺼냄

            Log.d("test", "hahah in LoadSensorLogs ip="+ipStr);
            Log.d("test", "hahah  in LoadSensorLogs----------- port="+portStr);
            Log.d("test", "hahah  in LoadSensorLogs----------- params[0]="+params[0]);
            Log.d("test", "hahah  in LoadSensorLogs----------- params[1]="+params[1]);


            try {
                String apiURL = "http://"+ipStr+":"+portStr+"/devices/"+params[0]+"/"+params[1];
//                String apiURL = "http://192.168.0.35:3000/devices/"+params[0]+"/"+params[1];
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) {  br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  br = new BufferedReader(new InputStreamReader(con.getErrorStream())); }
                String inputLine;
                while ((inputLine = br.readLine()) != null) { response.append(inputLine); }
                br.close();
            } catch (Exception e) { e.printStackTrace(); }
            return response.toString();
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("센서 로그 정보 수신 중...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            try {
                JSONArray array = new JSONArray(s);//JSON 문자열 -> JSON 객체로 변환
                items.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.getString("sensor").equals("dht11")) {
                        items.add(new Item(obj.getInt("temp"), obj.getInt("humidity"),
                                obj.getString("created_at")));
                    } else {
                        items.add(new Item(obj.getInt("digital"), obj.getInt("analog"),
                                obj.getString("created_at")));
                    }
                }
                ItemAdapter adapter = new ItemAdapter(ArduinoActivity.this);
                ListView listView = (ListView)findViewById(R.id.listview);
                listView.setAdapter(adapter);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void clickBuzzerOnButton(View view) {
        new SendBuzzerFlag().execute("buzzer","on");
    }
    public void clickBuzzerOffButton(View view) {
        new SendBuzzerFlag().execute("buzzer","off");
    }
    public void clickRedLEDOnButton(View view) {
        new SendBuzzerFlag().execute("led/red","on");
    }
    public void clickRedLEDOffButton(View view) {
        new SendBuzzerFlag().execute("led/red","off");
    }
    public void clickYellowLEDOnButton(View view) {
        new SendBuzzerFlag().execute("led/yellow","on");
    }
    public void clickYellowLEDOffButton(View view) {
        new SendBuzzerFlag().execute("led/yellow","off");
    }
    public void clickGreenLEDOnButton(View view) {
        new SendBuzzerFlag().execute("led/green","on");
    }
    public void clickGreenLEDOffButton(View view) {
        new SendBuzzerFlag().execute("led/green","off");
    }
    class SendBuzzerFlag extends AsyncTask<String, String, String> {
        ProgressDialog dialog = new ProgressDialog(ArduinoActivity.this);
        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();

            String sfName = "ServerInfo";
            SharedPreferences sf = getSharedPreferences(sfName, 0);
            String ipStr = sf.getString("ip", ""); // 키값으로 꺼냄
            String portStr = sf.getString("port", ""); // 키값으로 꺼냄
            Log.d("test", "hahah in SendBuzzerFlag ip="+ipStr);
            Log.d("test", "hahah  in SendBuzzerFlag----------- port="+portStr);

            for(int i=0; i< params.length; i++) {
                Log.d("test", "hahah  in SendBuzzerFlag----------- params["+i+"]=" + params[i]);
            }

            try {
                String urlString = "http://"+ipStr+":"+portStr+"/devices/"+params[0]+"/"+params[1];

//                String urlString = "http://192.168.0.27:3000/devices/"+params[0]+"/"+params[1];
//                String urlString = "http://192.168.0.35:3000/devices/"+params[0]+"/"+params[1];
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true); con.setDoOutput(true);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) {  br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else { br = new BufferedReader(new InputStreamReader(con.getErrorStream())); }
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
            } catch (Exception e) { e.printStackTrace(); }
            return response.toString();
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("부저 상태 정보 전송 중...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
        }
    }
}