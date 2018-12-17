package link.mgiannone.githubchallenge.ui.login;

import javax.inject.Inject;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import link.mgiannone.githubchallenge.data.repository.GitHubChallengeRepository;

public class LoginPresenter implements LoginContract.Presenter, LifecycleObserver {

	private GitHubChallengeRepository repository;

	@Inject
	public LoginPresenter(GitHubChallengeRepository repository, LoginContract.View view) {
		this.repository = repository;

		// Initialize this presenter as a lifecycle-aware when a view is a lifecycle owner.
		if (view instanceof LifecycleOwner) {
			((LifecycleOwner) view).getLifecycle().addObserver(this);
		}
	}

	@Override @OnLifecycleEvent(Lifecycle.Event.ON_RESUME) public void onAttach() {

	}

	@Override @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) public void onDetach() {

	}

	@Override
	public void getAccessToken(String clientId, String clientSecret, String code) {
		repository.recoverAccessToken(clientId, clientSecret, code);
	}
}