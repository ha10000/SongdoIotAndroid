package kr.linkb.helloworld;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView)findViewById(R.id.text);
        textView.setText("Hi Hong Gi Dong");

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            // 전화걸기 가능
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.CALL_PHONE)){
                // 전화걸기 설명해 줘여
            }else{
                // 전화걸기 권한 요청
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.CALL_PHONE}, 1);
            }
        }
    }

    public void StartSubactivity(View view){

        EditText editText =
                (EditText)findViewById(R.id.edittext);
        Intent intent =
                new Intent(MainActivity.this,
                        SubActivity.class);
        intent.putExtra("edittext",
                editText.getText().toString());
//        startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if( resultCode == RESULT_OK){
                    String result =
                            data.getStringExtra("edittext");
                    EditText editText =
                            (EditText)findViewById(R.id.edittext);
                    editText.setText(result);
                }
                break;
        }
    }
}
