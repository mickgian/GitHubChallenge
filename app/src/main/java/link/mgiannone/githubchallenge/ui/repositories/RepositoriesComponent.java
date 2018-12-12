package link.mgiannone.githubchallenge.ui.repositories;

import dagger.Component;
import link.mgiannone.githubchallenge.data.GitHubChallengeRepositoryComponent;
import link.mgiannone.githubchallenge.ui.base.ActivityScope;
import link.mgiannone.githubchallenge.util.schedulers.SchedulerModule;

@ActivityScope
@Component(modules = {RepositoriesPresenterModule.class, SchedulerModule.class}, dependencies = {
		GitHubChallengeRepositoryComponent.class
})
public interface RepositoriesComponent {
	void inject(RepositoriesActivity repositoriesActivity);
}
