package link.mgiannone.githubchallenge.data;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import link.mgiannone.githubchallenge.data.repository.BranchDataSource;
import link.mgiannone.githubchallenge.data.repository.Local;
import link.mgiannone.githubchallenge.data.repository.RepoDataSource;
import link.mgiannone.githubchallenge.data.repository.Remote;
import link.mgiannone.githubchallenge.data.repository.local.BranchLocalDataSource;
import link.mgiannone.githubchallenge.data.repository.local.RepoLocalDataSource;
import link.mgiannone.githubchallenge.data.repository.remote.BranchRemoteDataSource;
import link.mgiannone.githubchallenge.data.repository.remote.RepoRemoteDataSource;

@Module
public class RepoRepositoryModule {

	@Provides
	@Local
	@Singleton
	public RepoDataSource provideLocalRepoDataSource(RepoLocalDataSource repoLocalDataSource) {
		return repoLocalDataSource;
	}

	@Provides
	@Remote
	@Singleton
	public RepoDataSource provideRemoteRepoDataSource(RepoRemoteDataSource repoRemoteDataSource) {
		return repoRemoteDataSource;
	}


	@Provides
	@Local
	@Singleton
	public BranchDataSource provideLocalBranchDataSource(BranchLocalDataSource branchLocalDataSource) {
		return branchLocalDataSource;
	}

	@Provides
	@Remote
	@Singleton
	public BranchDataSource provideRemoteBranchDataSource(BranchRemoteDataSource branchRemoteDataSource) {
		return branchRemoteDataSource;
	}
}
