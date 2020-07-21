package tourguide.userservice;

public class UserInternalNumber {

	private static int internalUserNumber = 100;
	
	public static void set(int internalUserNumber) {
		UserInternalNumber.internalUserNumber = internalUserNumber;
	}
	
	public static int get() {
		return internalUserNumber;
	}
}
