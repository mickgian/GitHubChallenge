package link.mgiannone.githubchallenge.data.repository;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import link.mgiannone.githubchallenge.AndroidApplication;
import link.mgiannone.githubchallenge.data.Config;
import link.mgiannone.githubchallenge.data.model.AccessToken;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.prefs.PreferencesHelper;
import link.mgiannone.githubchallenge.ui.login.LoginActivity;
import link.mgiannone.githubchallenge.ui.repositories.RepositoriesActivity;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GitHubChallengeRepository implements RepoDataSource, BranchDataSource, AccessTokenDataSource, CommitDataSource, PreferencesHelper {

	private static final String TAG = GitHubChallengeRepository.class.getSimpleName();

	private RepoDataSource remoteRepoDataSource;
	private RepoDataSource localRepoDataSource;

	private BranchDataSource remoteBranchDataSource;
	private AccessTokenDataSource remoteAccessTokenDataSource;
	private CommitDataSource remoteCommitDataSource;

	private PreferencesHelper preferencesHelper;


	@VisibleForTesting
	List<Repo> repoCaches;
	AccessToken accessToken;

	@Inject
	public GitHubChallengeRepository(@Local RepoDataSource localRepoDataSource,
									 @Remote RepoDataSource remoteRepoDataSource,
									 @Remote BranchDataSource remoteBranchDataSource,
									 @Remote CommitDataSource remoteCommitDataSource,
									 @Remote AccessTokenDataSource remoteAccessTokenDataSource,
									 PreferencesHelper preferencesHelper) {
		this.localRepoDataSource = localRepoDataSource;
		this.remoteRepoDataSource = remoteRepoDataSource;
		this.remoteBranchDataSource = remoteBranchDataSource;
		this.remoteAccessTokenDataSource = remoteAccessTokenDataSource;
		this.remoteCommitDataSource = remoteCommitDataSource;
		this.preferencesHelper = preferencesHelper;

		repoCaches = new ArrayList<>();
	}



	//////////////////
	////  REPOS  /////
	//////////////////

	@Override
	public Observable<Response<List<Headers>>> checkReposPerUser(String owner, String accessTokenString, String accessTokenTypeString, String perPageValue) {
		return remoteRepoDataSource.checkReposPerUser(owner, accessTokenString, accessTokenTypeString, perPageValue);
	}

	public Observable<List<Repo>> loadLocalRepos(String owner, String accessTokenString, String accessTokenTypeString, String perPageValue) {

		if (repoCaches.size() > 0) {
			// if cache is available, return it immediately
			return Observable.just(repoCaches);
		} else {
			// else return data from local storage
			return localRepoDataSource.loadLocalRepos(owner, "","","")
					.take(1)
					.flatMap(Observable::fromIterable)
					.doOnNext(repo -> repoCaches.add(repo))
					.toList()
					.toObservable()
					.filter(list -> !list.isEmpty())
					.switchIfEmpty(
							loadRemoteRepos(owner, accessTokenString, accessTokenTypeString, perPageValue)); // If local storage is empty, fetch from remote source instead.
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
	public Observable<List<Repo>> loadRemoteRepos(String owner, String accessTokenString, String accessTokenTypeString, String perPageValue) {

			return remoteRepoDataSource.loadRemoteRepos(owner, accessTokenString, accessTokenTypeString, perPageValue) //getting repositories from GitHub
					.doOnNext(list -> {
						clearCacheAndLocalDB();
					})
					.flatMap(Observable::fromIterable)
					.doOnNext(repo ->  //for each repository, get count of branches asking for them paginated by 1 and checking header response
							remoteBranchDataSource.countBranches(true, owner, repo.getName(), accessTokenString, accessTokenTypeString, "1")
									.subscribe(new Consumer<Response<List<Headers>>>() {
												   @Override
												   public void accept(Response<List<Headers>> response) throws Exception {

													   //getting value 'Link' from response headers
													   String link = response.headers().get("Link");

													   //getting http response code
													   int code = response.code();

													   switch (code){
													   		case 403:
																//Instead of showing an error, we start the login process,
																// store another access token in shared Preferences and resend the same request that failed before,
																Log.d(TAG, "Error 403 when counting branches");
																Intent intentLogin = new Intent();
																intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
																intentLogin.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
																intentLogin.putExtra("owner", owner);
																intentLogin.putExtra("Class", TAG);
																intentLogin.setClass(AndroidApplication.getAppContext(), LoginActivity.class);
																AndroidApplication.getAppContext().startActivity(intentLogin);
														   		break;
														   	case 200:
																if (link == null) {
																	//Link value is not present into the header, it means there's only 1 branch, the master one.
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
																break;
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
					.doOnNext(repo -> {  //for each repository, get count of commits asking for them paginated by 1 and checking header response
						remoteCommitDataSource.countCommits(true, owner, repo.getName(), accessTokenString, accessTokenTypeString, "1")
								.subscribe(new Consumer<Response<List<Headers>>>() {
											   @Override
											   public void accept(Response<List<Headers>> response) throws Exception {

												   //getting value 'Link' from response headers
												   String link = response.headers().get("Link");

												   //getting http response code
												   int code = response.code();

												   switch(code){
													   case 403:
														   //Instead of showing an error, we start the login process,
														   // store another access token in shared Preferences and resend the same request that failed before
														   Log.d(TAG, "Error 403 when counting commits");
														   Intent intentLogin = new Intent();
														   intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
														   intentLogin.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
														   intentLogin.putExtra("owner", owner);
														   intentLogin.putExtra("Class", TAG);
														   intentLogin.setClass(AndroidApplication.getAppContext(), LoginActivity.class);
														   AndroidApplication.getAppContext().startActivity(intentLogin);
													   	break;
													   case 200:

														   if (link == null) {
															   //Link value is not present into the header, it means there's only 1 commit.
															   repo.setCommitsCount(1);
														   }else{
															   //get last page number: considering that we requested all the commits paginated with
															   //only 1 commit per page, the last page number is equal to the total number of commits
															   String totalCommitsString = link.substring(link.lastIndexOf("&page=") + 6, link.lastIndexOf(">"));

															   Log.d(TAG, "Total commits for repository " + repo.getName() + " are " + totalCommitsString);

															   //set commits number into Repo object
															   repo.setCommitsCount(Integer.valueOf(totalCommitsString));
														   }
												   }

											   }
										   }, new Consumer<Throwable>() {
											   @Override
											   public void accept(Throwable throwable) throws Exception {
												   Log.e(TAG, throwable.getMessage(), throwable);
											   }
										   }
								);
					})
					.doOnNext(repo -> {
						addReposToCacheAndLocalDB(repo);
					})
					.toList().toObservable();

	}

	public void clearCacheAndLocalDB() {
		// Clear cache
		repoCaches.clear();
		// Clear data in local storage
		new deleteAllReposAsyncTask(localRepoDataSource).execute();
	}

	public void addReposToCacheAndLocalDB(Repo repo) {
		repoCaches.add(repo); //adding repositories to cache
		localRepoDataSource.addRepo(repo); //adding repositories to local DB
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
	public Observable<Response<List<Headers>>> countBranches(boolean forceRemote, String owner, String repoName, String accessTokenString,
															 String accessTokenTypeString, String perPageValue) {
		return remoteBranchDataSource.countBranches(true, owner, repoName, accessTokenString, accessTokenTypeString, perPageValue);
	}

	////////////////////
	////  COMMITS  /////
	////////////////////

	@Override
	public Observable<Response<List<Headers>>> countCommits(boolean forceRemote, String owner, String repoName, String accessTokenString,
															String accessTokenTypeString, String perPageValue) {
		return remoteCommitDataSource.countCommits(forceRemote, owner, repoName, accessTokenString, accessTokenTypeString, perPageValue);
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

			Call<AccessToken> accessTokenCall = remoteAccessTokenDataSource.getToken(clientId, clientSecret, code);

			accessTokenCall.enqueue(new Callback<AccessToken>() {
				@Override
				public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
					Log.d(TAG, "Token Access succesfully recovered");
					accessToken = response.body();

					//setting access toekn to shared preferences
					setAccessTokenString(accessToken.getAccessToken());
					setAccessTokenType(accessToken.getTokenType());

					//getting temp owner from shared prefrences
					String owner = getCurrentTempOwner();
					if(owner.equalsIgnoreCase("")){
						owner = Config.DEFAULT_OWNER;
					}

					if(accessToken != null){
						Intent intentLogin = new Intent();
						intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intentLogin.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
						intentLogin.putExtra("owner", owner);
						intentLogin.putExtra("Class", TAG);
						intentLogin.setClass(AndroidApplication.getAppContext(), RepositoriesActivity.class);
						AndroidApplication.getAppContext().startActivity(intentLogin);
					}
				}

				@Override
				public void onFailure(Call<AccessToken> call, Throwable t) {
					Log.d(TAG, "Error recovering Token Access");
				}
			});

	}

	@Override
	public String getCurrentTempOwner() {
		return preferencesHelper.getCurrentTempOwner();
	}

	@Override
	public void setCurrentTempOwner(String tempOwner) {
		preferencesHelper.setCurrentTempOwner(tempOwner);
	}

	@Override
	public String getAccessTokenString() {
		return preferencesHelper.getAccessTokenString();
	}

	@Override
	public void setAccessTokenString(String accessTokenString) {
		preferencesHelper.setAccessTokenString(accessTokenString);
	}

	@Override
	public String getAccessTokenType() {
		return preferencesHelper.getAccessTokenType();
	}

	@Override
	public void setAccessTokenType(String accessTokenType) {
		preferencesHelper.setAccessTokenType(accessTokenType);
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

