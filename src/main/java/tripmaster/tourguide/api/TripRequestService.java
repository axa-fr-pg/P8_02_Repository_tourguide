package tripmaster.tourguide.api;

import java.util.List;

import tripmaster.common.attraction.AttractionNearby;
import tripmaster.common.trip.ProviderData;
import tripmaster.common.user.User;


public interface TripRequestService {

	List<ProviderData> calculateProposals(User user, List<AttractionNearby> attractions, int cumulativeRewardPoints);

}