package com.example.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

import java.lang.Readable;

public class HelloWorld extends Activity {
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
   }

    public void sayHello(View button) {
        ((TextView)findViewById(R.id.HelloWorldTextView)).setText("Hello World!");
    }
}
