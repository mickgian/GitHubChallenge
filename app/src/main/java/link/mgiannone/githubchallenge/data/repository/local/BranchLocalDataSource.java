package link.mgiannone.githubchallenge.data.repository.local;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.database.BranchDao;
import link.mgiannone.githubchallenge.data.model.Branch;
import link.mgiannone.githubchallenge.data.repository.BranchDataSource;

public class BranchLocalDataSource implements BranchDataSource {

	private BranchDao branchDao;

	@Inject
	public BranchLocalDataSource(BranchDao branchDao) {
		this.branchDao = branchDao;
	}

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
