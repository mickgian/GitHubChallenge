package link.mgiannone.githubchallenge.data.repository.remote;

import java.util.List;
import javax.inject.Inject;
import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.api.BranchService;
import link.mgiannone.githubchallenge.data.repository.BranchDataSource;
import okhttp3.Headers;
import retrofit2.Response;

public class BranchRemoteDataSource implements BranchDataSource {

	private BranchService branchService;

	@Inject
	public BranchRemoteDataSource(BranchService branchService) {
		this.branchService = branchService;
	}

	@Override
	public Observable<Response<List<Headers>>> countBranches(boolean forceRemote, String owner, String repoName) {
		return branchService.countBranches(owner, repoName);
	}
}
