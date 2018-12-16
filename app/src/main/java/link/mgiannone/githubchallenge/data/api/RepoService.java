package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Repo;
import okhttp3.Headers;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RepoService {

	//example link with authentication: https://api.github.com/user?access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
	//set to 1 repo per page to check how many repos we have for the user
	@GET("users/{username}/repos")
	Observable<Response<List<Headers>>> checkReposPerUser(@Path("username") String owner,
														  @Query("access_token") String accessTokenString,
														  @Query("token_type") String accessTokenTypeString,
														  @Query("per_page") String perPageValue);

	//set to 100 repo per page
	@GET("users/{username}/repos?per_page=100")
	Observable<List<Repo>> loadRepositories(@Path("username") String owner);

}
