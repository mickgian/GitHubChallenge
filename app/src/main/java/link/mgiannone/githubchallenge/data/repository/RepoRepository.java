package link.mgiannone.githubchallenge.data.repository;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import link.mgiannone.githubchallenge.data.model.Repo;

public class RepoRepository implements RepoDataSource {

	private RepoDataSource remoteDataSource;
	private RepoDataSource localDataSource;

	@VisibleForTesting
	List<Repo> caches;

	@Inject
	public RepoRepository(@Local RepoDataSource localDataSource,
						  @Remote RepoDataSource remoteDataSource) {
		this.localDataSource = localDataSource;
		this.remoteDataSource = remoteDataSource;

		caches = new ArrayList<>();
	}

	@Override public Flowable<List<Repo>> loadRepos(boolean forceRemote, String owner) {
		if (forceRemote) {
			return refreshData(owner);
		} else {
			if (caches.size() > 0) {
				// if cache is available, return it immediately
				return Flowable.just(caches);
			} else {
				// else return data from local storage
				return localDataSource.loadRepos(false, owner)
						.flatMap(Flowable::fromIterable)
						.doOnNext(repo -> caches.add(repo))
						.toList()
						.toFlowable()
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
	 * @return the Flowable of newly fetched data.
	 */
	Flowable<List<Repo>> refreshData(String owner) {

		return remoteDataSource.loadRepos(true, owner).doOnNext(list -> {
			// Clear cache
			caches.clear();
			// Clear data in local storage
			localDataSource.clearReposData();
		}).flatMap(Flowable::fromIterable).doOnNext(repo -> {
			caches.add(repo);
			localDataSource.addRepo(repo);
		}).toList().toFlowable();

	}

	/**
	 * Loads a repository by its repository id.
	 *
	 * @param repoId Repo's id.
	 * @return a corresponding Repo from cache.
	 */
	public Flowable<Repo> getRepo(int repoId) {
		return Flowable.fromIterable(caches).filter(repo -> repo.getId() == repoId);
	}



	@Override public void clearReposData() {
		caches.clear();
		localDataSource.clearReposData();
	}
}

