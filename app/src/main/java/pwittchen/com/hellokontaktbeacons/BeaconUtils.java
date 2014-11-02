package pwittchen.com.hellokontaktbeacons;

import com.kontakt.sdk.android.device.Beacon;

public class BeaconUtils {
    public static String getFormattedBeaconDetails(Beacon beacon) {
        return String.format(
                "name: %s, accuracy: %s, MAC: %s, proximity: %s, proximity UUID: %s, RSSI: %s, " +
                        "timestamp: %s, TxPower: %s, major: %s, minor: %s",
                beacon.getName(),
                String.valueOf(beacon.getAccuracy()),
                beacon.getMacAddress(),
                beacon.getProximity().toString(),
                beacon.getProximityUUID().toString(),
                String.valueOf(beacon.getRssi()),
                String.valueOf(beacon.getTimestamp()),
                String.valueOf(beacon.getTxPower()),
                String.valueOf(beacon.getMajor()),
                String.valueOf(beacon.getMinor()));
    }
}
