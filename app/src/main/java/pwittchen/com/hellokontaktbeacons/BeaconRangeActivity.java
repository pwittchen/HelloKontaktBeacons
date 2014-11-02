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

import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.Beacon;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.manager.BeaconManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Please note: This class is not working properly.
 * It requires further investigation.
 * TODO: to be debugged and fixed
 */
public class BeaconRangeActivity extends Activity {

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;
    private BeaconManager beaconManager;
    private Context context;
    private ListView lvBeacons;

    /**
     * Set the beaconManger configuration and registers the Ranging listener.
     *
     * @param savedInstanceState bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.a_beacon_range_activity);
        lvBeacons = (ListView) findViewById(R.id.lv_beacons);
        beaconManager = BeaconManager.newInstance(this);
        beaconManager.registerRangingListener(new BeaconManager.RangingListener() {
            /**
             * List of Beacons that are in range.
             * @param region
             * @param beacons
             */
            @Override
            public void onBeaconsDiscovered(final Region region, final List<Beacon> beacons) {
                displayBeacons(beacons);
            }
        });
    }

    /**
     * Start bluetooth on the device if not started and start ranging.
     * Set regions to be ranged and call listeners.
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
     * Stop Beacon ranging.
     */
    @Override
    protected void onStop() {
        super.onStop();
        beaconManager.stopRanging();
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
                public void onServiceBound() throws RemoteException {
                    beaconManager.startRanging(Region.EVERYWHERE);
                }
            });
        } catch (RemoteException e) {

        }
    }

    private void displayBeacons(final List<Beacon> beacons) {
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
}