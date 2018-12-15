package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.Headers;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CommitService {

	//we're getting all the commits paginated with 1 commit per page
	@GET("repos/{username}/{repo_name}/commits?per_page=1")
	Observable<Response<List<Headers>>> countCommits(@Path("username") String owner, @Path("repo_name") String repoName);
}
