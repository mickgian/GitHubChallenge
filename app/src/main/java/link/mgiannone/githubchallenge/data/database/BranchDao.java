package link.mgiannone.githubchallenge.data.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Observable;
import link.mgiannone.githubchallenge.data.Config;
import link.mgiannone.githubchallenge.data.model.Branch;

@Dao
public interface BranchDao {

	@Query("SELECT * FROM " + Config.BRANCH_TABLE_NAME)
	Observable<List<Branch>> getAllBranches();

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(Branch branch);

	@Query("DELETE FROM " + Config.BRANCH_TABLE_NAME)
	void deleteAll();

}
