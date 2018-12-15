package link.mgiannone.githubchallenge.data.repository;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.Headers;
import retrofit2.Response;

public interface CommitDataSource {

	Observable<Response<List<Headers>>> countCommits(boolean forceRemote, String owner, String repoName);

}
