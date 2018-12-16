package link.mgiannone.githubchallenge.data.repository;

import java.util.List;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Repo;
import okhttp3.Headers;
import retrofit2.Response;

public interface RepoDataSource {

	Observable<Response<List<Headers>>> checkReposPerUser(String owner, String accessTokenString, String accessTokenTypeString, String perPageValue);

	Observable<List<Repo>> loadRepos(boolean forceRemote, String owner);

	void addRepo(Repo repo);

	void clearReposData();
}
