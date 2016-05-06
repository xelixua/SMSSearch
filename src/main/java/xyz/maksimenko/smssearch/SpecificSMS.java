package xyz.maksimenko.smssearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class SpecificSMS extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String address = intent.getStringExtra("SMS_FROM");
        String text = intent.getStringExtra("SMS_BODY");
        long dateMillis = intent.getLongExtra("SMS_DATE", 0);
        final int position = intent.getIntExtra("SMS_POSITION_IN_LIST", 0);
        final int listLength = intent.getIntExtra("LIST_LENGTH", 0);
        final SpecificSMS self = this;
        System.out.println(address + " " + text);
        setContentView(R.layout.activity_specific_sms);
        TextView from = (TextView) findViewById(R.id.specSmsFromTextView);
        TextView date = (TextView) findViewById(R.id.specSmsDateTextView);
        TextView body = (TextView) findViewById(R.id.specSmsTextTextView);
        from.setText(address);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateMillis);
        String dateString = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        body.setText(text);
        date.setText(dateString);
        body.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeLeft() {
                Log.w("SMSSearch", "Swipe right");
                if (position + 1 == listLength) {
                    Toast.makeText(self, "Открыто последнее SMS в списке", Toast.LENGTH_LONG).show();
                    return;
                }
                showSpecificSMS(position + 1);
            }

            public void onSwipeRight() {
                Log.w("SMSSearch", "Swipe left");
                if (position == 0) {
                    Toast.makeText(self, "Открыта первое SMS в списке", Toast.LENGTH_LONG).show();
                    return;
                }
                showSpecificSMS(position - 1);
            }


        });
    }

    public void showSpecificSMS(int positionInList){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("ANOTHER_SMS");
        intent.putExtra("SMS_POSITION_IN_LIST", positionInList);
        startActivity(intent);
    }
}
