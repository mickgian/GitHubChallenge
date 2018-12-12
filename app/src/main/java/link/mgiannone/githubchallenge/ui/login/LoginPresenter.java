package link.mgiannone.githubchallenge.ui.login;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import link.mgiannone.githubchallenge.BuildConfig;
import link.mgiannone.githubchallenge.util.schedulers.RunOn;

import static link.mgiannone.githubchallenge.util.schedulers.SchedulerType.IO;
import static link.mgiannone.githubchallenge.util.schedulers.SchedulerType.UI;

public class LoginPresenter implements LoginContract.Presenter, LifecycleObserver {


	private LoginContract.View view;

	private Scheduler ioScheduler;
	private Scheduler uiScheduler;

	private CompositeDisposable disposeBag;

	@Inject
	public LoginPresenter(LoginContract.View view,
						  @RunOn(IO) Scheduler ioScheduler, @RunOn(UI) Scheduler uiScheduler) {
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

	}

	@Override @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) public void onDetach() {
		// Clean up any no-longer-use resources here
		disposeBag.clear();
	}



}