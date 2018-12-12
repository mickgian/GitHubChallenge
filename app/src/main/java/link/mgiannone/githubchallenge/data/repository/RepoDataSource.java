package link.mgiannone.githubchallenge.data.repository;

import java.util.List;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Repo;

public interface RepoDataSource {

	Observable<List<Repo>> loadRepos(boolean forceRemote, String owner);

	void addRepo(Repo repo);

	void clearReposData();
}
