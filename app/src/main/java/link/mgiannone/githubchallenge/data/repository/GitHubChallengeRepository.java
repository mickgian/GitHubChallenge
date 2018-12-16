package link.mgiannone.githubchallenge.data.repository;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import link.mgiannone.githubchallenge.AndroidApplication;
import link.mgiannone.githubchallenge.data.model.AccessToken;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.ui.repositories.RepositoriesActivity;
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
									 @Remote CommitDataSource remoteCommitDataSource,
									 @Remote AccessTokenDataSource remoteAccessTokenDataSource) {
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

	@Override
	public Observable<Response<List<Headers>>> checkReposPerUser(String owner, String accessTokenString, String accessTokenTypeString, String perPageValue) {
		return remoteRepoDataSource.checkReposPerUser(owner, accessTokenString, accessTokenTypeString, perPageValue);
	}

	@Override public Observable<List<Repo>> loadRepos(boolean forceRemote, String owner) {
		if (forceRemote) {
			return refreshData(forceRemote, owner);
		} else {
			if (repoCaches.size() > 0) {
				// if cache is available, return it immediately
				return Observable.just(repoCaches);
			} else {
				// else return data from local storage
				return localRepoDataSource.loadRepos(false, owner)
						.take(1)
						.flatMap(Observable::fromIterable)
						.doOnNext(repo -> repoCaches.add(repo))
						.toList()
						.toObservable()
						.filter(list -> !list.isEmpty())
						.switchIfEmpty(
								refreshData(forceRemote, owner)); // If local data is empty, fetch from remote source instead.
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
	Observable<List<Repo>> refreshData(boolean forceRemote, String owner) {

			if (forceRemote) {
				clearCacheAndLocalDB();
			}

			return remoteRepoDataSource.loadRepos(true, owner)
					.flatMap(Observable::fromIterable)
					.doOnNext(repo ->
							remoteBranchDataSource.countBranches(true, owner, repo.getName())
									.subscribe(new Consumer<Response<List<Headers>>>() {
												   @Override
												   public void accept(Response<List<Headers>> response) throws Exception {

													   //getting value 'Link' from response headers
													   String link = response.headers().get("Link");
													   if (link == null) { //Link value is not present into the header, it means there's only 1 branch, the master one.
														   repo.setBranchesCount(1);
														   Log.d(TAG, "Total branches for repository " + repo.getName() + " is 1.");

													   } else {

														   //get last page number: considering that we requested all the branches paginated with
														   //only 1 branch per page, the last page number is equal to the total number of branches
														   String totalBranchesString = link.substring(link.lastIndexOf("&page=") + 6, link.lastIndexOf(">"));
														   Log.d(TAG, "Total branches for repository " + repo.getName() + " are " + totalBranchesString);

														   //set commits number into Repo object
														   repo.setBranchesCount(Integer.valueOf(totalBranchesString));
													   }
												   }
											   }, new Consumer<Throwable>() {
												   @Override
												   public void accept(Throwable throwable) throws Exception {
													   Log.e(TAG, throwable.getMessage(), throwable);
												   }
											   }

									)
					)
					.doOnNext(repo -> {
						remoteCommitDataSource.countCommits(true, owner, repo.getName())
								.subscribe(new Consumer<Response<List<Headers>>>() {
											   @Override
											   public void accept(Response<List<Headers>> response) throws Exception {

												   //getting value 'Link' from response headers
												   String link = response.headers().get("Link");

												   //get last page number: considering that we requested all the commits paginated with
												   //only 1 commit per page, the last page number is equal to the total number of commits
												   String totalCommitsString = link.substring(link.lastIndexOf("&page=") + 6, link.lastIndexOf(">"));
												   Log.d(TAG, "Total commits for repository " + repo.getName() + " are " + totalCommitsString);

												   //set commits number into Repo object
												   repo.setCommitsCount(Integer.valueOf(totalCommitsString));

											   }
										   }, new Consumer<Throwable>() {
											   @Override
											   public void accept(Throwable throwable) throws Exception {
												   Log.e(TAG, throwable.getMessage(), throwable);
											   }
										   }
								);
					})
					.flatMap(Observable::fromArray).doOnNext(repo -> {
						repoCaches.add(repo);
						localRepoDataSource.addRepo(repo);
					})
					.toList().toObservable();

	}

	private void clearCacheAndLocalDB() {
		// Clear cache
		repoCaches.clear();
		// Clear data in local storage
		new deleteAllReposAsyncTask(localRepoDataSource).execute();
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

	////////////////////
	////  COMMITS  /////
	////////////////////

	@Override
	public Observable<Response<List<Headers>>> countCommits(boolean forceRemote, String owner, String repoName) {
		return remoteCommitDataSource.countCommits(forceRemote, owner, repoName);
	}

	//////////////////
	////  TOKEN  /////
	//////////////////

	@Override
	public Call<AccessToken> getToken(String clientId, String clientSecret, String code){
		Call<AccessToken> accessTokenCall =  remoteAccessTokenDataSource.getToken(clientId, clientSecret, code);
		return accessTokenCall;
	}

	public void recoverAccessToken(String clientId, String clientSecret, String code){

			Call<AccessToken> accessTokenCall = getToken(clientId, clientSecret,code);

			accessTokenCall.enqueue(new Callback<AccessToken>() {
				@Override
				public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
					Log.d(TAG, "Token Access succesfully recovered");
					accessToken = response.body();

					final SharedPreferences prefs = AndroidApplication.getAppContext().getSharedPreferences("access_token", Context.MODE_PRIVATE);
					prefs.edit().putString("oauth.accesstoken", accessToken.getAccesToken()).apply();
					prefs.edit().putString("oauth.tokentype", accessToken.getTokenType()).apply();

					if(accessToken != null){
						Toast.makeText(AndroidApplication.getAppContext(), "Access Token recovered: " + accessToken.getAccesToken(), Toast.LENGTH_SHORT).show();

						Intent intent = new Intent();
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
						intent.setClass(AndroidApplication.getAppContext(), RepositoriesActivity.class);
						AndroidApplication.getAppContext().startActivity(intent);
					}else{
						Toast.makeText(AndroidApplication.getAppContext(), "Access Token not recovered", Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onFailure(Call<AccessToken> call, Throwable t) {
					Log.d(TAG, "Error recovering Token Access");
				}
			});

	}

	private static class deleteAllReposAsyncTask extends AsyncTask<Void, Void, Void> {
		private RepoDataSource asyncTaskLocalRepoDataSource;

		deleteAllReposAsyncTask(RepoDataSource localRepoDataSource) {
			asyncTaskLocalRepoDataSource = localRepoDataSource;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			asyncTaskLocalRepoDataSource.clearReposData();
			return null;
		}
	}
}

