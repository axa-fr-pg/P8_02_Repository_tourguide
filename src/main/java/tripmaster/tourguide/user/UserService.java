package tripmaster.tourguide.user;

import java.util.List;

import tripmaster.common.user.User;

/**
 * Interface for user services
 * @see tripmaster.tourguide.user.UserServiceImpl
 */
public interface UserService {

	User getUser(String userName);

	List<User> getAllUsers();

	void setAllUsers(List<User> userList);

	void addUser(User user);

	void initializeInternalUsers(int expectedNumberOfUsers, boolean withLocationHistory);

}