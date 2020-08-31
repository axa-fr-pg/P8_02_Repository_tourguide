package tripmaster.tourguide.api;

import java.util.List;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.user.User;


public interface RewardRequestService {

	List<User> addAllNewRewardsAllUsers(List<User> userList, List<AttractionData> attractions);

	int sumOfAllRewardPoints(User user);

	int getRewardPoints(AttractionData attraction, User user);

}