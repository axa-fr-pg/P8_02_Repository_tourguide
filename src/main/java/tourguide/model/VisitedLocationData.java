package tourguide.model;

import java.util.Date;
import java.util.UUID;

import gpsUtil.location.VisitedLocation;

public class VisitedLocationData {
	public UUID userId;
	public LocationData location;
	public Date timeVisited;

	public VisitedLocationData(UUID givenUserId, LocationData givenLocation, Date givenTimeVisited) {
		userId = givenUserId;
		location = givenLocation;
		timeVisited = givenTimeVisited;
	}
	
	public VisitedLocationData() {		
	}
	
	public static VisitedLocation newVisitedLocation(VisitedLocationData visitedLocationData) {
		return new VisitedLocation(visitedLocationData.userId, LocationData.newLocation(visitedLocationData.location),
				visitedLocationData.timeVisited);
	}

	public static VisitedLocationData newVisitedLocationData(VisitedLocation visitedLocation) {
		return new VisitedLocationData(visitedLocation.userId, 
				LocationData.newLocationData(visitedLocation.location), visitedLocation.timeVisited);
	}

}
