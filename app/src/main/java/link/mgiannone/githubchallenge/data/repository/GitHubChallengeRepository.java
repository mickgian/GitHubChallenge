package link.mgiannone.githubchallenge.data.repository;

import android.util.Log;
import androidx.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import link.mgiannone.githubchallenge.data.model.AccessToken;
import link.mgiannone.githubchallenge.data.model.Repo;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GitHubChallengeRepository implements RepoDataSource, BranchDataSource, AccessTokenDataSource, CommitDataSource {

	private static final String TAG = GitHubChallengeRepository.class.getSimpleName();

	private RepoDataSource remoteRepoDataSource;
	private RepoDataSource localRepoDataSource;

	private BranchDataSource remoteBranchDataSource;
	private AccessTokenDataSource remoteAccessTokenDataSource;
	private CommitDataSource remoteCommitDataSource;


	@VisibleForTesting
	List<Repo> repoCaches;
	AccessToken accessToken;

	@Inject
	public GitHubChallengeRepository(@Local RepoDataSource localRepoDataSource,
									 @Remote RepoDataSource remoteRepoDataSource,
									 @Remote BranchDataSource remoteBranchDataSource,
									 @Remote AccessTokenDataSource remoteAccessTokenDataSource,
									 @Remote CommitDataSource remoteCommitDataSource) {
		this.localRepoDataSource = localRepoDataSource;
		this.remoteRepoDataSource = remoteRepoDataSource;
		this.remoteBranchDataSource = remoteBranchDataSource;
		this.remoteAccessTokenDataSource = remoteAccessTokenDataSource;
		this.remoteCommitDataSource = remoteCommitDataSource;

		repoCaches = new ArrayList<>();
	}



	//////////////////
	////  REPOS  /////
	//////////////////

	@Override public Observable<List<Repo>> loadRepos(boolean forceRemote, String owner) {
		if (forceRemote) {
			return refreshData(owner);
		} else {
			if (repoCaches.size() > 0) {
				// if cache is available, return it immediately
				return Observable.just(repoCaches);
			} else {
				// else return data from local storage
				return localRepoDataSource.loadRepos(false, owner)
						.flatMap(Observable::fromIterable)
						.doOnNext(repo -> repoCaches.add(repo))
						.toList()
						.toObservable()
						.filter(list -> !list.isEmpty())
						.switchIfEmpty(
								refreshData(owner)); // If local data is empty, fetch from remote source instead.
			}
		}
	}

	@Override
	public void addRepo(Repo repo) {
		//Currently, we do not need this.
		throw new UnsupportedOperationException("Unsupported operation");
	}

	/**
	 * Fetches data from remote source.
	 * Save it into both local database and cache.
	 *
	 * @return the Observable of newly fetched data.
	 */
	Observable<List<Repo>> refreshData(String owner) {

		return remoteRepoDataSource.loadRepos(true, owner)
				.flatMap(Observable::fromIterable)
				.doOnNext(repo ->
						 remoteBranchDataSource.countBranches(true, owner, repo.getName())
								.subscribe(new Consumer<Response<List<Headers>>>() {
									@Override
									public void accept(Response<List<Headers>> response) throws Exception {

										//getting value 'Link' from response headers
										String link = response.headers().get("Link");
										if(link == null){ //Link value is not present into the header, it means there's only 1 branch, the master one.
											repo.setBranchesCount(1);
											Log.d(TAG, "Total branches for repository " + repo.getName() + " is 1.");

										}else {

											//get last page number: considering that we requested all the branches paginated with
											//only 1 branch per page, the last page number is equal to the total number of branches
											String totalBranchesString = link.substring(link.lastIndexOf("&page=") + 6, link.lastIndexOf(">"));
											Log.d(TAG, "Total branches for repository " + repo.getName() + " are " + totalBranchesString);

											//set commits number into Repo object
											repo.setBranchesCount(Integer.valueOf(totalBranchesString));
										}
									}
								}

						)
				).doOnNext(repo -> {
						remoteCommitDataSource.countCommits(true, owner, repo.getName())
								.subscribe(new Consumer<Response<List<Headers>>>() {
											@Override
											public void accept(Response<List<Headers>> response) throws Exception {

												//getting value 'Link' from response headers
												String link = response.headers().get("Link");

												//get last page number: considering that we requested all the commits paginated with
												//only 1 commit per page, the last page number is equal to the total number of commits
												String totalCommitsString = link.substring(link.lastIndexOf("&page=")+6, link.lastIndexOf(">"));
												Log.d(TAG, "Total commits for repository " + repo.getName() + " are " + totalCommitsString);

												//set commits number into Repo object
												repo.setCommitsCount(Integer.valueOf(totalCommitsString));

											}
										}
								);
				}).doOnNext(list -> {
					// Clear cache
					repoCaches.clear();
					// Clear data in local storage
					localRepoDataSource.clearReposData();
				}).doOnNext(repo -> {
					repoCaches.add(repo);
					localRepoDataSource.addRepo(repo);
				}).toList().toObservable();

	}

	/**
	 * Loads a repository by its repository id.
	 *
	 * @param repoId Repo's id.
	 * @return a corresponding Repo from cache.
	 */
	public Observable<Repo> getRepo(int repoId) {
		return Observable.fromIterable(repoCaches).filter(repo -> repo.getId() == repoId);
	}



	@Override public void clearReposData() {
		repoCaches.clear();
		localRepoDataSource.clearReposData();
	}


	/////////////////////
	////  BRANCHES  /////
	/////////////////////

	@Override
	public Observable<Response<List<Headers>>> countBranches(boolean forceRemote, String owner, String repoName) {
		return remoteBranchDataSource.countBranches(true, owner, repoName);
	}

	public AccessToken recoverAccessToken(String clientId, String clientSecret, String code){

		if(accessToken != null){
			return accessToken;
		}else{
			Call<AccessToken> accessTokenCall = getToken(clientId, clientSecret,code);

			accessTokenCall.enqueue(new Callback<AccessToken>() {
			@Override
			public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
				Log.d(TAG, "Token Access succesfully recovered");
				accessToken = response.body();
			}

			@Override
			public void onFailure(Call<AccessToken> call, Throwable t) {
				Log.d(TAG, "Error recovering Token Access");
			}
		});

			return accessToken;
		}
	}

	//////////////////
	////  TOKEN  /////
	//////////////////

	@Override
	public Call<AccessToken> getToken(String clientId, String clientSecret, String code){
		Call<AccessToken> accessTokenCall =  remoteAccessTokenDataSource.getToken(clientId, clientSecret, code);
		return accessTokenCall;
	}

	////////////////////
	////  COMMITS  /////
	////////////////////

	@Override
	public Observable<Response<List<Headers>>> countCommits(boolean forceRemote, String owner, String repoName) {
		return remoteCommitDataSource.countCommits(forceRemote, owner, repoName);
	}
}

