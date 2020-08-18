package tourguide.gps;

import gpsUtil.location.Location;

/**
 * Extension of the Location class provided in the read only gpsUtil library 
 * Required for use with the Jackson object mapper
 * 
 */
public class LocationWithEmptyConstructor extends Location {

	public LocationWithEmptyConstructor(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	public LocationWithEmptyConstructor() {
		this(0, 0);
	}
}
