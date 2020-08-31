package tourguide.user;

import java.util.List;

import tourguide.model.User;

public interface UserService {

	User getUser(String userName);

	List<User> getAllUsers();

	void setAllUsers(List<User> userList);

	void addUser(User user);

	void initializeInternalUsers(int expectedNumberOfUsers, boolean withLocationHistory);

}