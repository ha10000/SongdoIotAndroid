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
import java.util.ArrayList;

public class Arduino3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino3);
        Intent intent = getIntent();
        String sensorName = intent.getStringExtra("sensor");

        Log.d("test", "haha--------- -> Arduino3 from sensorList sensorName="+sensorName);
        Toast.makeText(Arduino3Activity.this, sensorName, Toast.LENGTH_SHORT).show();
        Log.d("test", "haha--------- -> 1");

//        final String sensors[] = { "dht11 온습도센서", "mq2 가스센서 " };
        final String sensors[] = { "dht11", "mq2" };
        Log.d("test", "haha--------- -> 2");


        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(Arduino3Activity.this,
                        android.R.layout.simple_spinner_item, sensors);
        Log.d("test", "haha--------- -> 3");
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        Log.d("test", "haha--------- -> 4");
        spinner.setAdapter(spinnerAdapter);
        Log.d("test", "haha--------- -> 5");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("test", "haha--------- -> 6");
                Log.d("test", "haha--------- -> spinner from sensorList position="+position);
                new Arduino3Activity.LoadSensorLogs().execute("arduino", sensors[position]);
                Log.d("test", "haha--------- -> 7");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        new LoadSensorLogs().execute("arduino", "dht11");
    }
    class Item {
        int temp, humidity; String created_at;
        String label1; String label2; String label3;
//        Item(int temp, int humidity, String created_at) {
//            this.temp = temp; this.humidity = humidity; this.created_at = created_at;
//        }
        Item(int temp, int humidity, String created_at, String label1, String label2, String label3) {
            this.temp = temp; this.humidity = humidity; this.created_at = created_at;
            this.label1 = label1; this.label2 = label2; this.label3 = label3;

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

            Log.d("test", "haha--------- -> 10"+items.get(position).label1);
            tempText.setText(items.get(position).label1+items.get(position).temp+"");
            humidityText.setText(items.get(position).label2+items.get(position).humidity+"");
            createdAtText.setText(items.get(position).label3+items.get(position).created_at);
            return view;
        }
    }
    class LoadSensorLogs extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(Arduino3Activity.this);
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
            Log.d("test", "hahah in LoadSensorLogs ip="+ipStr);
            Log.d("test", "hahah  in LoadSensorLogs----------- port="+portStr);

            Log.d("test", "hahah  in LoadSensorLogs----------- params[0]="+params[0]);
            Log.d("test", "hahah  in LoadSensorLogs----------- params[1]="+params[1]);

            try {

//                String apiURL = "http://192.168.0.27:3000/devices/"+params[0]+"/"+params[1];
                String apiURL = "http://"+ipStr+":"+portStr+"/devices/"+params[0]+"/"+params[1];
                Log.d("test", "hahah  in LoadSensorLogs----------- apiURL="+apiURL);

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
            dialog.setMessage("센서 로그수신중");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            Log.d("test", "haha--------- -> 8"+s);
            try {
                JSONArray array = new JSONArray(s);//JSON 문자??-> JSON 객체�?변??
                items.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.getString("sensor").equals("dht11")) {
                        items.add(new Item(obj.getInt("temp"), obj.getInt("humidity"),
                                obj.getString("created_at"),"온도값 : ", "습도값 : ", "생성일 : "));
                    } else {
                        items.add(new Item(obj.getInt("digital"), obj.getInt("analog"),
                                obj.getString("created_at"), "디지탈값 : ", "아닐로그값 : ", "생성일 : "));
                    }
                }
                ItemAdapter adapter = new ItemAdapter(Arduino3Activity.this);
                ListView listView = (ListView)findViewById(R.id.listview);
                listView.setAdapter(adapter);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void onClickGraphView(View view){
        Toast.makeText(Arduino3Activity.this, "onClickGraphView", Toast.LENGTH_LONG).show();
    }
}
