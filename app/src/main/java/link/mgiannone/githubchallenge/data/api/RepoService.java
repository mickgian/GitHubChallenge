package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import io.reactivex.Flowable;
import link.mgiannone.githubchallenge.data.model.Repo;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RepoService {

	@GET("users/{username}/repos?per_page=100")
	Flowable<List<Repo>> loadRepositories(@Path("username") String owner);

}
