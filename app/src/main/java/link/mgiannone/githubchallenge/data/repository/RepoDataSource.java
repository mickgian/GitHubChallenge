package link.mgiannone.githubchallenge.data.repository;

import java.util.List;

import io.reactivex.Flowable;
import link.mgiannone.githubchallenge.data.model.Repo;

public interface RepoDataSource {

	Flowable<List<Repo>> loadRepos(boolean forceRemote, String owner);

	void addRepo(Repo repo);

	void clearReposData();
}
