package link.mgiannone.githubchallenge.data.repository.local;

import java.util.List;


import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Branch;
import link.mgiannone.githubchallenge.data.repository.BranchDataSource;

public class BranchLocalDataSource implements BranchDataSource {

	@Override
	public Observable<List<Branch>> loadBranches(boolean forceRemote, String owner, String repoName) {
		return null;
	}

	@Override
	public void addBranch(Branch branch) {

	}

	@Override
	public void clearBranchesData() {

	}
}
