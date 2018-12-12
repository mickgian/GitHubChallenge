package link.mgiannone.githubchallenge.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import link.mgiannone.githubchallenge.data.Config;
import link.mgiannone.githubchallenge.data.model.Repo;

@Dao
public interface RepoDao {
	@Query("SELECT * FROM " + Config.REPO_TABLE_NAME)
	Flowable<List<Repo>> getAllRepositories();

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Repo repo);

	@Query("DELETE FROM " + Config.REPO_TABLE_NAME)
	void deleteAll();
}
