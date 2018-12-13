package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.model.Branch;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BranchService {

	@GET("repos/{username}/{repo_name}/branches?per_page=10")
	Observable<List<Branch>> loadBranches(@Path("username") String owner, @Path("repo_name") String repoName);
}
