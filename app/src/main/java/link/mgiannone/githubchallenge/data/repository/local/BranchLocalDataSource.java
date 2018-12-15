package link.mgiannone.githubchallenge.data.repository.local;

import java.util.List;
import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.repository.BranchDataSource;
import okhttp3.Headers;
import retrofit2.Response;

public class BranchLocalDataSource implements BranchDataSource {

	@Override
	public Observable<Response<List<Headers>>> countBranches(boolean forceRemote, String owner, String repoName) {
		return null;
	}
}
