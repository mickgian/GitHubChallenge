package link.mgiannone.githubchallenge.ui.repositories;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.security.InvalidParameterException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import link.mgiannone.githubchallenge.R;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.ui.base.BaseRecyclerViewAdapter;

class RepositoriesAdapter extends BaseRecyclerViewAdapter<RepositoriesAdapter.RepoViewHolder> {

	class RepoViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.repo_title_text_view)
		TextView repoTitleTextView;
		@BindView(R.id.repo_language_text_view)
		TextView repoLanguageTextView;
		@BindView(R.id.repo_description_text_view)
		TextView repoDescriptionTextView;

		@BindView(R.id.repo_stars_count_text_view)
		TextView repoStarsCountTextView;
		@BindView(R.id.repo_forks_count_text_view)
		TextView repoForksCountTextView;
		@BindView(R.id.repo_branches_count_text_view)
		TextView repoBranchesCountTextView;
		@BindView(R.id.repo_commits_count_text_view)
		TextView repoCommitsCountTextView;


		public RepoViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	private List<Repo> repoList;
	private Context context;

	public RepositoriesAdapter(@NonNull List<Repo> repoList, Context context) {
		this.repoList = repoList;
		this.context = context;
	}

	@Override public RepoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.item_repositories, viewGroup, false);
		return new RepoViewHolder(view);
	}

	@Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
		super.onBindViewHolder(viewHolder, i);
		RepoViewHolder vh = (RepoViewHolder) viewHolder; //safe cast
		Repo repo = repoList.get(i);

		vh.repoTitleTextView.setText(repo.getName());
		vh.repoLanguageTextView.setText(repo.getLanguage());
		vh.repoDescriptionTextView.setText(repo.getDescription());

		vh.repoStarsCountTextView.setText(String.valueOf(repo.getStargazersCount()));
		vh.repoForksCountTextView.setText(String.valueOf(repo.getForksCount()));
		vh.repoBranchesCountTextView.setText(String.valueOf(repo.getBranchesCount()));
		vh.repoCommitsCountTextView.setText(String.valueOf(repo.getCommitsCount()));
	}

	@Override public int getItemCount() {
		return repoList.size();
	}

	public void replaceData(List<Repo> repoList) {
		this.repoList.clear();
		this.repoList.addAll(repoList);
		notifyDataSetChanged();
	}

	public Repo getItem(int position) {
		if (position < 0 || position >= repoList.size()) {
			throw new InvalidParameterException("Invalid item index");
		}
		return repoList.get(position);
	}

	public void clearData() {
		repoList.clear();
		notifyDataSetChanged();
	}
}
