package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import link.mgiannone.githubchallenge.data.model.Repo;

public class RepoResponse {

	private List<Repo> repoList;

	public List<Repo> getRepoList() {
		return repoList;
	}

	public void setRepoList(List<Repo> repoList) {
		this.repoList = repoList;
	}
}

