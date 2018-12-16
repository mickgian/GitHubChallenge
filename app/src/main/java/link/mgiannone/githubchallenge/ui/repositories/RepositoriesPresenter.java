package link.mgiannone.githubchallenge.ui.repositories;

import android.util.Log;

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
		String owner = view.getOwner();
		if(!owner.equalsIgnoreCase("")) {
			loadRepos(false, view.getOwner());
		}
	}

	@Override @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) public void onDetach() {
		// Clean up any no-longer-use resources here
		disposeBag.clear();
	}

	@Override
	public void checkRepoPerUser(String owner) {
		// Load new one and populate it into view
		Disposable disposable = repository.checkReposPerUser(owner)
				.subscribeOn(ioScheduler)
				.observeOn(uiScheduler)
				.subscribe(this::handleReturnedHeaderData, this::handleHeaderError);
		disposeBag.add(disposable);
	}

	private void handleReturnedHeaderData(Response<List<Headers>> response) {
		//getting value 'Link' from response headers
		String link = response.headers().get("Link");
		String message = response.message();
		int code = response.code();

		if(code == 404 && message.toLowerCase().equalsIgnoreCase("not found")) {
			view.showUserNotFoundMessage();
		} else if(code == 403 && message.toLowerCase().equalsIgnoreCase("forbidden")){
			view.showApiRateLimitExceeded();
		} else if (code == 200 && link == null) { //Link value is not present into the header, it means there's 0 or 1 repo
			Log.d(TAG, "Total repos for current user is 0 or 1.");
			//get the repository
			searchRepo(view.getOwner());
		} else if (code == 200 && link != null){

			//get last page number: considering that we requested all the branches paginated with
			//only 1 branch per page, the last page number is equal to the total number of branches
			String totalRepoString = link.substring(link.lastIndexOf("&page=") + 6, link.lastIndexOf(">"));
			Log.d(TAG, "Total repos for current user are " + totalRepoString);

			//get the repositories
			searchRepo(view.getOwner());
		}
//		else if (){
//			//user exists but has zero repos
//		}
		else{

		}


	}

	private void handleHeaderError(Throwable throwable) {
		Log.e(TAG, throwable.getMessage(), throwable);
	}





	@Override public void loadRepos(boolean onlineRequired, String owner) {
		// Clear old data on view
		view.clearRepos();

		// Load new one and populate it into view
		Disposable disposable = repository.loadRepos(onlineRequired, owner)
				.subscribeOn(ioScheduler)
				.observeOn(uiScheduler)
				.subscribe(this::handleReturnedData, this::handleError, () -> view.stopLoadingIndicator());
		disposeBag.add(disposable);

	}

	@Override public void getRepo(int repoId) {
		Disposable disposable = repository.getRepo(repoId)
				.filter(repo -> repo != null)
				.subscribeOn(ioScheduler)
				.observeOn(uiScheduler)
				.subscribe(repo -> view.showRepositoryDetail(repo));
		disposeBag.add(disposable);
	}

	@Override public void searchRepo(final String owner) {

		// Load new one and populate it into view
		Disposable disposable = repository.loadRepos(true, owner)
				.flatMap(Observable::fromIterable)
				.filter(repo -> repo.getName() != null)
				.toList()
				.toObservable()
				.subscribeOn(ioScheduler)
				.observeOn(uiScheduler)
				.subscribe(repos -> {
					if (repos.isEmpty()) {
						// Clear old data in view
						view.clearRepos();
						// Show notification
						view.showEmptySearchResult();
					} else {
						// Update filtered data
						view.showRepos(repos);

					}
				});

		disposeBag.add(disposable);

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
		view.stopLoadingIndicator();
		view.showErrorMessage(error.getLocalizedMessage());
	}
}
