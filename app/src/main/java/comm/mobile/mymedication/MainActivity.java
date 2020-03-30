package comm.mobile.mymedication;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import id.co.telkom.iot.AntaresHTTPAPI;
import id.co.telkom.iot.AntaresResponse;


public class MainActivity extends AppCompatActivity implements AntaresHTTPAPI.OnResponseListener {
    FloatingActionButton fbSetAlarm;
    final static int RQS_1 = 1;
    TimePickerDialog timePickerDialog;
    private TextView tvCountdownTimer;
    private Button btnStop, btnSnooze;

    private String TAG = "ANTARES-API";
    private AntaresHTTPAPI antaresAPIHTTP;
    private String dataDevice;

    private String APIKEY = "004cbd64ff8a7fd4:53c77e7cf8c628fd";
    private String APPNAME = "MyMedication";
    private String DEVICENAME = "KotakObat";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvCountdownTimer = findViewById(R.id.tv_CountdownTimer);
        fbSetAlarm = findViewById(R.id.fb_SetAlarm);
        btnStop = findViewById(R.id.btn_Stop);
        btnSnooze = findViewById(R.id.btn_Snooze);


        antaresAPIHTTP = new AntaresHTTPAPI();
        antaresAPIHTTP.addListener(this);

        fbSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent SetAlarm = new Intent(getApplicationContext(), ListAlarmActivity.class);
                //startActivity(SetAlarm);
                openTimePickerDialog(false);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                antaresAPIHTTP.storeDataofDevice(8443, APIKEY, APPNAME, DEVICENAME, "{\\\"Status\\\":0}");
            }
        });

        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                antaresAPIHTTP.storeDataofDevice(8443, APIKEY, APPNAME, DEVICENAME, "{\\\"Status\\\":2}");
            }
        });
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);

            setAlarm(calSet);
        }
    };

    private void openTimePickerDialog(boolean b) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(MainActivity.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setAlarm(Calendar targetCal) {
        tvCountdownTimer.setText(+targetCal.get(Calendar.HOUR) + " : "
                + targetCal.get(Calendar.MINUTE) + " : "
                + targetCal.get(Calendar.SECOND));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (targetCal.before(Calendar.getInstance())) {
            targetCal.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

        Toast.makeText(this, "Alarm aktif!", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onResponse(AntaresResponse antaresResponse) {
        Log.d(TAG, Integer.toString(antaresResponse.getRequestCode()));
        if (antaresResponse.getRequestCode() == 0) {
            try {
                JSONObject body = new JSONObject(antaresResponse.getBody());
                dataDevice = body.getJSONObject("m2m:cin").getString("con");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // txtData.setText(dataDevice);
                    }
                });
                Log.d(TAG, dataDevice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
