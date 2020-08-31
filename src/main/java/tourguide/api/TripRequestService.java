package tourguide.api;

import java.util.List;

import tourguide.model.AttractionNearby;
import tourguide.model.ProviderData;
import tourguide.model.User;

public interface TripRequestService {

	List<ProviderData> calculateProposals(User user, List<AttractionNearby> attractions, int cumulativeRewardPoints);

}