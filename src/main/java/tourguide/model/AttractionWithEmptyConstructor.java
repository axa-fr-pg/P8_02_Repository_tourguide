package tourguide.model;

import gpsUtil.location.Attraction;

public class AttractionWithEmptyConstructor extends Attraction {

	public AttractionWithEmptyConstructor(String attractionName, String city, String state, 
											double latitude, double longitude) {
		super(attractionName, city, state, latitude, longitude);
	}

	public AttractionWithEmptyConstructor() {
		this(new String(), new String(), new String(), 0, 0);
	}
}
