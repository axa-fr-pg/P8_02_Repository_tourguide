package tourguide.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.model.AttractionNearby;
import tourguide.model.ProviderData;
import tourguide.model.User;
import tourguide.trip.TripController;

@Service
public class TripRequest {

	private Logger logger = LoggerFactory.getLogger(TripRequest.class);
	@Autowired private TripController tripController;

	public List<ProviderData> calculateProposals(User user, List<AttractionNearby> attractions,	int cumulativeRewardPoints) {
		logger.debug("calculateProposals for User " + user.getUserName() 
			+ " with Attraction list of size " + attractions.size()
			+ " and " + cumulativeRewardPoints + "reward points");
		return tripController.calculateProposals(user, attractions, cumulativeRewardPoints);
	}
}
