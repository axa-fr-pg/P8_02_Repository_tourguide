package tripmaster.tourguide.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import tripmaster.common.attraction.AttractionData;
import tripmaster.common.location.VisitedLocationData;
import tripmaster.common.user.User;

/**
 * Interface to call the gps API.
 * @see tripmaster.gps.GpsController
 *
 */
@FeignClient(name="gps", url="http://localhost:8081")
public interface GpsClient {

	@PatchMapping("/trackAllUserLocations")
	public List<User> trackAllUserLocations(@RequestBody List<User> userList);

	@GetMapping("/getAllAttractions")
	public List<AttractionData> getAllAttractions();

	@GetMapping("/getCurrentUserLocation")
	public VisitedLocationData getCurrentUserLocation(@RequestParam("userId") String userId);
}
