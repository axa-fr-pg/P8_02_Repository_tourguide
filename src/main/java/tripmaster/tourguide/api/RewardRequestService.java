package tripmaster.tourguide.api;

import java.util.List;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.user.User;

/**
 * Interface to prepare a reward API request.
 * @see tripmaster.tourguide.api.RewardClient
 * @see tripmaster.tourguide.api.RewardRequestServiceImpl
 */
public interface RewardRequestService {

	List<User> addAllNewRewardsAllUsers(List<User> userList, List<AttractionData> attractions);

	int sumOfAllRewardPoints(User user);

	int getRewardPoints(AttractionData attraction, User user);

}