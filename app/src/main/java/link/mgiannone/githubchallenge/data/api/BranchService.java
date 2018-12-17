package link.mgiannone.githubchallenge.data.api;

import java.util.List;
import io.reactivex.Observable;
import okhttp3.Headers;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BranchService {

	//we're getting all the branches paginated with 1 commit per page (perPageValue parameter will be 1)
	@GET("repos/{username}/{repo_name}/branches")
	Observable<Response<List<Headers>>> countBranches(@Path("username") String owner,
													  @Path("repo_name") String repoName,
													  @Query("access_token") String accessTokenString,
													  @Query("token_type") String accessTokenTypeString,
													  @Query("per_page") String perPageValue);
}
