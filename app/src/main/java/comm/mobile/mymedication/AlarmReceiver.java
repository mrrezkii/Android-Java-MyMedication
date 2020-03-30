package comm.mobile.mymedication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import id.co.telkom.iot.AntaresHTTPAPI;
import id.co.telkom.iot.AntaresResponse;


public class AlarmReceiver extends BroadcastReceiver implements AntaresHTTPAPI.OnResponseListener {
    private String TAG = "ANTARES-API";
    private AntaresHTTPAPI antaresAPIHTTP;
    private String dataDevice;

    private String APIKEY = "004cbd64ff8a7fd4:53c77e7cf8c628fd";
    private String APPNAME = "MyMedication";
    private String DEVICENAME = "KotakObat";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        antaresAPIHTTP = new AntaresHTTPAPI();
        antaresAPIHTTP.addListener(this);
        antaresAPIHTTP.storeDataofDevice(8443, APIKEY, APPNAME, DEVICENAME, "{\\\"Status\\\":1}");
    }

    @Override
    public void onResponse(AntaresResponse antaresResponse) {
        Log.d(TAG, Integer.toString(antaresResponse.getRequestCode()));
        if (antaresResponse.getRequestCode() == 0) {
            try {
                JSONObject body = new JSONObject(antaresResponse.getBody());
                dataDevice = body.getJSONObject("m2m:cin").getString("con");
                Log.d(TAG, dataDevice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
