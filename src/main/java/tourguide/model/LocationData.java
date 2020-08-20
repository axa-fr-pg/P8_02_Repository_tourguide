package tourguide.model;

import gpsUtil.location.Location;

public class LocationData {
	public double latitude;
	public double longitude;
	
	public LocationData(double lat, double lon) {
		latitude = lat;
		longitude = lon;
	}
	
	public LocationData() {
	}
	
	public static LocationData newLocationData(Location location) {
		return new LocationData(location.latitude, location.longitude);
	}
	
	public static Location newLocation(LocationData locationData) {
		return new Location(locationData.latitude, locationData.longitude);
	}
}
