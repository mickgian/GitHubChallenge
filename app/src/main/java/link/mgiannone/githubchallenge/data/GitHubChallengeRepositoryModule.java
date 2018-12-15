package link.mgiannone.githubchallenge.data;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import link.mgiannone.githubchallenge.data.repository.AccessTokenDataSource;
import link.mgiannone.githubchallenge.data.repository.BranchDataSource;
import link.mgiannone.githubchallenge.data.repository.CommitDataSource;
import link.mgiannone.githubchallenge.data.repository.Local;
import link.mgiannone.githubchallenge.data.repository.RepoDataSource;
import link.mgiannone.githubchallenge.data.repository.Remote;
import link.mgiannone.githubchallenge.data.repository.local.AccessTokenLocalDataSource;
import link.mgiannone.githubchallenge.data.repository.local.RepoLocalDataSource;
import link.mgiannone.githubchallenge.data.repository.remote.AccessTokenRemoteDataSource;
import link.mgiannone.githubchallenge.data.repository.remote.BranchRemoteDataSource;
import link.mgiannone.githubchallenge.data.repository.remote.CommitRemoteDataSource;
import link.mgiannone.githubchallenge.data.repository.remote.RepoRemoteDataSource;

@Module
public class GitHubChallengeRepositoryModule {

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
	@Remote
	@Singleton
	public BranchDataSource provideRemoteBranchDataSource(BranchRemoteDataSource branchRemoteDataSource) {
		return branchRemoteDataSource;
	}

	@Provides
	@Remote
	@Singleton
	public CommitDataSource provideRemoteCommitDataSource(CommitRemoteDataSource commitRemoteDataSource) {
		return commitRemoteDataSource;
	}

	@Provides
	@Local
	@Singleton
	public AccessTokenDataSource provideLocalAccessTokenDataSource(AccessTokenLocalDataSource accessTokenLocalDataSource) {
		return accessTokenLocalDataSource;
	}

	@Provides
	@Remote
	@Singleton
	public AccessTokenDataSource provideRemoteAccessTokenDataSource(AccessTokenRemoteDataSource accessTokenRemoteDataSource) {
		return accessTokenRemoteDataSource;
	}

}
