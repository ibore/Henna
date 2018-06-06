    package me.ibore.http.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.ibore.http.StringInfo;
import me.ibore.http.StringObserver;
import me.ibore.http.XHttp;
import me.ibore.http.exception.HttpException;

    public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XHttp.download("http://msoftdl.360.cn/mobilesafe/shouji360/360safe/500192/360MobileSafe.apk", new StringObserver() {

            @Override
            public void onSuccess(StringInfo stringInfo) {

            }

            @Override
            public void onError(HttpException e) {

            }
        });
    }
}
