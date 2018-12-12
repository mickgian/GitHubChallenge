package link.mgiannone.githubchallenge.data;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import link.mgiannone.githubchallenge.data.repository.Local;
import link.mgiannone.githubchallenge.data.repository.RepoDataSource;
import link.mgiannone.githubchallenge.data.repository.Remote;
import link.mgiannone.githubchallenge.data.repository.local.RepoLocalDataSource;
import link.mgiannone.githubchallenge.data.repository.remote.RepoRemoteDataSource;

@Module
public class RepoRepositoryModule {

	@Provides
	@Local
	@Singleton
	public RepoDataSource provideLocalDataSource(RepoLocalDataSource repoLocalDataSource) {
		return repoLocalDataSource;
	}

	@Provides
	@Remote
	@Singleton
	public RepoDataSource provideRemoteDataSource(RepoRemoteDataSource repoRemoteDataSource) {
		return repoRemoteDataSource;
	}

}
