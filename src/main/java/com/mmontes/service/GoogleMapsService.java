package com.mmontes.service;

import com.amazonaws.util.json.JSONObject;
import com.mmontes.util.Constants;
import com.mmontes.util.JSONParser;
import com.mmontes.util.exception.GoogleMapsServiceException;
import com.vividsolutions.jts.geom.Coordinate;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

@Service("GoogleMapsService")
public class GoogleMapsService {

    public GoogleMapsService() {
    }

    public String getTIPGoogleMapsUrl(Coordinate coordinate){
        return "http://maps.google.com/maps?q=loc:"+coordinate.y+","+coordinate.x;
    }

    public String getAddress(Coordinate coordinate) throws Exception {
        String requestUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + coordinate.y + "," + coordinate.x + "&key=" + Constants.GOOGLE_MAPS_KEY;

        URL url = new URL(requestUrl);
        HttpURLConnection connnection = (HttpURLConnection) url.openConnection();
        connnection.setRequestMethod("GET");

        int responseCode = connnection.getResponseCode();
        if (responseCode >= 400) {
            throw new GoogleMapsServiceException();
        }

        JSONObject obj = JSONParser.parseJSON(connnection.getInputStream());

        return obj.getJSONArray("results").getJSONObject(0).getString("formatted_address");
    }
}
