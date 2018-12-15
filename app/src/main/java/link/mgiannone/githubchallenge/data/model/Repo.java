package link.mgiannone.githubchallenge.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import link.mgiannone.githubchallenge.data.Config;

@Entity(tableName = Config.REPO_TABLE_NAME)
public class Repo {


	@SerializedName("id")
	@PrimaryKey
	private int id;

	@SerializedName("name")
	private String name;

	@SerializedName("owner")
	@Embedded(prefix = "owner_")
	private Owner owner;

	@SerializedName("description")
	private String description;

	@SerializedName("url")
	private String url;

	@SerializedName("stargazers_count")
	private int stargazersCount;

	@SerializedName("watchers_count")
	private int watchersCount;

	@SerializedName("language")
	private String language;

	@SerializedName("forks_count")
	private int forksCount;

	@SerializedName("watchers")
	private int watchers;

	@SerializedName("commits_count")
	private int commitsCount;

	@SerializedName("default_branch")
	private String defaultBranch;

	@Ignore
	private List<Branch> branchList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStargazersCount() {
		return stargazersCount;
	}

	public void setStargazersCount(int stargazersCount) {
		this.stargazersCount = stargazersCount;
	}

	public int getWatchersCount() {
		return watchersCount;
	}

	public void setWatchersCount(int watchersCount) {
		this.watchersCount = watchersCount;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getForksCount() {
		return forksCount;
	}

	public void setForksCount(int forksCount) {
		this.forksCount = forksCount;
	}

	public int getWatchers() {
		return watchers;
	}

	public void setWatchers(int watchers) {
		this.watchers = watchers;
	}

	public int getCommitsCount() {
		return commitsCount;
	}

	public void setCommitsCount(int commitsCount) {
		this.commitsCount = commitsCount;
	}

	public String getDefaultBranch() {
		return defaultBranch;
	}

	public void setDefaultBranch(String defaultBranch) {
		this.defaultBranch = defaultBranch;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<Branch> branchList) {
		this.branchList = branchList;
	}
}

