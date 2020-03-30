package comm.mobile.mymedication;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Button btnSetAlarm;
    final static int RQS_1 = 1;
    TimePickerDialog timePickerDialog;
    private TextView tvCountdownTimer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvCountdownTimer = findViewById(R.id.tv_CountdownTimer);
        btnSetAlarm = findViewById(R.id.btn_SetAlarm);

        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent SetAlarm = new Intent(getApplicationContext(), ListAlarmActivity.class);
                //startActivity(SetAlarm);
                openTimePickerDialog(false);
            }
        });
    }

    private void openTimePickerDialog(boolean b) {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(MainActivity.this,
                onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle("Set Alarm Time");

        timePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setAlarm(Calendar targetCal) {
        tvCountdownTimer.setText(+targetCal.get(Calendar.HOUR) + " : "
                + targetCal.get(Calendar.MINUTE) + " : "
                + targetCal.get(Calendar.SECOND));


        Intent intent = new Intent(getBaseContext(), comm.mobile.alarmapps.AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);
    }
}
