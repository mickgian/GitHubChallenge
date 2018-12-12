package link.mgiannone.githubchallenge.ui.login;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginPresenterModule {

	private LoginContract.View view;

	public LoginPresenterModule(LoginContract.View view) {
		this.view = view;
	}

	@Provides
	public LoginContract.View provideView() {
		return view;
	}
}
