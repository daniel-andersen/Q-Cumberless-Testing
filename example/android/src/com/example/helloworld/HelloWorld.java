package com.example.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Spinner;
import android.view.View;

import java.lang.Readable;

public class HelloWorld extends Activity {
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
   }

    public void sayIt(View button) {
        final Spinner spinner = (Spinner) findViewById(R.id.SpinnerType);
        ((TextView)findViewById(R.id.HelloWorldTextView)).setText(spinner.getSelectedItem().toString());
    }
}
