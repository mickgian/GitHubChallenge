package link.mgiannone.githubchallenge.ui.login;

import link.mgiannone.githubchallenge.data.model.AccessToken;
import link.mgiannone.githubchallenge.ui.base.BasePresenter;
import retrofit2.Call;

public interface LoginContract {
	interface View {

		void showNoDataMessage();

		void showSuccessMessage(String accessTokenString);

		void showErrorMessage();
	}

	interface Presenter extends BasePresenter<View> {

		void sendSuccessMessageToView(String accessTokenString);

		void sendErrorMessageToView();

		void getAccessToken(String clientId, String clientSecret, String code);
	}
}
