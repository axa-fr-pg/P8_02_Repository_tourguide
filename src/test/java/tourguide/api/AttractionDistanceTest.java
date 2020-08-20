package tourguide.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tourguide.model.AttractionDistance;
import tourguide.model.LocationData;

public class AttractionDistanceTest {

	@Test
	public void givenTwoLocations_whenGetDistance_thenReturnsCorrectDistance() {
		// GIVEN
		LocationData parisLocation = new LocationData(48.8534, 2.3488);
		LocationData londonLocation = new LocationData(51.5084, -0.1255);
		// WHEN
		double distance = AttractionDistance.getDistance(parisLocation, londonLocation);
		// THEN
		assertEquals(213, distance, 1);
	}		
}
