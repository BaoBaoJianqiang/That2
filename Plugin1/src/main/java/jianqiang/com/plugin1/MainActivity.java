package jianqiang.com.plugin1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends BasePluginActivity {

    private static final String TAG = "Client-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        that.setContentView(R.layout.activity_main);

        //startActivity
        Button button1 = (Button) that.findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityByProxy("jianqiang.com.plugin1.SecondActivity");
            }
        });

        //startActivityForResult
        Button button2 = (Button) that.findViewById(R.id.button2);
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResultByProxy("jianqiang.com.plugin1.TestActivity", 0);
            }
        });


        Button button3 = (Button) that.findViewById(R.id.button3);
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("userName", "baojianqiang");
                ComponentName componentName = new ComponentName("jianqiang.com.hostapp", "jianqiang.com.hostapp.MainActivity");
                intent.setComponent(componentName);
                that.startActivity(intent);
            }
        });

        Button button4 = (Button) that.findViewById(R.id.button4);
        button4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult resultCode=" + resultCode);
        if (resultCode == RESULT_FIRST_USER) {
            that.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}