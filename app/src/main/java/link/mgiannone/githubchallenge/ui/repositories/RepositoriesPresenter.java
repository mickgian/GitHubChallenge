package link.mgiannone.githubchallenge.ui.repositories;

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

import static link.mgiannone.githubchallenge.util.schedulers.SchedulerType.IO;
import static link.mgiannone.githubchallenge.util.schedulers.SchedulerType.UI;

/**
 * A presenter with life-cycle aware.
 */
public class RepositoriesPresenter implements RepositoriesContract.Presenter, LifecycleObserver {

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
		loadRepos(false, "");
	}

	@Override @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) public void onDetach() {
		// Clean up any no-longer-use resources here
		disposeBag.clear();
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
