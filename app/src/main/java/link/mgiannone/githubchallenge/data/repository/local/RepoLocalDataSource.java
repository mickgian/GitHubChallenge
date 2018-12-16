package link.mgiannone.githubchallenge.data.repository.local;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.database.RepoDao;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.RepoDataSource;
import okhttp3.Headers;
import retrofit2.Response;

public class RepoLocalDataSource implements RepoDataSource {

	private RepoDao repoDao;

	@Inject
	public RepoLocalDataSource(RepoDao repoDao) {
		this.repoDao = repoDao;
	}

	@Override
	public Observable<Response<List<Headers>>> checkReposPerUser(String owner, String accessTokenString, String accessTokenTypeString, String perPageValue) {
		return null;
	}

	@Override
	public Observable<List<Repo>> loadRepos(boolean forceRemote, String owner) {
		return repoDao.getAllRepositories();
	}

	@Override
	public void addRepo(Repo repo) {
		repoDao.insert(repo);
	}

	@Override
	public void clearReposData() {
		repoDao.deleteAll();
	}
}
