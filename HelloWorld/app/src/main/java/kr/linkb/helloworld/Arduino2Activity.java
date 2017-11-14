package kr.linkb.helloworld;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import static kr.linkb.helloworld.R.id.user_id;

public class Arduino2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino2);

        Intent intent = getIntent();
        String sensorName = intent.getStringExtra("sensor");

        Log.d("test", "haha---------Arduino2 from sensorList  sensor_name="+sensorName);
        Log.d("test", "===>sensor_name at Arduino2 "+ sensorName);
        if( isEmpty(sensorName)) sensorName ="dht11";

        Toast.makeText(Arduino2Activity.this, sensorName, Toast.LENGTH_LONG).show();

        new LoadSensorLogData().execute("arduino", "mq2","1");
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

        String[] FieldString = {"온도 : ", "습도 : ", "생성일 :"};
        String[] FieldString2 = {"digital Value : ", "Analog Value : ", "생성일 :"};

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_sensor_item, null);
            }
            TextView textView1 = (TextView)view.findViewById(R.id.temp);
            TextView textView2 = (TextView)view.findViewById(R.id.humidity);
            TextView textView3 = (TextView)view.findViewById(R.id.created_at);
            textView1.setText(FieldString[0]+ items.get(position).temp+"");
            textView2.setText(FieldString[1]+ items.get(position).humidity+"");
            textView3.setText(FieldString[2]+ items.get(position).created_at);
            return view;
        }
    }
    class LoadSensorLogData extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(Arduino2Activity.this);
        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();
            try {

//                String apiURL = "http://192.168.0.24:3000/devices/+params[0];

                String apiURL = "http://192.168.0.24:3000/devices/"+params[0]+"/"+params[1];
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
            Log.d("test", "===>onPostExecute at Arduino2 "+ s);
            dialog.dismiss();
            try {
                JSONArray array = new JSONArray(s);//JSON 문자열 -> JSON 객체로 변환
                items.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.getString("sensor").equals("dht11")) {
                        items.add(new Arduino2Activity.Item(obj.getInt("temp"), obj.getInt("humidity"),
                                obj.getString("created_at")));
                    } else {
                        items.add(new Arduino2Activity.Item(obj.getInt("digital"), obj.getInt("analog"),
                                obj.getString("created_at")));
                    }
                }
                Arduino2Activity.ItemAdapter adapter = new Arduino2Activity.ItemAdapter(Arduino2Activity.this);
                ListView listView = (ListView)findViewById(R.id.listview);
                listView.setAdapter(adapter);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
