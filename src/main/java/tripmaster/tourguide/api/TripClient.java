package tripmaster.tourguide.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tripmaster.common.trip.ProposalForm;
import tripmaster.common.trip.ProviderData;

/**
 * Interface to call the trip API.
 * @see tripmaster.trip.TripController
 *
 */
@FeignClient(name="trip", url="http://localhost:8083")
public interface TripClient {

	@GetMapping("/calculateProposals")
	List<ProviderData> calculateProposals(@RequestBody ProposalForm proposalForm);
}
