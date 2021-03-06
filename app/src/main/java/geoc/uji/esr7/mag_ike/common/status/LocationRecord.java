package geoc.uji.esr7.mag_ike.common.status;


import android.content.res.Resources;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Date;

import geoc.uji.esr7.mag_ike.R;
import geoc.uji.esr7.mag_ike.common.logger.Log;
import geoc.uji.esr7.mag_ike.common.logger.LogRecord;

/**
 * Created by pajarito on 05/05/2017.
 */

public class LocationRecord {

    private LogRecord logRecord;
    private String locationRecord_class;
    private String measurementRecord_class;
    private String device;
    public float no_data;
    private float latitude;
    private float longitude;
    private float altitude;
    private float precision;
    private Date time_gps;
    private Date time_device;
    private float distance;
    private float last_distance;
    private float speed;
    private String device_tag;
    private String latitude_tag;
    private String longitude_tag;
    private String altitude_tag;
    private String precision_tag;
    private String time_gps_tag;
    private String time_device_tag;
    private String measurement_tag;
    private String value_tag;
    private String distance_tag;
    private String last_distance_tag;
    private String speed_tag;


    public LocationRecord(Resources res){

        this.logRecord = LogRecord.getInstance();

        //Setting the parse class names from resources
        this.locationRecord_class = res.getString(R.string.location_class_parse);
        this.measurementRecord_class = res.getString(R.string.measurement_class_parse);

        //Setting all properties to no_data Value

        no_data = res.getInteger(R.integer.value_nodata);
        latitude = longitude = altitude = precision = speed  = no_data;
        time_gps = time_device = new Date();

        //Settings and tags
        device_tag = res.getString(R.string.device_tag);
        latitude_tag = res.getString(R.string.latitude_tag);
        longitude_tag = res.getString(R.string.longitude_tag);
        altitude_tag = res.getString(R.string.altitude_tag);
        precision_tag = res.getString(R.string.precision_tag);
        time_gps_tag = res.getString(R.string.time_gps_tag);
        time_device_tag = res.getString(R.string.time_device_tag);
        measurement_tag = res.getString(R.string.measurement_tag);
        value_tag = res.getString(R.string.value_tag);

        distance_tag = res.getString(R.string.distance_tag);
        last_distance_tag = res.getString(R.string.last_distance_tag);
        speed_tag = res.getString(R.string.speed_tag);
    }


    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        if (latitude != no_data)
            this.latitude = latitude;
    }

    public float getLongitude() { return longitude; }

    public void setLongitude(float longitude) {
        if (longitude != no_data)
            this.longitude = longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        if (altitude != no_data)
            this.altitude = altitude;
    }

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        if (precision != no_data)
            this.precision = precision;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        if (distance != no_data)
            this.distance = distance;
    }

    public float getLast_distance() {
        return last_distance;
    }

    public void setLast_distance(float last_distance) {
        this.last_distance = last_distance;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        if(speed != no_data)
            this.speed = speed;
    }

    public Date getTime_gps() {
        return time_gps;
    }

    public void setTime_gps(Date time_gps) {
        this.time_gps = time_gps;
    }

    public Date getTime_device() {
        return time_device;
    }

    public void setTime_device(Date time_device) {
        this.time_device = time_device;
    }




    public void saveLocation_Eventually(float lat, float lon, float alt, float pre){


        ParseObject parseObjectLocation = new ParseObject(this.locationRecord_class);
        parseObjectLocation.put(device_tag,this.getDevice());
        this.setLatitude(lat);
        parseObjectLocation.put(latitude_tag,this.getLatitude());
        this.setLongitude(lon);
        parseObjectLocation.put(longitude_tag,this.getLongitude());
        this.setAltitude(alt);
        parseObjectLocation.put(altitude_tag,this.getAltitude());
        this.setPrecision((int) pre);
        parseObjectLocation.put(precision_tag,this.getPrecision());
        this.setTime_gps(new Date());
        parseObjectLocation.put(time_gps_tag,this.getTime_gps());

        parseObjectLocation.saveEventually(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("PARSE - LOCATION SAVED OK", String.valueOf(e));
                } else {
                    Log.d("PARSE - SAVE LOCATION FAILED", String.valueOf(e));
                    logRecord.writeLog_Eventually("Location Save error - " + String.valueOf(e));
                }
            }
        });

    }

    public void saveMeasurement_Eventually(final String label, float value){

        ParseObject parseObjectMeasurement = new ParseObject(this.measurementRecord_class);
        parseObjectMeasurement.put(device_tag,this.getDevice());
        this.setTime_device(new Date());
        parseObjectMeasurement.put(time_device_tag,this.getTime_device());
        if (label.equals(speed_tag)) {
            parseObjectMeasurement.put(measurement_tag,speed_tag);
            this.setSpeed(value);
            parseObjectMeasurement.put(value_tag, this.getSpeed());
        } else if (label.equals(distance_tag)){
            parseObjectMeasurement.put(measurement_tag,distance_tag);
            this.setDistance(value);
            parseObjectMeasurement.put(value_tag, this.getDistance());
        } else if (label.equals(last_distance_tag)){
            parseObjectMeasurement.put(measurement_tag,last_distance_tag);
            this.setLast_distance(value);
            parseObjectMeasurement.put(value_tag, this.getLast_distance());
        } else {
            return;
        }
        parseObjectMeasurement.saveEventually(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("PARSE - " + label + " SAVED OK", String.valueOf(e));
                } else {
                    Log.d("PARSE - SAVE " + label + " FAILED", String.valueOf(e));
                    logRecord.writeLog_Eventually("Measurement Save error - " + String.valueOf(e));
                }
            }
        });

    }



}
