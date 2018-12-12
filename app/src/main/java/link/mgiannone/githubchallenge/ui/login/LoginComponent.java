package link.mgiannone.githubchallenge.ui.login;

import dagger.Component;
import link.mgiannone.githubchallenge.data.GitHubChallengeRepositoryComponent;
import link.mgiannone.githubchallenge.ui.base.ActivityScope;
import link.mgiannone.githubchallenge.util.schedulers.SchedulerModule;

@ActivityScope
@Component(modules = {LoginPresenterModule.class, SchedulerModule.class}, dependencies = {
		GitHubChallengeRepositoryComponent.class
})
public interface LoginComponent {
	void inject(LoginActivity loginActivity);
}
