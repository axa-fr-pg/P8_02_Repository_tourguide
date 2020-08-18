package tourguide.gps;

import gpsUtil.location.Attraction;

/**
 * Extension of the Attraction class provided in the read only gpsUtil library 
 * Required for use with the Jackson object mapper
 * 
 */
public class AttractionWithEmptyConstructor extends Attraction {

	public AttractionWithEmptyConstructor(String name, String city, String state, double latitude, double longitude) {
		super(name, city, state, latitude, longitude);
	}

	public AttractionWithEmptyConstructor() {
		this(new String(), new String(), new String(), 0, 0);
	}
}
