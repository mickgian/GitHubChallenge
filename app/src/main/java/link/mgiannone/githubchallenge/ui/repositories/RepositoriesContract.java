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

		void stopLoadingIndicator();

		void showEmptySearchResult();

		void showUserNotFoundMessage();

		String getOwner();

		void startLogin();

		void showProgressBarIfHidden();
	}

	interface Presenter extends BasePresenter<View> {
		void presenterLoadRepos(boolean onlineRequired, String owner);

		void searchRepo(String repoTitle);

		void checkRepoPerUser(String query);
	}
}
