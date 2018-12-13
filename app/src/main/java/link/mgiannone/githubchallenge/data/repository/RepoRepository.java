package link.mgiannone.githubchallenge.data.repository;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Branch;
import link.mgiannone.githubchallenge.data.model.Repo;

public class RepoRepository implements RepoDataSource, BranchDataSource {

	private RepoDataSource remoteRepoDataSource;
	private RepoDataSource localRepoDataSource;
	private BranchDataSource remoteBranchDataSource;

	@VisibleForTesting
	List<Repo> repoCaches;
	List<Branch> branchCaches;

	@Inject
	public RepoRepository(@Local RepoDataSource localRepoDataSource,
						  @Remote RepoDataSource remoteRepoDataSource,
						  @Remote BranchDataSource remoteBranchDataSource) {
		this.localRepoDataSource = localRepoDataSource;
		this.remoteRepoDataSource = remoteRepoDataSource;
		this.remoteBranchDataSource = remoteBranchDataSource;

		repoCaches = new ArrayList<>();
		branchCaches = new ArrayList<>();
	}



	//////////////////
	////  REPOS  /////
	//////////////////

	@Override public Observable<List<Repo>> loadRepos(boolean forceRemote, String owner) {
		if (forceRemote) {
			return refreshData(owner);
		} else {
			if (repoCaches.size() > 0) {
				// if cache is available, return it immediately
				return Observable.just(repoCaches);
			} else {
				// else return data from local storage
				return localRepoDataSource.loadRepos(false, owner)
						.flatMap(Observable::fromIterable)
						.doOnNext(repo -> repoCaches.add(repo))
						.toList()
						.toObservable()
						.filter(list -> !list.isEmpty())
						.switchIfEmpty(
								refreshData(owner)); // If local data is empty, fetch from remote source instead.
			}
		}
	}

	@Override
	public void addRepo(Repo repo) {
		//Currently, we do not need this.
		throw new UnsupportedOperationException("Unsupported operation");
	}

	/**
	 * Fetches data from remote source.
	 * Save it into both local database and cache.
	 *
	 * @return the Observable of newly fetched data.
	 */
	Observable<List<Repo>> refreshData(String owner) {

		return remoteRepoDataSource.loadRepos(true, owner).doOnNext(list -> {
			// Clear cache
			repoCaches.clear();
			// Clear data in local storage
			localRepoDataSource.clearReposData();
		}).flatMap(Observable::fromIterable).doOnNext(repo -> {
			repoCaches.add(repo);
			localRepoDataSource.addRepo(repo);
		}).toList().toObservable();

	}

	/**
	 * Loads a repository by its repository id.
	 *
	 * @param repoId Repo's id.
	 * @return a corresponding Repo from cache.
	 */
	public Observable<Repo> getRepo(int repoId) {
		return Observable.fromIterable(repoCaches).filter(repo -> repo.getId() == repoId);
	}



	@Override public void clearReposData() {
		repoCaches.clear();
		localRepoDataSource.clearReposData();
	}


	/////////////////////
	////  BRANCHES  /////
	/////////////////////

	@Override public Observable<List<Branch>> loadBranches(boolean forceRemote, String owner, String repoName) {
			return refreshBranchData(owner, repoName);
	}

	@Override
	public void addBranch(Branch branch) {
		//Currently, we do not need this.
		throw new UnsupportedOperationException("Unsupported operation");
	}

	@Override
	public void clearBranchesData() {
		branchCaches.clear();
	}

	Observable<List<Branch>> refreshBranchData(String owner, String repoName) {

		return remoteBranchDataSource.loadBranches(true, owner, repoName).doOnNext(list -> {
			// Clear cache
			branchCaches.clear();
			// Clear data in local storage
		}).flatMap(Observable::fromIterable).doOnNext(branch -> {
			branchCaches.add(branch);
		}).toList().toObservable();
	}
}

