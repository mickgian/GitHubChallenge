package link.mgiannone.githubchallenge.data.repository.remote;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.api.BranchService;
import link.mgiannone.githubchallenge.data.model.Branch;
import link.mgiannone.githubchallenge.data.repository.BranchDataSource;

public class BranchRemoteDataSource implements BranchDataSource {

	private BranchService branchService;

	@Inject
	public BranchRemoteDataSource(BranchService branchService) {
		this.branchService = branchService;
	}

	@Override
	public Observable<List<Branch>> loadBranches(boolean forceRemote, String owner, String repoName) {
		return branchService.loadBranches(owner, repoName);
	}

	@Override
	public void addBranch(Branch branch) {
		//Currently, we do not need this for remote source.
		throw new UnsupportedOperationException("Unsupported operation");
	}

	@Override
	public void clearBranchesData() {
		//Currently, we do not need this for remote source.
		throw new UnsupportedOperationException("Unsupported operation");
	}
}
