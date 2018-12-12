package link.mgiannone.githubchallenge.data;

import androidx.room.Room;
import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import link.mgiannone.githubchallenge.data.database.BranchDao;
import link.mgiannone.githubchallenge.data.database.GitHubRepoDB;
import link.mgiannone.githubchallenge.data.database.RepoDao;

@Module
public class GitHubChallengeDatabaseModule {
	private static final String DATABASE = "database_name";

	@Provides
	@Named(DATABASE)
	String provideDatabaseName() {
		return Config.GITHUBCHALLENGE_DATABASE_NAME;
	}

	@Provides
	@Singleton
	GitHubRepoDB provideGitHubChallengeDb(Context context, @Named(DATABASE) String databaseName) {
		return Room.databaseBuilder(context, GitHubRepoDB.class, databaseName).build();
	}

	@Provides
	@Singleton
	RepoDao provideRepositoryDao(GitHubRepoDB gitHubRepoDB) {
		return gitHubRepoDB.repoDao();
	}

	@Provides
	@Singleton
	BranchDao provideBranchDao(GitHubRepoDB gitHubRepoDB) {
		return gitHubRepoDB.branchDao();
	}
}
