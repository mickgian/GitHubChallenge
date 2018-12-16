package link.mgiannone.githubchallenge.ui.repositories;

import java.util.List;

import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.ui.base.BasePresenter;

public interface RepositoriesContract {
	interface View {
		void showRepos(List<Repo> repositories);

		void clearRepos();

		void showNoDataMessage();

		void showErrorMessage(String error);

		void showRepositoryDetail(Repo repo);

		void stopLoadingIndicator();

		void showEmptySearchResult();

		void showUserNotFoundMessage();

		void showApiRateLimitExceeded();

		String getOwner();
	}

	interface Presenter extends BasePresenter<View> {
		void loadRepos(boolean onlineRequired, String owner);

		void getRepo(int repoId);

		void searchRepo(String repoTitle);

		void checkRepoPerUser(String query);
	}
}
