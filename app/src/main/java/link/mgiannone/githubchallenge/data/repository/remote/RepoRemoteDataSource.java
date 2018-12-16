package link.mgiannone.githubchallenge.data.repository.remote;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.api.RepoService;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.RepoDataSource;
import okhttp3.Headers;
import retrofit2.Response;

public class RepoRemoteDataSource implements RepoDataSource {

	private RepoService repoService;

	@Inject
	public RepoRemoteDataSource(RepoService repoService) {
		this.repoService = repoService;
	}

	@Override
	public Observable<Response<List<Headers>>> checkReposPerUser(String owner, String accessTokenString, String accessTokenTypeString, String perPageValue) {
		return repoService.checkReposPerUser(owner, accessTokenString, accessTokenTypeString, perPageValue);
	}

	@Override
	public Observable<List<Repo>> loadRepos(boolean forceRemote, String owner) {
		return repoService.loadRepositories(owner);
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
