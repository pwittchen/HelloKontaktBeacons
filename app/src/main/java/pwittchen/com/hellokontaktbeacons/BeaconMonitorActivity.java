package pwittchen.com.hellokontaktbeacons;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kontakt.sdk.android.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.configuration.MonitorPeriod;
import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.Beacon;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.manager.BeaconManager;

import java.util.ArrayList;
import java.util.List;

public class BeaconMonitorActivity extends Activity {

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;
    private BeaconManager beaconManager;
    private Context context;
    private ListView lvBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.a_beacon_monitor);
        lvBeacons = (ListView) findViewById(R.id.lv_beacons);
        beaconManager = BeaconManager.newInstance(this);
        beaconManager.setMonitorPeriod(MonitorPeriod.MINIMAL);
        beaconManager.setForceScanConfiguration(ForceScanConfiguration.DEFAULT);
        beaconManager.registerMonitoringListener(new BeaconManager.MonitoringListener() {

            /**
             * Monitor Service has started
             */
            @Override
            public void onMonitorStart() {
                showToast("Started monitoring");
            }

            /**
             * Monitor Service has been stopped
             */
            @Override
            public void onMonitorStop() {
                showToast("Stopped monitoring");
            }

            /**
             * Beacon has been detected
             * @param region
             * @param beacons
             */
            @Override
            public void onBeaconsUpdated(final Region region, final List<Beacon> beacons) {
                showToast("Beacon detected");
                displayListOfBeacons(beacons);
            }

            /**
             * Beacon has appeared for the first time in monitored region
             * @param region
             * @param beacon
             */
            @Override
            public void onBeaconAppeared(final Region region, final Beacon beacon) {
                showToast(String.format("New beacon in the region: %s", beacon.getName()));
            }

            /**
             * Device has entered monitored region
             * @param region
             */
            @Override
            public void onRegionEntered(final Region region) {
                showToast(String.format("Entered region: %s", region.getIdentifier()));
            }

            /**
             * Device has abandoned monitored region (no Beacons for region encountered)
             * @param region
             */
            @Override
            public void onRegionAbandoned(final Region region) {
                showToast(String.format("Abandoned region: %s", region.getIdentifier()));
            }
        });
    }

    /**
     * Start bluetooth on the device if not started and start monitoring
     * and set regions to be monitored and call listeners.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!beaconManager.isBluetoothEnabled()) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        } else {
            connect();
        }
    }

    /**
     * Stop Beacon monitoring.
     */
    @Override
    protected void onStop() {
        super.onStop();
        beaconManager.stopMonitoring();
    }

    /**
     * Disconnect BeaconManager and give it a null value.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
        beaconManager = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                connect();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
                getActionBar().setSubtitle("Bluetooth not enabled");
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connect() {
        try {
            beaconManager.connect(new OnServiceBoundListener() {
                @Override
                public void onServiceBound() {
                    try {
                        beaconManager.startMonitoring(Region.EVERYWHERE);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    private void displayListOfBeacons(final List<Beacon> beacons) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<String> beaconDetailsList = new ArrayList<String>();
                for (Beacon beacon : beacons) {
                    beaconDetailsList.add(BeaconUtils.getFormattedBeaconDetails(beacon));
                }

                ListAdapter listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, beaconDetailsList);
                lvBeacons.setAdapter(listAdapter);
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
