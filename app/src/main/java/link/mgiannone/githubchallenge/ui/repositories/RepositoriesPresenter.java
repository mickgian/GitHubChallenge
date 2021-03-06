package link.mgiannone.githubchallenge.ui.repositories;

import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.List;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.GitHubChallengeRepository;
import link.mgiannone.githubchallenge.util.schedulers.RunOn;

import okhttp3.Headers;
import retrofit2.Response;

import static link.mgiannone.githubchallenge.util.schedulers.SchedulerType.IO;
import static link.mgiannone.githubchallenge.util.schedulers.SchedulerType.UI;

/**
 * A presenter with life-cycle aware.
 */
public class RepositoriesPresenter implements RepositoriesContract.Presenter, LifecycleObserver {

	private static final String TAG = RepositoriesPresenter.class.getSimpleName();


	private GitHubChallengeRepository repository;

	private RepositoriesContract.View view;

	private Scheduler ioScheduler;
	private Scheduler uiScheduler;

	private CompositeDisposable disposeBag;

	@Inject
	public RepositoriesPresenter(GitHubChallengeRepository repository, RepositoriesContract.View view,
								 @RunOn(IO) Scheduler ioScheduler, @RunOn(UI) Scheduler uiScheduler) {
		this.repository = repository;
		this.view = view;
		this.ioScheduler = ioScheduler;
		this.uiScheduler = uiScheduler;

		// Initialize this presenter as a lifecycle-aware when a view is a lifecycle owner.
		if (view instanceof LifecycleOwner) {
			((LifecycleOwner) view).getLifecycle().addObserver(this);
		}

		disposeBag = new CompositeDisposable();
	}

	@Override @OnLifecycleEvent(Lifecycle.Event.ON_RESUME) public void onAttach() {
		presenterLoadRepos(false, view.getOwner());
	}

	@Override @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) public void onDetach() {
		// Clean up any no-longer-use resources here
		disposeBag.clear();
	}


	@Override
	public void checkRepoPerUser(String owner) {

		//recovering access token data from Shared Preferences;
		String accessTokenString = repository.getAccessTokenString();
		String accessTokenTypeString = repository.getAccessTokenType();

		//Asking for a list of repositories with 1 repository per page.
		//This let us know how many repositories we found and also to deal with error response code
		Disposable disposable = repository.checkReposPerUser(owner, accessTokenString, accessTokenTypeString, "1")
				.subscribeOn(ioScheduler)
				.observeOn(uiScheduler)
				.subscribe(this::handleReturnedHeaderData, this::handleHeaderError);
		disposeBag.add(disposable);
	}

	@VisibleForTesting
 	private void handleReturnedHeaderData(Response<List<Headers>> response) {
		//getting value 'Link' from response headers in order to count the repositories
		String link = response.headers().get("Link");
		String message = response.message();

		//For debugging: checking GitHub API requests limit
		String limit = response.headers().get("X-RateLimit-Limit");
		String limitRemaining = response.headers().get("X-RateLimit-Remaining");

		//getting http response code
		int code = response.code();

		switch (code){
			case 404:
				if(message.equalsIgnoreCase("not found")){ //User not exists
					view.showUserNotFoundMessage();
				}else{
					view.showErrorMessage(message);
				}
				break;
			case 403:
				//GitHub API requests limit reached
				//Instead of showing an error, we start the login process,
				// store another access token in shared Preferences and resend the same request that failed before
				view.startLogin();
				break;
			case 200:
				if(link == null){ //Link value is not present into the header, it means there's 0 or 1 repo
					Log.d(TAG, "Total repos for current user is 0 or 1.");
					//get the repository
					searchRepo(view.getOwner()); //Starting looking for data
				}else if( link != null){
					//get last page number: considering that we requested all the repos paginated with
					//only 1 repo per page, the last page number is equal to the total number of repos
					String totalRepoString = link.substring(link.lastIndexOf("&page=") + 6, link.lastIndexOf(">"));
					Log.d(TAG, "Total repos for current user are " + totalRepoString);

					// TODO once we know how many repositories we have, we can decide how many calls to do (total repositories/100 rounded up )

					//get the repositories
					searchRepo(view.getOwner()); //Starting 3 looking for data
				}
				break;
			default:
				searchRepo(view.getOwner()); //Starting 3 looking for data
				break;
		}
	}

	private void handleHeaderError(Throwable error) {
		view.showErrorMessage(error.getLocalizedMessage());
	}

	@Override public void searchRepo(final String owner) {

		view.showProgressBarIfHidden();

		//recovering access token data from Shared Preferences
		String accessTokenString = repository.getAccessTokenString();
		String accessTokenTypeString = repository.getAccessTokenType();

		// Load new one and populate it into view
		Disposable disposable = repository.loadRemoteRepos(owner, accessTokenString, accessTokenTypeString, "100")
				.flatMap(Observable::fromIterable)
				.filter(repo -> repo.getName() != null)
				.toList()
				.toObservable()
				.subscribeOn(ioScheduler)
				.observeOn(uiScheduler)
				.subscribe(repos -> {
					if (repos.isEmpty()) {
						// Clear old data from recycler view
						view.clearRepos();
						// Show notification
						view.showEmptySearchResult();
					} else {
						// Update recycler view items
						view.showRepos(repos);

					}
				});

		disposeBag.add(disposable);

	}

	public void presenterLoadRepos(boolean onlineRequired, String owner) {
		// Clear old data on view
		view.clearRepos();

		//recovering access token data from Shared Preferences
		String accessTokenString = repository.getAccessTokenString();
		String accessTokenTypeString = repository.getAccessTokenType();

		if(onlineRequired){
			Disposable disposable = repository.loadRemoteRepos(owner, accessTokenString, accessTokenTypeString, "100")
					.subscribeOn(ioScheduler)
					.observeOn(uiScheduler)
					.subscribe(this::handleReturnedData, this::handleError, () -> view.stopLoadingIndicator());
			disposeBag.add(disposable);
		}else {
			// Load new repositories and paginate them with 100 (GitHub API max) repositories par page.
			Disposable disposable = repository.loadLocalRepos(owner, accessTokenString, accessTokenTypeString, "100")
					.subscribeOn(ioScheduler)
					.observeOn(uiScheduler)
					.subscribe(this::handleReturnedData, this::handleError, () -> view.stopLoadingIndicator());
			disposeBag.add(disposable);
		}

	}

	/**
	 * Updates view after loading data is completed successfully.
	 */
	private void handleReturnedData(List<Repo> list) {
		view.stopLoadingIndicator();
		if (list != null && !list.isEmpty()) {
			view.showRepos(list);
		} else {
			view.showNoDataMessage();
		}
	}

	/**
	 * Updates view if there is an error after loading data from repository.
	 */
	private void handleError(Throwable error) {
		if(error.getMessage().equalsIgnoreCase("http 403 forbidden")){
			view.startLogin();
		}else {
			view.stopLoadingIndicator();
			view.showErrorMessage(error.getLocalizedMessage());
		}
	}

}
