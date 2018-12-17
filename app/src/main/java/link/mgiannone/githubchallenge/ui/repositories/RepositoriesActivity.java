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
import android.widget.ProgressBar;
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
	@BindView(R.id.repo_owner_text_view)
	TextView repoOwnerTextView;
	@BindView(R.id.repo_text_notification)
	TextView notificationText;
	@BindView(R.id.loadReposProgressBar)
	ProgressBar loadReposProgressBar;

	private SearchView searchView;
	private RepositoriesAdapter adapter;
	private String owner = "mickgian";

	@Inject
	RepositoriesPresenter presenter;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repositories);
		ButterKnife.bind(this);
		initializePresenter();
		setupWidgets();
	}

	@Override
	protected void onResume() {
		super.onResume();
		actionBarSetup(RepositoriesActivity.this);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		repoOwnerTextView.setText(savedInstanceState.getString("owner"));
		notificationText.setText(savedInstanceState.getString("notification_text"));
	}

	private void initializePresenter() {
		DaggerRepositoriesComponent.builder()
				.repositoriesPresenterModule(new RepositoriesPresenterModule(this))
				.gitHubChallengeRepositoryComponent(getGitHubChallengeRepositoryComponent())
				.build()
				.inject(this);
	}

	@Override
	public String getOwner() {
		if(owner == null){
			return "";
		}else {
			return owner;
		}
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
		searchView = (SearchView) menu.findItem(R.id.repos_action_search).getActionView();
		searchView.setQueryHint(getString(R.string.repo_search_hint));
		searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override public boolean onQueryTextSubmit(String query) {
				owner = query;
				presenter.checkRepoPerUser(query);
				searchView.clearFocus();
				loadReposProgressBar.setVisibility(View.VISIBLE);
				return true;
			}

			@Override public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		return true;
	}

	@Override public void showRepos(List<Repo> repositories) {
		refreshLayout.setVisibility(View.VISIBLE);
		notificationText.setVisibility(View.GONE);
		adapter.replaceData(repositories);
		repoOwnerTextView.setText(repositories.get(0).getOwner().getLogin());
		loadReposProgressBar.setVisibility(View.GONE);
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
//		Intent intent = new Intent(RepositoriesActivity.this, RepositoryDetail.class);
//		startActivity(intent);
	}

	@Override public void showEmptySearchResult() {
		repoOwnerTextView.setText(owner);
		showNotification(getString(R.string.msg_empty_repo_search_result));
	}

	@Override
	public void showUserNotFoundMessage() {
		repoOwnerTextView.setText("");
		loadReposProgressBar.setVisibility(View.GONE);
		refreshLayout.setVisibility(View.GONE);
		notificationText.setVisibility(View.VISIBLE);
		notificationText.setText(getString(R.string.no_user_found));
	}

	@Override
	public void showApiRateLimitExceeded() {
		loadReposProgressBar.setVisibility(View.GONE);
		refreshLayout.setVisibility(View.GONE);
		notificationText.setVisibility(View.VISIBLE);
		notificationText.setText(R.string.api_rate_limit_exceeded);
	}

	private void showNotification(String message) {
		loadReposProgressBar.setVisibility(View.GONE);
		refreshLayout.setVisibility(View.GONE);
		notificationText.setVisibility(View.VISIBLE);
		notificationText.setText(message);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("owner", repoOwnerTextView.getText().toString());
		outState.putString("notification_text", notificationText.getText().toString());

		// call superclass to save any view hierarchy
		super.onSaveInstanceState(outState);
	}
}

