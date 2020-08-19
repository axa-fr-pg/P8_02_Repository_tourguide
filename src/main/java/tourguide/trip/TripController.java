package tourguide.trip;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourguide.model.AttractionNearby;
import tourguide.model.ProviderData;
import tourguide.model.User;

@Service
public class TripController {

	private Logger logger = LoggerFactory.getLogger(TripController.class);
	@Autowired private TripService tripService;

	public List<ProviderData> calculateProposals(User user, List<AttractionNearby> attractions,	int cumulativeRewardPoints) {
		logger.debug("calculateProposals for User " + user.getUserName() 
			+ " with Attraction list of size " + attractions.size()
			+ " and " + cumulativeRewardPoints + "reward points");
		return tripService.calculateProposals(user, attractions, cumulativeRewardPoints);
	}
}
