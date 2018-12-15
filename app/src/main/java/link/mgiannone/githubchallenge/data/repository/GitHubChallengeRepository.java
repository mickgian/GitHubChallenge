package link.mgiannone.githubchallenge.data.repository;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import link.mgiannone.githubchallenge.data.model.AccessToken;
import link.mgiannone.githubchallenge.data.model.Branch;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.remote.CommitRemoteDataSource;
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
	List<Branch> branchCaches;
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
		branchCaches = new ArrayList<>();
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

		return remoteRepoDataSource.loadRepos(true, owner).doOnNext(list -> {
			// Clear cache
			repoCaches.clear();
			// Clear data in local storage
			localRepoDataSource.clearReposData();
		}).flatMap(Observable::fromIterable).doOnNext(repo -> {
//			repoCaches.add(repo);
//			localRepoDataSource.addRepo(repo);
		}).toList().toObservable();

	}

//	Observable<List<Repo>> refreshData2(String owner) {
//
//		return remoteRepoDataSource.loadRepos(true, owner)
//				.flatMap(Observable::fromIterable)
//				.doOnNext(repo ->
//						 remoteBranchDataSource.loadBranches(true, owner, repo.getName())
//						 .flatMap(Observable::fromIterable)
////						 .toList()
////						 .subscribe(branches -> {
////							repo.setBranchList(branches);
////						 })
//						.doOnNext(branch -> library.getAuthors().add(branch))
//						.subscribe()
//				).doOnNext(repo -> {
//						remoteCommitDataSource.countCommits(true, owner, repo.getName())
//								.subscribe(
//										new Consumer<Response<List<Headers>>>() {
//											@Override
//											public void accept(Response<List<Headers>> response) throws Exception {
//
//												//getting value 'Link' from response headers
//												String link = response.headers().get("Link");
//												Log.d("RepositoriesPresenter", "Header Link: " + link);
//
//												//get last page number: considering that we requested all the commits paginated with
//												//only 1 commit per page, the last page number is equal to the total number of commits
//												String totalCommitsString = link.substring(link.lastIndexOf("&page=")+6, link.lastIndexOf(">"));
//												Log.d("RepositoriesPresenter", "Total commit: " + totalCommitsString);
//
//												//set commits number into Repo object
//												repo.setCommitsCount(Integer.valueOf(totalCommitsString));
//
//											}
//										}
//								);
//				}).doOnNext(list -> {
//					// Clear cache
//					repoCaches.clear();
//					// Clear data in local storage
//					localRepoDataSource.clearReposData();
//				}).flatMap(Observable::fromIterable).doOnNext(repo -> {
////					repoCaches.add(repo);
////					localRepoDataSource.addRepo(repo);
//				}).toList().toObservable();
//
//	}

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

	@Override public Observable<List<Branch>> loadBranches(boolean forceRemote, String owner, String repoName) {
			return refreshBranchData(owner, repoName);
	}

	@Override
	public void addBranch(Branch branch) {
		//Currently, we do not need this.
		throw new UnsupportedOperationException("Unsupported operation");
	}

	@Override
	public void clearBranchesData() {
		branchCaches.clear();
	}

	Observable<List<Branch>> refreshBranchData(String owner, String repoName) {

		return remoteBranchDataSource.loadBranches(true, owner, repoName).doOnNext(list -> {
			// Clear cache
			branchCaches.clear();
			// Clear data in local storage
		}).flatMap(Observable::fromIterable).doOnNext(branch -> {
			branchCaches.add(branch);
		}).toList().toObservable();
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

