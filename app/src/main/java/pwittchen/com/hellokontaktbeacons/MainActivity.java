package pwittchen.com.hellokontaktbeacons;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button btnBeaconMonitor;
    private Button btnBeaconRange;
    private Button btnApiSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);
        setViews();
        setBtnListeners();
    }

    private void setViews() {
        btnBeaconMonitor = (Button) findViewById(R.id.btn_beacon_monitor);
        btnBeaconRange = (Button) findViewById(R.id.btn_beacon_range);
        btnApiSupport = (Button) findViewById(R.id.btn_api_support);
    }

    private void setBtnListeners() {
        btnBeaconMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BeaconMonitorActivity.class));
            }
        });

        btnBeaconRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BeaconRangeActivity.class));
            }
        });

        btnApiSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkApiSupport();
            }
        });
    }

    private void checkApiSupport() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            showDialog("Unfortunately your device does not support BLE", android.R.drawable.ic_dialog_alert);
        } else {
            showDialog("Your device supports BLE", android.R.drawable.ic_dialog_info);
        }
    }

    private void showDialog(String message, int iconResource) {
        final Resources resources = getResources();
        new AlertDialog.Builder(this)
                .setIcon(resources.getDrawable(iconResource))
                .setMessage(message)
                .setNeutralButton("OK", null)
                .show();
    }
}
