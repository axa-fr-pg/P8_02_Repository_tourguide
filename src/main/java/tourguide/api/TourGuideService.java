package tourguide.api;

import java.util.List;
import java.util.Map;

import tourguide.model.AttractionNearby;
import tourguide.model.LocationData;
import tourguide.model.ProviderData;
import tourguide.model.User;
import tourguide.model.UserReward;
import tourguide.model.VisitedLocationData;

public interface TourGuideService {

	final static int NUMBER_OF_PROPOSED_ATTRACTIONS = 5;

	List<UserReward> getUserRewards(User user);

	VisitedLocationData getLastUserLocation(User user);

	Map<String, LocationData> getLastLocationAllUsers();

	List<ProviderData> getTripDeals(User user);

	List<AttractionNearby> getNearbyAttractions(String userName);

}