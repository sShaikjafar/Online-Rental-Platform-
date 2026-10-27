package com.rental.platform.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocationService {
    
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }

    public Map<String, Double> geocodeAddress(String address) {
        // Simulate geocoding - integrate with actual geocoding service like Google Maps API
        Map<String, Double> coordinates = new HashMap<>();
        
        // Default coordinates for demo (Hyderabad, India)
        coordinates.put("latitude", 17.3850);
        coordinates.put("longitude", 78.4867);
        
        return coordinates;
    }
}