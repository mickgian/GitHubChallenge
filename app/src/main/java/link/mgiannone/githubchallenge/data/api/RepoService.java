package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Repo;
import okhttp3.Headers;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RepoService {

	//set to 1 repo per page to check how many repos we have for the user
	@GET("users/{username}/repos?per_page=1")
	Observable<Response<List<Headers>>> checkReposPerUser(@Path("username") String owner);

	//set to 100 repo per page
	@GET("users/{username}/repos?per_page=100")
	Observable<List<Repo>> loadRepositories(@Path("username") String owner);

}
