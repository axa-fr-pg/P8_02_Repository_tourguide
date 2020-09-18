package tripmaster.tourguide.tracker;

/**
 * Interface for tracker services
 * @see tripmaster.tourguide.tracker.TrackerServiceImpl
 */
public interface TrackerService {

	void run();

	void trackAllUsers();

}