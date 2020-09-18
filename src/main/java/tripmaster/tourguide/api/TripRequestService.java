package tripmaster.tourguide.api;

import java.util.List;

import tripmaster.common.attraction.AttractionNearby;
import tripmaster.common.trip.ProviderData;
import tripmaster.common.user.User;

/**
 * Interface to prepare a trip API request.
 * @see tripmaster.tourguide.api.TripClient
 * @see tripmaster.tourguide.api.TripRequestServiceImpl
 */
public interface TripRequestService {

	List<ProviderData> calculateProposals(User user, List<AttractionNearby> attractions, int cumulativeRewardPoints);

}