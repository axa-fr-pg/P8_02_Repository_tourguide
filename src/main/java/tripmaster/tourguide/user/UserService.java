package tripmaster.tourguide.user;

import java.util.List;

import tripmaster.common.user.User;


public interface UserService {

	User getUser(String userName);

	List<User> getAllUsers();

	void setAllUsers(List<User> userList);

	void addUser(User user);

	void initializeInternalUsers(int expectedNumberOfUsers, boolean withLocationHistory);

}