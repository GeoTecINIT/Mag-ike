package geoc.uji.esr7.mag_ike.common.status;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ExpandableListAdapter;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import geoc.uji.esr7.mag_ike.R;
import geoc.uji.esr7.mag_ike.SessionActivity;

/**
 * Created by pajarito on 22/07/2017.
 */

public class LeaderBoardStatus {

    private ParseObject parseObject;
    private Resources resources;
    private SessionActivity activity;
    private String trip_parse_class;
    private String device_tag;
    private String device;
    private String trip_tag;
    private boolean retrive_status;
    private int own_trips;
    private int total_trips;
    private int position_trips;
    private int own_tags;
    private int total_tags;
    private int position_tags;
    private TopPlayer[] top3Trips = new TopPlayer[3];
    private TopPlayer[] top3Tags = new  TopPlayer[3];

    public LeaderBoardStatus(Resources res){
        resources = res;
        trip_parse_class = res.getString(R.string.trip_class_parse);
        device_tag = res.getString(R.string.device_tag);
        trip_tag = res.getString(R.string.trip_counter_tag);
        retrive_status = false;
    }

    public int getOwn_trips() {
        return own_trips;
    }

    public void setOwn_trips(int own_trips) {
        this.own_trips = own_trips;
    }

    public int getTotal_trips() {
        return total_trips;
    }

    public void setTotal_trips(int total_trips) {
        this.total_trips = total_trips;
    }

    public int getPosition_trips() {
        return position_trips;
    }

    public void setPosition_trips(int position_trips) {
        this.position_trips = position_trips;
    }

    public int getOwn_tags() {
        return own_tags;
    }

    public void setOwn_tags(int own_tags) {
        this.own_tags = own_tags;
    }

    public int getTotal_tags() {
        return total_tags;
    }

    public void setTotal_tags(int total_tags) {
        this.total_tags = total_tags;
    }

    public int getPosition_tags() {
        return position_tags;
    }

    public void setPosition_tags(int position_tags) {
        this.position_tags = position_tags;
    }

    public ArrayList getTop3TripsList(){

        ArrayList top = new ArrayList();
        for (int i = 0; i < 3; i++) {
            if (top3Trips[i] != null){
                top.add(top3Trips[i].getAvatar());
            }
        }
        return top;

    }
 /*
    public ExpandableListAdapter getTop3TripsListAdapter(){
        ExpandableListAdapter listAdapter = new List
    }
*/

    public void updateLeaderboard(SessionActivity act){
        activity = act;
        device = act.gameStatus.getDevice();
        getLeaderBoardFromServer();
    }

    private void getPositionOnLeaderBoardTrips(final int ownTrips){
        String[] ownTripsArray = { Integer.toString(ownTrips + 1) };
        ParseQuery<ParseObject> query = ParseQuery.getQuery(trip_parse_class);
        query.whereEqualTo(trip_tag, ownTrips+1);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    setPosition_trips(count + 1);
                    activity.updateLeaderBoard();
                }
            }
        });
    }

    private void getLeaderBoardFromServer(){

        ParseQuery<ParseObject> query;
        String[] deviceArray = {device};


        /**
         * Leader board for trips
         */

        // Own Trips
        query = ParseQuery.getQuery(trip_parse_class);
        query.whereContainedIn(device_tag, Arrays.asList(deviceArray));
        query.orderByDescending(trip_tag);
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    setOwn_trips( scoreList.get(0).getInt(trip_tag));
                    getPositionOnLeaderBoardTrips(getOwn_trips());
                } else {
                    Log.d("Cyclist", "Error: " + e.getMessage());
                }
            }
        });

        // Total Trips
        query = ParseQuery.getQuery(trip_parse_class);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    setTotal_trips(count);
                    activity.updateLeaderBoard();
                } else {
                    Log.d("Cyclist", "Error: " + e.getMessage());
                }
            }
        });



        // Top 3 Trips
        final ParseQuery<ParseObject> queryTop = ParseQuery.getQuery(trip_parse_class);
        queryTop.addDescendingOrder(trip_tag);
        queryTop.setLimit(1);
        queryTop.findInBackground(new FindCallback<ParseObject>(){
            public void done(List<ParseObject> results, ParseException e){
                if (e == null && results.size() > 0) {
                    String avatar =  (String) results.get(0).get(device_tag);
                    int val = results.get(0).getInt(trip_tag) ;
                    top3Trips[0] = new TopPlayer(avatar, val);
                    String[] top1 = {top3Trips[0].getAvatar()};
                    ParseQuery<ParseObject> queryTop = ParseQuery.getQuery(trip_parse_class);
                    queryTop.whereNotContainedIn(device_tag, Arrays.asList(top1));
                    queryTop.addDescendingOrder(trip_tag);
                    queryTop.setLimit(1);
                    queryTop.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> results, ParseException e) {
                            if (e == null && results.size() > 0) {
                                String avatar =  (String) results.get(0).get(device_tag);
                                int val = (int) results.get(0).get(trip_tag) ;
                                top3Trips[1] = new TopPlayer(avatar, val);
                                String[] top2 = {top3Trips[0].getAvatar(), top3Trips[1].getAvatar()};
                                ParseQuery<ParseObject> queryTop = ParseQuery.getQuery(trip_parse_class);
                                queryTop.whereNotContainedIn(device_tag, Arrays.asList(top2));
                                queryTop.addDescendingOrder(trip_tag);
                                queryTop.setLimit(1);
                                queryTop.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> results, ParseException e) {
                                        if (e == null && results.size()>0 ){
                                            String avatar =  (String) results.get(0).get(device_tag);
                                            int val = (int) results.get(0).get(trip_tag) ;
                                            top3Trips[2] = new TopPlayer(avatar, val);
                                            activity.updateLeaderBoard();
                                        } else {
                                            Log.d("Cyclist", "Error: " + e.getMessage());
                                        }
                                    }
                                });
                                activity.updateLeaderBoard();
                            } else {
                                Log.d("Cyclist", "Error: " + e.getMessage());
                            }
                        }
                    });
                    activity.updateLeaderBoard();
                } else {
                    Log.d("Cyclist", "Error: " + e.getMessage());
                }

            }

        });

    }


}
