package link.mgiannone.githubchallenge.data.repository.local;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import link.mgiannone.githubchallenge.data.database.RepoDao;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.RepoDataSource;

public class RepoLocalDataSource implements RepoDataSource {

	private RepoDao repoDao;

	@Inject
	public RepoLocalDataSource(RepoDao repoDao) {
		this.repoDao = repoDao;
	}

	@Override
	public Flowable<List<Repo>> loadRepos(boolean forceRemote, String owner) {
		return repoDao.getAllRepositories();
	}

	@Override
	public void addRepo(Repo repo) {

	}

	@Override
	public void clearReposData() {
		repoDao.deleteAll();
	}
}
