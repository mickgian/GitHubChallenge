package link.mgiannone.githubchallenge.data.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;

public class Branch {

	@SerializedName("name")
	@NonNull
	private String name;

	@SerializedName("commit")
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
