package tourguide.model;

public class AttractionDistance extends AttractionData implements Comparable<AttractionDistance> {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private static final double EARTH_RADIUS_IN_NAUTICAL_MILES = 3440.0647948;

	private LocationData fromLocation;
	
	public AttractionDistance(LocationData fromLocation, AttractionData toAttraction) {
		super(toAttraction.name, toAttraction.city, toAttraction.state, toAttraction.latitude, toAttraction.longitude);
		this.fromLocation = fromLocation;
	}
	
	@Override
	public int compareTo(AttractionDistance that) {
		// Check that we are comparing to the same reference
		if (this.fromLocation.latitude != that.fromLocation.latitude 
				|| this.fromLocation.longitude != that.fromLocation.longitude) {
			throw new RuntimeException("Trying to compare attractions based on different origins");
		}
		double distanceToThis = getDistance(this.fromLocation, new LocationData(this.latitude, this.longitude));
		double distanceToThat = getDistance(that.fromLocation, new LocationData(that.latitude, that.longitude));
		return Double.valueOf(distanceToThis).compareTo(Double.valueOf(distanceToThat));
	}
	
	/**
	 * Calculates distance in statute miles between locations
	 * Uses Spherical Law of Cosines
	 * @param loc1
	 * @param loc2
	 * @return calculated distance
	 */	
	public static double getDistance(LocationData loc1, LocationData loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angleDistance = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMilesDistance = EARTH_RADIUS_IN_NAUTICAL_MILES * angleDistance;
        double statuteMilesDistance = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMilesDistance;
        return statuteMilesDistance;
	}
}
