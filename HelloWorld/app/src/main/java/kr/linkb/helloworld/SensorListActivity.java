package kr.linkb.helloworld;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

public class SensorListActivity extends AppCompatActivity {



    class SensorItem {
        //        int temp, humidity; String created_at;
//        SensorItem(int temp, int humidity, String created_at) {
//            this.temp = temp; this.humidity = humidity; this.created_at = created_at;
//        }
        int id, user_id; String mac_address; String created_at;
        SensorItem(int id, int user_id, String mac_address, String created_at) {
            this.id = id; this.user_id = user_id; this.mac_address = mac_address;
            this.created_at = created_at ;
        }
    }
    ArrayList<SensorItem> sensor_items = new ArrayList<SensorItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        Log.d("test", "activity_sensor_list");
        Intent intent = getIntent();

        String sensorName = intent.getStringExtra("sensor_name");
        if( isEmpty(sensorName)) sensorName ="empty sensorName";
        String deviceName = intent.getStringExtra("device_name");
        if( isEmpty(deviceName)) deviceName ="empty deviceName";

        Log.d("test", "sensorName"+sensorName);;
        Log.d("test", "deviceName"+deviceName);;

        final String sensors[] = {"sen1", "sen2", "sen3", "sen4", "sen5"};
        sensors[0] = sensorName;
        ArrayAdapter<String > adapter = new ArrayAdapter<String>(
                SensorListActivity.this, android.R.layout.simple_list_item_1, sensors);
        ListView listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SensorListActivity.this, position+"",
                        Toast.LENGTH_LONG).show();
                Log.d("test", "haha---------before 0");
                Log.d("test", "haha---------position="+position);
                Log.d("test", "SensorListActivity to Arduino2Activity putExtra--sensor[position]"+sensors[position]);
                Log.d("test", "haha---------position="+position);
                Intent intent = new Intent(SensorListActivity.this,
                        Arduino3Activity.class);
                Log.d("test", "haha---------sensorList -> Arduino2 sensor_name="+"dht11");
                intent.putExtra("sensor", "dht11");
                startActivity(intent);
            }
        });
        new GetSensorLists().execute(sensors[0]);;

        Log.d("test", "haha--------111111111111111111="+"dht11");
        final Button button_control = (Button)findViewById(R.id.sensorControlButton) ;
        button_control.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("test", "clickSensorControlButton called....");
            Intent intent = new Intent(SensorListActivity.this,
                    ArduinoActivity.class);
            Log.d("test", "haha---------clickSensorControlButton -> Arduino sensor_name="+"dht11");
            intent.putExtra("sensor", "dht11");
            startActivity(intent);
            }
        });

        final Button button_location = (Button)findViewById(R.id.sensorLocationButton) ;
        button_location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("test", "sensorLocation called....");
                Intent intent = new Intent(SensorListActivity.this,
                        GoogleMapActivity.class);
                Log.d("test", "haha---------clickSensorControlButton -> Arduino sensor_name="+"dht11");
//                intent.putExtra("sensor", "dht11");
                startActivity(intent);
            }
        });

    }

    class SensorItemAdapter extends ArrayAdapter {
        public SensorItemAdapter(Context context) {
            super(context, R.layout.list_sensor_item2, sensor_items);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_sensor_item2, null);
            }
            TextView textview1 = (TextView)view.findViewById(R.id.id);
            TextView textview2 = (TextView)view.findViewById(R.id.user_id);
            TextView textview3 = (TextView)view.findViewById(R.id.mac_address);
            TextView textview4 = (TextView)view.findViewById(R.id.created_at);
            textview1.setText("SENSOR ID :" + sensor_items.get(position).id+"");
            textview2.setText("USER ID :" + sensor_items.get(position).user_id+"");
            textview3.setText("SENSOR_주소:" + sensor_items.get(position).mac_address+"");
            textview4.setText("생성일 :" + sensor_items.get(position).created_at);
            return view;
        }
    }


    public void clickSensorAddButton(View view) {
//        new SensorAdd().execute("sensorName","SensorDevice");
    }
    public void clickSensorControlButton()
    {
        Log.d("test", "clickSensorControlButton called....");
//        Intent intent = new Intent(SensorListActivity.this,
//                ArduinoActivity.class);
//        Log.d("test", "haha---------clickSensorControlButton -> Arduino sensor_name="+"dht11");
//        intent.putExtra("sensor", "dht11");
//        startActivity(intent);
    }
    class GetSensorLists extends AsyncTask<String, String, String> {
        ProgressDialog dialog = new ProgressDialog(SensorListActivity.this);

        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();
            String sfName = "ServerInfo";
            // 지난번 저장해놨던 사용자 입력값을 꺼내서 보여주기
            SharedPreferences sf = getSharedPreferences(sfName, 0);
            String ipStr = sf.getString("ip", ""); // 키값으로 꺼냄
//                ServerIpEditText.setText(ipStr); // EditText에 반영함
            String portStr = sf.getString("port", ""); // 키값으로 꺼냄
//                ServerPortEditText.setText(portStr); // EditText에 반영함
            Log.d("test", "hahah ip="+ipStr);
            Log.d("test", "hahah----------- port="+portStr);
            Log.d("test", "hahah----------- GetSensorLists params[0]="+params[0]);
//            Log.d("test", "hahah----------GetSensorLists- params[1]="+params[1]);


            try {
                Log.d(this.getClass().getName(), "haha---------0");
//                String urlString = "http://192.168.0.27:3000/devices/"+params[0]+"/"+params[1];
//                String urlString = "http://"+ipStr+":"+portStr+"/devices/"+params[0]+"/"+params[1];

                String urlString = "http://"+ipStr+":"+portStr+"/devices/"+params[0];
                for( int i =0; i< params.length; i++) {
                    Log.d("test", "hahah----------GetSensorLists- params[" + i+"]"+params[i]);
                }
                Log.d("test", "hahah----------GetSensorLists- urlString="+urlString);

                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
//                con.setRequestMethod("POST");
                con.setRequestMethod("GET");
                //con.setDoInput(true); con.setDoOutput(true);
                int responseCode = con.getResponseCode();
                BufferedReader br;

                if(responseCode==200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    Log.d(this.getClass().getName(), "haha-----getInputStream----");

                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    Log.d(this.getClass().getName(), "haha-----getErrorStream----");
                }
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
/*
                    params[0] = inputLine; //haha
                    Log.d(this.getClass().getName(), inputLine);
  */
                }
                br.close();
            } catch (Exception e) { e.printStackTrace(); }

            return response.toString();
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("센서정보 수신 중...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Log.i("haresulthaha", s);

            try {
                JSONArray array = new JSONArray(s);//JSON 문자열 -> JSON 객체로 변환
                Log.i("length", array.length()+"");
                sensor_items.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
//                    if (obj.getString("id").equals("dht11")) {
//                        sensor_items.add(new SensorItem(obj.getInt("user_id"), obj.getInt("mac_address"),
//                                obj.getString("created_at")));
//                    } else {
//                        sensor_items.add(new SensorItem(obj.getInt("user_id"), obj.getInt("analog"),
//                                obj.getString("created_at")));
//                    }
                    /*
                    sensor_items.add(new SensorItem(obj.getInt("id"), obj.getInt("user_id"),
                            obj.getString("mac_address"),obj.getString("created_at")));
                            */
                    sensor_items.add(new SensorItem(obj.getInt("id"), obj.getInt("user_id"),
                            obj.getString("mac_address"),obj.getString("created_at")));

                }
                SensorItemAdapter adapter = new SensorItemAdapter(SensorListActivity.this);
                ListView listView = (ListView)findViewById(R.id.listview);
                listView.setAdapter(adapter);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
