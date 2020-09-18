package tripmaster.tourguide.api;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tripmaster.common.trip.ProposalForm;
import tripmaster.common.trip.ProviderData;
import tripmaster.common.user.User;
import tripmaster.common.attraction.AttractionNearby;

/**
 * Class to prepare a trip API request. Implements TripRequestService interface.
 * @see tripmaster.tourguide.api.TripClient
 * @see tripmaster.tourguide.api.TripRequestServiceImpl
 */
@Service
public class TripRequestServiceImpl implements TripRequestService {

	private Logger logger = LoggerFactory.getLogger(TripRequestServiceImpl.class);
	private final TripClient tripClient;
	@Autowired private ObjectMapper objectMapper;

	public TripRequestServiceImpl(TripClient tripClient) {
		this.tripClient = tripClient;
	}
	
	/**
	 * Gets the proposed trips for a given user, attractions and reward points combination.
	 * @param user for whom the proposals shall be computed (based on his preferences).
	 * @param attractions the list of AttractionNearby to be parsed for the trip proposals.
	 * @param cumulativeRewardPoints the number of reward points to be taken into account for pricing.
	 * @return List of ProviderData proposed for the user (name, price and id).
	 */
	@Override
	public List<ProviderData> calculateProposals(User user, List<AttractionNearby> attractions,	int cumulativeRewardPoints) {
		logListContent("calculateProposals before external call", Collections.singletonList(user));
		logListContent("calculateProposals before external call", attractions);
		logListContent("calculateProposals before external call", Collections.singletonList(cumulativeRewardPoints));
		ProposalForm proposalForm = new ProposalForm(user, attractions, cumulativeRewardPoints);
		List<ProviderData> proposals = tripClient.calculateProposals(proposalForm);
		logListContent("calculateProposals after external call", proposals);
		return proposals;
	}
	
	// logging helper tool
	private void logListContent(String methodName, List<?> list) {
		logger.debug(methodName + " number of elements " + list.size() + " : " + list.toString());
		try {
			logger.debug(methodName + " content details : " + objectMapper.writeValueAsString(list));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("logListContent catched a JsonProcessingException");
		}
	}
}
