package link.mgiannone.githubchallenge.ui.login;

import link.mgiannone.githubchallenge.ui.base.BasePresenter;

public interface LoginContract {
	interface View {

	}

	interface Presenter extends BasePresenter<View> {

		void getAccessToken(String clientId, String clientSecret, String code);
	}
}
