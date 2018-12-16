package link.mgiannone.githubchallenge.data.repository.remote;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.api.CommitService;
import link.mgiannone.githubchallenge.data.repository.CommitDataSource;
import okhttp3.Headers;
import retrofit2.Response;

public class CommitRemoteDataSource implements CommitDataSource {

	private CommitService commitService;

	@Inject
	public CommitRemoteDataSource(CommitService commitService) {
		this.commitService = commitService;
	}

	@Override
	public Observable<Response<List<Headers>>> countCommits(boolean forceRemote,
															String owner,
															String repoName,
															String accessTokenString,
															String accessTokenTypeString,
															String perPageValue) {
		return commitService.countCommits(owner, repoName, accessTokenString, accessTokenTypeString, perPageValue);
	}

}
