package link.mgiannone.githubchallenge.data.repository.remote;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import link.mgiannone.githubchallenge.BuildConfig;
import link.mgiannone.githubchallenge.data.api.RepoService;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.RepoDataSource;

public class RepoRemoteDataSource implements RepoDataSource {

	private RepoService repoService;

	@Inject
	public RepoRemoteDataSource(RepoService repoService) {
		this.repoService = repoService;
	}

	@Override
	public Flowable<List<Repo>> loadRepos(boolean forceRemote) {
		return repoService.loadRepositories();
	}

	@Override
	public void addRepo(Repo repo) {
		//Currently, we do not need this for remote source.
		throw new UnsupportedOperationException("Unsupported operation");
	}

	@Override
	public void clearReposData() {
		//Currently, we do not need this for remote source.
		throw new UnsupportedOperationException("Unsupported operation");
	}
}
