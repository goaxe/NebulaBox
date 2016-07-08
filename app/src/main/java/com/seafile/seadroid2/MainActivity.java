package com.seafile.seadroid2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.seafile.seadroid2.account.Account;


public class MainActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.buttion);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account loginAccount = new Account("http://166.111.131.62:6789/", "1@qq.com", null);
                SeafConnection sc = new SeafConnection(loginAccount);
                new MyThread(sc).start();
            }
        });
    }

    class MyThread extends Thread {
        private SeafConnection sc;

        public MyThread(SeafConnection sc) {
            this.sc = sc;

        }

        @Override
        public void run() {
            try {
                Log.e("====", "start run");
                if (!sc.doLogin("123456")) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
