package link.mgiannone.githubchallenge.data.repository;

import java.util.List;
import io.reactivex.Observable;
import okhttp3.Headers;
import retrofit2.Response;

public interface BranchDataSource {

	Observable<Response<List<Headers>>> countBranches(boolean forceRemote,
													  String owner,
													  String repoName,
													  String accessTokenString,
													  String accessTokenTypeString,
													  String perPageValue);

}
