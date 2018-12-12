package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Repo;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RepoService {

	@GET("users/{username}/repos?per_page=100")
	Observable<List<Repo>> loadRepositories(@Path("username") String owner);

}
