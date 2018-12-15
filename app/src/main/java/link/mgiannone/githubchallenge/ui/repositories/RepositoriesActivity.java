package link.mgiannone.githubchallenge.ui.repositories;

import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import link.mgiannone.githubchallenge.R;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.ui.base.BaseActivity;

public class RepositoriesActivity extends BaseActivity implements RepositoriesContract.View {

	@BindView(R.id.recycler_repos)
	RecyclerView repoRecyclerView;
	@BindView(R.id.refreshRepos)
	SwipeRefreshLayout refreshLayout;
	@BindView(R.id.repo_text_notification)
	TextView notificationText;

	private RepositoriesAdapter adapter;
	private String owner = "";

	@Inject
	RepositoriesPresenter presenter;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repositories);
		actionBarSetup(RepositoriesActivity.this);
		ButterKnife.bind(this);
		initializePresenter();
		setupWidgets();
	}

	private void initializePresenter() {
		DaggerRepositoriesComponent.builder()
				.repositoriesPresenterModule(new RepositoriesPresenterModule(this))
				.gitHubChallengeRepositoryComponent(getGitHubChallengeRepositoryComponent())
				.build()
				.inject(this);
	}

	private void setupWidgets() {
		// Setup recycler view
		adapter = new RepositoriesAdapter(new ArrayList<>(), this);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		repoRecyclerView.setLayoutManager(layoutManager);
		repoRecyclerView.setAdapter(adapter);
		repoRecyclerView.setItemAnimator(new DefaultItemAnimator());
		adapter.setOnItemClickListener(
				(view, position) -> presenter.getRepo(adapter.getItem(position).getId()));

		// Refresh layout
		refreshLayout.setOnRefreshListener(() -> presenter.loadRepos(true, owner));
		// Set notification text visible first
		notificationText.setVisibility(View.GONE);
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.repositories, menu);

		// Setup search widget in action bar
		SearchView searchView = (SearchView) menu.findItem(R.id.repos_action_search).getActionView();
		searchView.setQueryHint(getString(R.string.repo_search_hint));
		searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override public boolean onQueryTextSubmit(String query) {
				owner = query;
				presenter.searchRepo(query);
				return true;
			}

			@Override public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		return true;
	}

	@Override public void countBranches(List<Repo> repositories){
		presenter.searchBranches(repositories);
	}

	@Override public void countCommits(List<Repo> repositories){
		presenter.searchCommits(repositories);
	}

	@Override public void showRepos(List<Repo> repositories) {
		notificationText.setVisibility(View.GONE);
		adapter.replaceData(repositories);
	}

	@Override public void showNoDataMessage() {
		showNotification(getString(R.string.msg_no_data));
	}

	@Override public void showErrorMessage(String error) {
		showNotification(error);
	}

	@Override public void clearRepos() {
		adapter.clearData();
	}

	@Override public void stopLoadingIndicator() {
		if (refreshLayout.isRefreshing()) {
			refreshLayout.setRefreshing(false);
		}
	}

	@Override public void showRepositoryDetail(Repo repo) {
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.setData(Uri.parse(repo.getLink()));
//		startActivity(intent);
	}

	@Override public void showEmptySearchResult() {
		showNotification(getString(R.string.msg_empty_repo_search_result));
	}

	private void showNotification(String message) {
		notificationText.setVisibility(View.VISIBLE);
		notificationText.setText(message);
	}
}

