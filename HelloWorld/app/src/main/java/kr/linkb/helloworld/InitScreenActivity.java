package kr.linkb.helloworld;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InitScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_screen);

        Log.d("test", "--------activity_init_screen");
        Toast.makeText(InitScreenActivity.this, "InitScreenActivity", Toast.LENGTH_LONG).show();

        TextView editText = (TextView)findViewById(R.id.user_id);
        String strText = editText.getText().toString() ;
        Log.d("test", "activity_init_screen--------strText--"+strText);
//     editText.setText("hhhh");
        setContentView(R.layout.activity_init_screen);
        Toast.makeText(InitScreenActivity.this, "editText", Toast.LENGTH_LONG).show();

//        final Button buttonLogin = (Button) findViewById(R.id.ButtonLogin);
//        buttonLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(InitScreenActivity.this, "onClick", Toast.LENGTH_LONG).show();
//
//                Intent intent = new Intent(InitScreenActivity.this,
//                        SensorListActivity.class);
//                intent.putExtra("device_name", "device_name");
//                intent.putExtra("sensor_name", "sensor_name");
//                startActivity(intent);
//            }
//        });
    }

    public void clickLoginButton(View view) {

        Log.d("test", "--------clickLoginButton");
        Log.d("test", "view"+view);;

        Toast.makeText(InitScreenActivity.this, "clickLoginButton", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(InitScreenActivity.this,
                SensorListActivity.class);
//        intent.putExtra("device_name", "device_name");
        intent.putExtra("sensor_name", "sensor_name");
        startActivity(intent);
    }
//
    public void clickNetworkSettingButton(View view) {
//        AlertDialog.Builder dialog =
//                new AlertDialog.Builder(InitScreenActivity.this);
//        dialog.setTitle("알림");
//        dialog.setMessage("이것은 얼럿 다이알로그입니다");
        Log.d("test", "--------clickNetworkSettingButton");
//        Toast.makeText(InitScreenActivity.this, "clickSettingButton", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(
            InitScreenActivity.this,
            NetworkSetActivity.class);
        startActivity(intent);
    }

}
