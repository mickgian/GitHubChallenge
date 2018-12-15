package link.mgiannone.githubchallenge.data.api;

import java.util.List;
import io.reactivex.Observable;
import okhttp3.Headers;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BranchService {

	//we're getting all the branches paginated with 1 commit per page
	@GET("repos/{username}/{repo_name}/branches?per_page=1")
	Observable<Response<List<Headers>>> countBranches(@Path("username") String owner, @Path("repo_name") String repoName);
}
