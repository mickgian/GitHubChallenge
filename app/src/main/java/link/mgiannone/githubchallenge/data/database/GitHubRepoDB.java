package link.mgiannone.githubchallenge.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import link.mgiannone.githubchallenge.data.model.Branch;
import link.mgiannone.githubchallenge.data.model.Repo;

@Database(entities = {Repo.class, Branch.class} , version = 1)
public abstract class GitHubRepoDB extends RoomDatabase {

	public abstract RepoDao repoDao();

	public abstract BranchDao branchDao();

}