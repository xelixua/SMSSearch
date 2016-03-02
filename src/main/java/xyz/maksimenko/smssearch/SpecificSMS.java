package xyz.maksimenko.smssearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class SpecificSMS extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String address = intent.getStringExtra("SMS_FROM");
        String text = intent.getStringExtra("SMS_BODY");
        System.out.println(address + " " + text);
        setContentView(R.layout.activity_specific_sms);
        TextView from = (TextView) findViewById(R.id.specSmsFromTextView);
        TextView body = (TextView) findViewById(R.id.specSmsTextTextView);
        from.setText(address);
        body.setText(text);
    }

}
