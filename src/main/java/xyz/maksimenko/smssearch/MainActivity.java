package xyz.maksimenko.smssearch;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private Cursor cursor;
    private static Search lastSearch;
    private SMS currentSMS;
    static List<SMS> foundSMS;

    public long getDateFrom() {
        return dateFrom;
    }

    public long getDateTo() {
        return dateTo;
    }

    private long dateFrom = 0;
    private long dateTo = Long.MAX_VALUE;

    public void setDateFrom(long dateFrom) {
        System.out.println("Setting dateFrom " + dateFrom);
        if(dateFrom > this.dateTo){
            Toast.makeText(this, "Начальная дата должна быть раньше конечной даты", Toast.LENGTH_LONG).show();
            return;
        }
        this.dateFrom = dateFrom;
        formPeriodString();
    }

    public void setDateTo(long dateTo) {
        System.out.println("Setting dateTo " + dateTo);
        if(dateTo < this.dateFrom){
            Toast.makeText(this, "Конечная дата должна быть позже начальной даты", Toast.LENGTH_LONG).show();
            return;
        }
        this.dateTo = dateTo;
        formPeriodString();
    }

    private void formPeriodString(){
        String period;
        if(dateFrom == 0 || dateTo == Long.MAX_VALUE){
            period = "За все время";
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(dateFrom);
            period = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " - ";
            c.setTimeInMillis(dateTo);
            period += c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
        }

        TextView periodView = (TextView) findViewById(R.id.periodTextView);
        periodView.setText(period);
        searchButtonClicked(null);
    }
    public void searchButtonClicked(View view){
        EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        String searchText = searchEditText.getText().toString();
        lastSearch = new Search(searchText);
        ListView foundList = (ListView) findViewById(R.id.foundList);
        cursor.moveToFirst();
        foundSMS = new ArrayList<SMS>();
        List<String> foundSMSnames = new ArrayList<String>();
        do {
            int ind = cursor.getColumnIndex("body");
            String body = cursor.getString(ind);
            ind = cursor.getColumnIndex("address");
            String address = cursor.getString(ind);
            ind = cursor.getColumnIndex("date");
            long date = cursor.getLong(ind);
            if((lastSearch.match(address) || lastSearch.match(body) || searchText.length() == 0) && date > dateFrom && date < dateTo){
                SMS sms = new SMS(address, body, date);
                foundSMS.add(sms);
                foundSMSnames.add(sms.getName());
            }
        }while(cursor.moveToNext());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, foundSMSnames);

        // присваиваем адаптер списку
        foundList.setAdapter(adapter);
        foundList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        TextView fromDescr = (TextView) findViewById(R.id.textView);
        TextView textDescr = (TextView) findViewById(R.id.textView3);
        TextView from = (TextView) findViewById(R.id.smsFromTextView);
        TextView text = (TextView) findViewById(R.id.smsTextTextView);


        if(foundSMSnames.size() == 0){
            Toast.makeText(this, "Сообщения не найдены", Toast.LENGTH_LONG).show();
            fromDescr.setVisibility(View.INVISIBLE);
            textDescr.setVisibility(View.INVISIBLE);
            from.setVisibility(View.INVISIBLE);
            text.setVisibility(View.INVISIBLE);
            return;
        }

        String selectedName = (String) foundList.getAdapter().getItem(0);
        //refreshMessageText(selectedName, 0);

        if(view != null) {
            //if the function called by button click
            fromDescr.setVisibility(View.VISIBLE);
            textDescr.setVisibility(View.VISIBLE);
            from.setVisibility(View.VISIBLE);
        }
        text.setVisibility(View.VISIBLE);
    }

    private void  refreshMessageText(String selectedName, int selectedPosition) {
        Log.w("SMSSEarch","Renew message text");
        Iterator<SMS> it = foundSMS.iterator();
        TextView from = (TextView) findViewById(R.id.smsFromTextView);
        TextView textDescr = (TextView) findViewById(R.id.textView3);
        TextView fromDescr = (TextView) findViewById(R.id.smsFromTextView);
        TextView text = (TextView) findViewById(R.id.smsTextTextView);
        int position = 0;
        while(it.hasNext()){
            SMS sms = it.next();
            if(sms.getName().equals(selectedName) || position == selectedPosition){
                sms.setPositionInList(selectedPosition);
                from.setVisibility(View.VISIBLE);
                textDescr.setVisibility(View.VISIBLE);
                fromDescr.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                System.out.println(sms.getAddress());
                System.out.println(sms.getBody());
                currentSMS = sms;
                TextView smsAddress = (TextView) findViewById(R.id.smsFromTextView);
                TextView smsText = (TextView) findViewById(R.id.smsTextTextView);
                smsAddress.setText(sms.getAddress());
                String body = sms.getBody();
                if(body.length() > 56){
                    body = body.substring(0, 55) + "...";
                }
                if(smsText.getText().equals(body)){
                    //it's the second time user click on same SMS. Looks like he want to open it
                    showSpecificSMS(null);
                } else {
                    smsText.setText(body);
                }
                return;
            }
            position++;
        }
        from.setVisibility(View.INVISIBLE);
        textDescr.setVisibility(View.INVISIBLE);
        fromDescr.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
    }

    public void showSpecificSMS(View view) {
        Log.w("MainActivity", "Show specific SMS");
        Intent intent = new Intent(this, SpecificSMS.class);
        intent.putExtra("SMS_FROM", currentSMS.getAddress());
        intent.putExtra("SMS_BODY", currentSMS.getBody());
        intent.putExtra("SMS_DATE", currentSMS.getDate());
        intent.putExtra("SMS_POSITION_IN_LIST", currentSMS.getPositionInList());
        intent.putExtra("LIST_LENGTH", foundSMS.size());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("SMSSearch", "Main activity started");
        super.onCreate(savedInstanceState);
        cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        //if we open main activity to show next SMS
        if(intent.getAction().equals("ANOTHER_SMS")){
            Log.w("SMSSearch", intent.toString());
            //activity has been called from specificSMS, so we need to show SMS at specific position
            int position = intent.getIntExtra("SMS_POSITION_IN_LIST", 0);
            refreshMessageText("", position);
            currentSMS = foundSMS.get(position);
            currentSMS.setPositionInList(position);
            showSpecificSMS(null);
            return;
        }
        lastSearch = new Search("");
        refreshMessagesList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMessagesList();
    }

    private void refreshMessagesList() {
        EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.setText(lastSearch.getSearchString());
        formPeriodString();
        ListView foundList = (ListView) findViewById(R.id.foundList);
        foundList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ListView foundList = (ListView) findViewById(R.id.foundList);
                String selectedName = (String) foundList.getAdapter().getItem(position);
                refreshMessageText(selectedName, position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.fromDateMenuItem){
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.setMainActivity(this);
            newFragment.setType(0);
            newFragment.show(getFragmentManager(), "datePicker");
            return true;
        }

        if(id == R.id.toDateMenuItem) {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.setMainActivity(this);
            newFragment.setType(1);
            newFragment.show(getFragmentManager(), "datePicker");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
