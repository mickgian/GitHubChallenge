package link.mgiannone.githubchallenge.data.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import link.mgiannone.githubchallenge.data.Config;

@Entity(tableName = Config.BRANCH_TABLE_NAME)
public class Branch {

	@SerializedName("name")
	@PrimaryKey
	@NonNull
	private String name;

	@SerializedName("commit")
	@Embedded(prefix = "commit_")
	private Commit commit;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}
}
