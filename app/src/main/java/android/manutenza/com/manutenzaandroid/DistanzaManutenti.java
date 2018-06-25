package android.manutenza.com.manutenzaandroid;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by vale- on 15/02/2018.
 */

public class DistanzaManutenti implements Serializable{

    private Location location;
    private int distance;

    public DistanzaManutenti(){

    }

    public DistanzaManutenti(Location location, int distance){
        this.location = location;
        this.distance = distance;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
