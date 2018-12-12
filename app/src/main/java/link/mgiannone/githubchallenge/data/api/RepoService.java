package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import io.reactivex.Flowable;
import link.mgiannone.githubchallenge.data.model.Repo;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RepoService {

	@GET("users/{username}/repos")
	Flowable<RepoResponse> loadRepositories(@Path("username") String owner);

}
