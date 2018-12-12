package link.mgiannone.githubchallenge.data;

import javax.inject.Singleton;

import dagger.Component;
import link.mgiannone.githubchallenge.AppModule;
import link.mgiannone.githubchallenge.data.repository.RepoRepository;

@Singleton
@Component(modules = { RepoRepositoryModule.class,  AppModule.class, GitHubChallengeApiServiceModule.class,
		GitHubChallengeDatabaseModule.class})
public interface GitHubChallengeRepositoryComponent {
	RepoRepository provideRepoRepository();
}
