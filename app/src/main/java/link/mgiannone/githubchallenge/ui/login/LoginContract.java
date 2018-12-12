package link.mgiannone.githubchallenge.ui.login;

import java.util.List;

import link.mgiannone.githubchallenge.ui.base.BasePresenter;

public interface LoginContract {
	interface View {

		void showNoDataMessage();

		void showErrorMessage(String error);
	}

	interface Presenter extends BasePresenter<View> {

	}
}
