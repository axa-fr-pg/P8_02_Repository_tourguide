package tourguide.model;

import gpsUtil.location.Location;

// Added this extended class in order to be able to use jackson for JSON (de)serializing
public class LocationWithEmptyConstructor extends Location {

	public LocationWithEmptyConstructor(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	public LocationWithEmptyConstructor() {
		this(0, 0);
	}
}
