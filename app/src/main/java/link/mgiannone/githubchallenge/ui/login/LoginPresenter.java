package link.mgiannone.githubchallenge.ui.login;

import javax.inject.Inject;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import link.mgiannone.githubchallenge.data.repository.GitHubChallengeRepository;

public class LoginPresenter implements LoginContract.Presenter, LifecycleObserver {

	private GitHubChallengeRepository repository;

	@Inject
	public LoginPresenter(GitHubChallengeRepository repository, LoginContract.View view) {
		this.repository = repository;
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