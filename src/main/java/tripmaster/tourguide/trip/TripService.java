package tripmaster.tourguide.trip;

import java.util.List;

import tripmaster.common.attraction.AttractionNearby;
import tripmaster.common.trip.ProviderData;
import tripmaster.common.user.User;


public interface TripService {

	final static String TRIP_PRICER_KEY = "test-server-api-key";

	List<ProviderData> calculateProposals(User user, List<AttractionNearby> attractions, int cumulativeRewardPoints);

}