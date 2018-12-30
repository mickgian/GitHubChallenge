package link.mgiannone.githubchallenge.data;

import javax.inject.Singleton;

import dagger.Component;
import link.mgiannone.githubchallenge.AppModule;
import link.mgiannone.githubchallenge.data.repository.GitHubChallengeRepository;

@Singleton
@Component(modules = {
		AppModule.class,
		GitHubChallengeRepositoryModule.class,
		GitHubChallengeApiServiceModule.class,
		GitHubChallengeDatabaseModule.class,
		GitHubChallengePrefModule.class
})
public interface GitHubChallengeRepositoryComponent {
	GitHubChallengeRepository provideGitHubChallengeRepository();
}
