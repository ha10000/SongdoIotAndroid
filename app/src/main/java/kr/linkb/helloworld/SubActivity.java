package kr.linkb.helloworld;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        /////////////
        Intent intent = getIntent();
        String edittext =
                intent.getStringExtra("edittext");
        EditText editText =
                (EditText)findViewById(R.id.edittext);
        editText.setText(edittext);

    }

    public void finishActivity(View view)
    {
        EditText editText =
                (EditText)findViewById(R.id.edittext);
        Intent intent = new Intent();
        intent.putExtra("edittext",
                editText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void StartThirdactivity(View view){
        Intent intent =
                new Intent(SubActivity.this, ThirdActivity.class);
        startActivity(intent);
    }

    public void StartCallactivity(View view){
        Intent intent =
                new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:01012345678"));
        try {
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void StartWebactivity(View view){
        Intent intent =
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.naver.com"));
            startActivity(intent);
    }

}
