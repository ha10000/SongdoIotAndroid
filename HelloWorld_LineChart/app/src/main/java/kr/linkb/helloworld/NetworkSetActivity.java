package kr.linkb.helloworld;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class NetworkSetActivity extends AppCompatActivity {
    String sfName = "ServerInfo";
    String sfNameMongo = "MongoServerInfo";
    String sfNameMqtt = "MqttServerInfo";
    String sfNameRedis = "RedisServerInfo";
//    String sName[] = {"Server", "Mongo", "Mqtt", "redis"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_set);

        final String sfName = "ServerInfo";
        final String sfNameMongo = "MongoServerInfo";
        final String sfNameMqtt = "MqttServerInfo";
        final String sfNameRedis = "RedisServerInfo";

        //----- node서버
        SharedPreferences sf = getSharedPreferences(sfName, 0);
        final String ipStr = sf.getString("ip", ""); // 키값으로 꺼냄
        final String portStr = sf.getString("port", ""); // 키값으로 꺼냄
        Log.d("test", "hahah ip="+ipStr);
        Log.d("test", "hahah----------- port="+portStr);
        final EditText ServerIpEditText = (EditText)findViewById(R.id.ServerIpEditText01);
        final EditText ServerPortEditText = (EditText)findViewById(R.id.ServerPortEditText01);
        ServerIpEditText.setText(ipStr); // EditText에 반영함
        ServerPortEditText.setText(portStr); // EditText에 반영함

        //--------------몽고
        SharedPreferences sfMongo = getSharedPreferences(sfNameMongo, 0);
        final String ipStrMongo = sf.getString("ip", ""); // 키값으로 꺼냄
        final String portStrMongo = sf.getString("port", ""); // 키값으로 꺼냄
        Log.d("test", "hahah ipStrMongo="+ipStrMongo);
        Log.d("test", "hahah----------- portStrMongo="+portStrMongo);
        final EditText MongoIpEditText = (EditText)findViewById(R.id.MongoIpEditText01);
        final EditText MongoPortEditText = (EditText)findViewById(R.id.MongoPortEditText01);
        MongoIpEditText.setText(ipStrMongo); // EditText에 반영함
        MongoPortEditText.setText(portStrMongo); // EditText에 반영함

        //--- MQTT 서버
        SharedPreferences sfMqtt = getSharedPreferences(sfNameMqtt, 0);
        final String ipStrMqtt = sf.getString("ip", ""); // 키값으로 꺼냄
        final String portStrMqtt = sf.getString("port", ""); // 키값으로 꺼냄
        Log.d("test", "hahah ipStrMqtt="+ipStrMqtt);
        Log.d("test", "hahah----------- portStrMqtt="+portStrMqtt);
        final EditText MqttIpEditText = (EditText)findViewById(R.id.MqttIpEditText01);
        final EditText MqttPortEditText = (EditText)findViewById(R.id.MqttPortEditText01);
        MongoIpEditText.setText(ipStrMqtt); // EditText에 반영함
        MongoPortEditText.setText(portStrMqtt); // EditText에 반영함

        final Button button_w = (Button)findViewById(R.id.ButtonWriteFile) ;
        final Button button_r = (Button)findViewById(R.id.ButtonReadFile) ;
        final Button button_n = (Button)findViewById(R.id.ButtonNetworkSet) ;

        button_w.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 연결 서버 정보 저장
                SharedPreferences sf = getSharedPreferences(sfName, 0);
                SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요
                String wIpStr = ServerIpEditText.getText().toString(); // 사용자가 입력한 값
                String wPortStr = ServerPortEditText.getText().toString(); // 사용자가 입력한 값
                editor.putString("ip", wIpStr); // 입력
                editor.putString("port", wPortStr); // 입력
                editor.commit(); // 파일에 최종 반영함
                Log.d("test", "write wIpStr="+wIpStr );
                Log.d("test", "write wPortStr="+wPortStr );

                // 연결 Mongo 서버 정보 저장
                SharedPreferences sfMongo = getSharedPreferences(sfNameMongo, 0);
                SharedPreferences.Editor editorMongo = sfMongo.edit();//저장하려면 editor가 필요
                String MongoIpStr = MongoIpEditText.getText().toString(); // 사용자가 입력한 값
                String MongoPortStr = MongoPortEditText.getText().toString(); // 사용자가 입력한 값
                editorMongo.putString("ip", MongoIpStr); // 입력
                editorMongo.putString("port", MongoPortStr); // 입력
                editorMongo.commit(); // 파일에 최종 반영함
                Log.d("test", "write MongoIpStr="+MongoIpStr );
                Log.d("test", "write MongoPortStr="+MongoPortStr );

                // 연결 Mqtt 서버 정보 저장
                SharedPreferences sfMqtt = getSharedPreferences(sfNameMqtt, 0);
                SharedPreferences.Editor editorMqtt = sfMqtt.edit();//저장하려면 editor가 필요
                String MqttIpStr = MqttIpEditText.getText().toString(); // 사용자가 입력한 값
                String MqttPortStr = MqttPortEditText.getText().toString(); // 사용자가 입력한 값
                editorMqtt.putString("ip", MqttIpStr); // 입력
                editorMqtt.putString("port", MqttPortStr); // 입력
                editorMqtt.commit(); // 파일에 최종 반영함
                Log.d("test", "write MqttIpStr="+MqttIpStr );
                Log.d("test", "write MqttPortStr="+MqttPortStr );

            }
        });


        button_r.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // 지난번 저장해놨던 사용자 입력값을 꺼내서 보여주기
                SharedPreferences sf = getSharedPreferences(sfName, 0);
                String ipStr = sf.getString("ip", ""); // 키값으로 꺼냄
                ServerIpEditText.setText(ipStr); // EditText에 반영함
                String portStr = sf.getString("port", ""); // 키값으로 꺼냄
                ServerPortEditText.setText(portStr); // EditText에 반영함
                Log.d("test", "read ipStr="+ipStr );
                Log.d("test", "read portStr="+portStr );

                //  MongoDB 서버 정보 읽기
                SharedPreferences Mongosf = getSharedPreferences(sfNameMongo, 0);
                String MongoIpStr = Mongosf.getString("ip", ""); // 키값으로 꺼냄
                MongoIpEditText.setText(MongoIpStr); // EditText에 반영함
                String MongoPortStr = Mongosf.getString("port", ""); // 키값으로 꺼냄
                MongoPortEditText.setText(MongoPortStr); // EditText에 반영함
                Log.d("test", "read MongIpStr="+MongoIpStr );
                Log.d("test", "read MongoPortStr="+MongoPortStr );

                // /  MqttDB 서버 정보 읽기
                SharedPreferences Mqttsf = getSharedPreferences(sfNameMqtt, 0);
                String MqttIpStr = Mqttsf.getString("ip", ""); // 키값으로 꺼냄
                MqttIpEditText.setText(MqttIpStr); // EditText에 반영함
                String MqttPortStr = Mqttsf.getString("port", ""); // 키값으로 꺼냄
                MqttPortEditText.setText(MqttPortStr); // EditText에 반영함
                Log.d("test", "read MqttIpStr="+MqttIpStr );
                Log.d("test", "read MqttPortStr="+MqttPortStr );
            }
        });

        button_n.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(NetworkSetActivity.this, "ButtonNetworkSave", Toast.LENGTH_LONG).show();
//                final String sfName = "ServerInfo";
//                final String sfName = "ServerInfo";
//                final String sfName = "ServerInfo";
                String sfName = "ServerInfo";
                String MongosfName = "MongoServerInfo";
                String MqttsfName = "MqttServerInfo";
                String RedissfName = "RedisServerInfo";

                String ipAddress; String portNo;
                String MongoIpAddress; String MongoPortNo;
                String MqttIpAddress; String MqttPortNo;

                SharedPreferences sf = getSharedPreferences(sfName, 0);
                ipAddress = sf.getString("ip", ""); // 키값으로 꺼냄
                portNo = sf.getString("port", ""); // 키값으로 꺼냄

                SharedPreferences sfMongo = getSharedPreferences(MongosfName, 0);
                MongoIpAddress = sfMongo.getString("ip", ""); // 키값으로 꺼냄
                MongoPortNo = sfMongo.getString("port", ""); // 키값으로 꺼냄

                SharedPreferences sfMqtt = getSharedPreferences(MqttsfName, 0);
                MqttIpAddress = sfMqtt.getString("ip", ""); // 키값으로 꺼냄
                MqttPortNo = sfMqtt.getString("port", ""); // 키값으로 꺼냄

                Log.d("test", "SaveNetworkInfo ipStr="+ipAddress );
                Log.d("test", "SaveNetworkInfo portStr="+portNo );
                Log.d("test", "MongoServerInfo ipStr="+MongoIpAddress );
                Log.d("test", "MongoServerInfo portStr="+MongoPortNo );
                Log.d("test", "MqttServerInfo ipStr="+MqttIpAddress );
                Log.d("test", "MqttServerInfo portStr="+MqttPortNo );
                new SaveNetworkInfo().execute(ipAddress, portNo,
                        MongoIpAddress, MongoPortNo,
                        MqttIpAddress, MqttPortNo
                        );
            }
        });
    }



    public void ButtonNetworkSave(){

    }

    public class ServerInfo
    {
        String ip;
        String port;
    }

    class SaveNetworkInfo extends AsyncTask<String,String,String>{

        ProgressDialog dialog = new ProgressDialog(NetworkSetActivity.this);

        @Override
        protected String doInBackground(String... params) {
            StringBuilder output = new StringBuilder();
            String ipAddress = params[0]; // 연결하고자 하는 서버 IP";
            String portNo = params[1]; // 연결하고자 하는 서버의 포트번호
            String MongoIP = params[2];   // 연결하고자 하는 몽고DB IP";
            String MongoPortNo = params[3]; // "27017"; // 연결하고자 하는 서버의 몽고DB트번호
            String MqttIP = params[4];   // 연결하고자 하는 몽고DB IP";
            String MqttPortNo = params[5]; // "27017"; // 연결하고자 하는 서버의 몽고DB트번호


            StringBuffer response = new StringBuffer();
            try {
                String text = URLEncoder.encode(params[0], "UTF-8");
                String apiURL = "http://"+ipAddress+":"+portNo+"/networks";
                Log.d("test", "haha apiURL="+apiURL );

//                String apiURL = "http://"+params[0]+params[1]+"/networks/"+params[0]+"/"+params[1]+params[2]+"/"+params[3];
//                String apiURL = "http://192.168.0.24:3000/networks/"+params[0]+"/"+params[1]+params[2]+"/"+params[3];
//                String apiURL = "https://openapi.naver.com/v1/search/blog.json?query="+ text; // json 결과
                //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
                URL url = new URL(apiURL);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("server_ip", params[0]);
                postDataParams.put("server_port", params[1]);
                postDataParams.put("mongo_ip", params[2]);
                postDataParams.put("mongo_port", params[3]);

                Log.d("test", "haha postDataParams="+postDataParams );

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                con.setRequestMethod("POST");

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true); conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();
                    os.close();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) break;
                        output.append(line);
                    }
                    reader.close();
                    conn.disconnect();
                }
            } catch (Exception e) {
//                System.out.println(e);
                e.printStackTrace();
            }
            return response.toString();
        }
        // <ctrl + o> 로 onPreExecute, onPostExecute 불러옴...
        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            dialog.setMessage("Network 정보 보관중...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
            dialog.dismiss();
            Log.i("json",s);
            Toast.makeText(NetworkSetActivity.this, s, Toast.LENGTH_LONG).show();
            try{
/*
                // JSON 문자열 --> JSON객체로 변환
                JSONObject json = new JSONObject(s);
                // JSON객체에서  items 키값의 배열을 추출 --> 여기에만 해당
                JSONArray items = json.getJSONArray("items");

                itemList.clear(); // 동적 배열 초기화
//                Toast.makeText(NaverOpenAPIActivity.this, items.length() + "", Toast.LENGTH_LONG).show();
                for( int i = 0; i < items.length();i++){ // items 배열 안의 객체 정보 개별 추출
                    JSONObject obj = items.getJSONObject(i);
                    String title = obj.getString("title");
                    String link = obj.getString("link");
                    String description = obj.getString("description");
                    String bloggername = obj.getString("bloggername");
                    String bloggerlink = obj.getString("bloggerlink");
                    String postdate = obj.getString("postdate");
                    itemList.add(new NaverOpenAPIActivity.Item(title, link, description, bloggername, bloggerlink, postdate));
//                    Log.i("title", title);
                }
                NaverOpenAPIActivity.BlogAdapter adapter = new NaverOpenAPIActivity.BlogAdapter(NaverOpenAPIActivity.this);
                ListView listView = (ListView)findViewById(R.id.listview);
                listView.setAdapter(adapter);
//                int display = json.getInt("display");

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(itemList.get(position).link));
                        startActivity(intent);
                    }
                });
*/
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        Log.d("test", "result="+result);
        return result.toString();
    }
}

