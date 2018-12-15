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
import io.reactivex.functions.Consumer;
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
						view.countBranches(repos);
					}
				});

		disposeBag.add(disposable);

	}

	@Override
	public void searchBranches(List<Repo> repositories) {

		for(int i = 0; i < repositories.size(); i++){
			int finalI = i;
			Disposable disposable = repository.countBranches(true, repositories.get(i).getOwner().getLogin(), repositories.get(i).getName())
					.subscribeOn(ioScheduler)
					.observeOn(uiScheduler)
					.subscribe(new Consumer<Response<List<Headers>>>() {
						@Override
						public void accept(Response<List<Headers>> response) throws Exception {

							//getting value 'Link' from response headers
							String link = response.headers().get("Link");
							if(link == null){ //Link value is not present into the header, it means there's only 1 branch, the master one.
								repositories.get(finalI).setBranchesCount(1);
							}else {
								Log.d("RepositoriesPresenter", "Header Link: " + link);

								//get last page number: considering that we requested all the branches paginated with
								//only 1 branch per page, the last page number is equal to the total number of branches
								String totalBranchesString = link.substring(link.lastIndexOf("&page=") + 6, link.lastIndexOf(">"));
								Log.d("RepositoriesPresenter", "Total branches: " + totalBranchesString);

								//set commits number into Repo object
								repositories.get(finalI).setBranchesCount(Integer.valueOf(totalBranchesString));
							}

						}
					});

			disposeBag.add(disposable);

			if(finalI == repositories.size() -1){
				view.countCommits(repositories);
			}
		}
	}

	@Override
	public void searchCommits(List<Repo> repositories) {

		for(int i = 0; i < repositories.size(); i++){
			int finalI = i;
			Disposable disposable = repository.countCommits(true, repositories.get(i).getOwner().getLogin(), repositories.get(i).getName())
					.subscribeOn(ioScheduler)
					.observeOn(uiScheduler)
					.subscribe(new Consumer<Response<List<Headers>>>() {
						@Override
						public void accept(Response<List<Headers>> response) throws Exception {

							//getting value 'Link' from response headers
							String link = response.headers().get("Link");
							Log.d("RepositoriesPresenter", "Header Link: " + link);

							//get last page number: considering that we requested all the commits paginated with
							//only 1 commit per page, the last page number is equal to the total number of commits
							String totalCommitsString = link.substring(link.lastIndexOf("&page=")+6, link.lastIndexOf(">"));
							Log.d("RepositoriesPresenter", "Total commit: " + totalCommitsString);

							//set commits number into Repo object
							repositories.get(finalI).setCommitsCount(Integer.valueOf(totalCommitsString));

						}
					});

			disposeBag.add(disposable);

			if(finalI == repositories.size() -1){
				view.showRepos(repositories);
			}
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
		view.stopLoadingIndicator();
		view.showErrorMessage(error.getLocalizedMessage());
	}
}
