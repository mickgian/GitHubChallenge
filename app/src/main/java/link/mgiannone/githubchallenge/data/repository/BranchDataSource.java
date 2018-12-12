package link.mgiannone.githubchallenge.data.repository;

import java.util.List;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Branch;

public interface BranchDataSource {

	Observable<List<Branch>> loadBranches(boolean forceRemote, String owner, String repoName);

	void addBranch(Branch branch);

	void clearBranchesData();
}
