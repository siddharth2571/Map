package gallery.sd.map;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomMapPathDraw {


    LatLng StartingPoint;
    LatLng EndingPoint;

    GoogleMap mMap;
    int width = 5;
    int color = Color.GREEN;
    List<LatLng> mMarkerPoints;

    Boolean check = true;

    public static String distance;
    public static String duration;


    int sizeList = 1;

    public CustomMapPathDraw(GoogleMap gMap, LatLng startingPoint, LatLng endingPoint) {

        mMarkerPoints = new ArrayList<>();
        this.StartingPoint = startingPoint;
        this.EndingPoint = endingPoint;
        this.mMap = gMap;

        try {
            String url = getDirectionsUrl(StartingPoint, EndingPoint);

            DownloadTask downloadTask = new DownloadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public CustomMapPathDraw(GoogleMap mMap, List<LatLng> city) {
        mMarkerPoints = new ArrayList<>();
        this.mMap = mMap;
        this.mMarkerPoints = city;

        setListMaker(city);
        try {
            String url = getDirectionsUrl(city.get(0), city.get(city.size() - 1));

            DownloadTask downloadTask = new DownloadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setListMaker(List<LatLng> list) {
        int firstInd = 0;
        int lastInd = (list.size() - 1);

        if (check) {
            int i = 0;
            for (LatLng l : list) {
                if (firstInd == i || lastInd == i) {
                    drawMarker(list.get(i), 1);
                } else {
                    drawMarker(list.get(i), 2);
                }

                i++;
            }
        }
//        sizeList = mMarkerPoints.size();
    }


    //------------------------------- Marking at both side

    public void drawMarkerAtPoint(Boolean check) {
        if (check) {
            this.check = check;
            drawMarker(StartingPoint, 1);
            drawMarker(EndingPoint, 2);
        }
    }


    //-------------------------- draw path with different Color ---------------------------
    public void drawPathWidthColor(Integer width, Integer color) {
        this.width = width;
        this.color = color;

    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.i("UrlRequest", "=>" + url);

        return url;
    }

    //-------------------------- Map Data Download Async---------------------------------

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    //------------------------- Method For Download Data ----------------------------

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloadinl", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    //------------------------------- Parshe the  All the json Data ------------------------------

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                MapsJsonDataParser parser = new MapsJsonDataParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    Log.e("latLng", "=>" + position);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(width);
                lineOptions.color(color);

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            Log.d("Line", "Gmap");

        }
    }

    //------------------------- Set Marker -------------------------------------

    //---------------------------------- Draw Maker Point --------------------------------------

    private void drawMarker(LatLng point, int color) {


        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        Log.d("mMarkerSize", "=>" + sizeList);

        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (color == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        if (check) {
            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);
        }
    }

}


class MapsJsonDataParser {

    /**
     * Receives a JSONObject and returns a list of lists containing latitude and longitude
     */
    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");
            Log.v("route", "=>" + jRoutes);

            try {
                JSONObject routeObject = jObject.getJSONObject("routes");
                JSONObject legsObject = routeObject.getJSONObject("legs");
                JSONObject durationObject = legsObject.getJSONObject("duration");
                JSONObject dictanceObject = legsObject.getJSONObject("distance");

                CustomMapPathDraw.duration = durationObject.optString("text");
                CustomMapPathDraw.distance = dictanceObject.getString("text");

                MapsActivity.distance.setText("DISTANCE " + CustomMapPathDraw.distance.toString());
                MapsActivity.duration.setText("DURATION " + CustomMapPathDraw.duration.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }


        return routes;
    }


    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}
