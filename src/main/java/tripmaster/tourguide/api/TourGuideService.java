package tripmaster.tourguide.api;

import java.util.List;
import java.util.Map;

import tripmaster.common.attraction.AttractionNearby;
import tripmaster.common.location.LocationData;
import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.trip.ProviderData;
import tripmaster.common.user.User;
import tripmaster.common.user.UserReward;


public interface TourGuideService {

	final static int NUMBER_OF_PROPOSED_ATTRACTIONS = 5;

	List<UserReward> getUserRewards(User user);

	VisitedLocationData getLastUserLocation(User user);

	Map<String, LocationData> getLastLocationAllUsers();

	List<ProviderData> getTripDeals(User user);

	List<AttractionNearby> getNearbyAttractions(String userName);

}