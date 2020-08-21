package tourguide.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import tourguide.model.ProposalForm;
import tourguide.model.ProviderData;

@FeignClient(name="trip", url="http://localhost:8080")
public interface TripClient {

	@GetMapping("/calculateProposals")
	List<ProviderData> calculateProposals(@RequestBody ProposalForm proposalForm);
}
