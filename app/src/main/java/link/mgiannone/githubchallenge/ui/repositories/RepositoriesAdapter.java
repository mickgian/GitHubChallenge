package link.mgiannone.githubchallenge.ui.repositories;

import android.content.Context;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.security.InvalidParameterException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import link.mgiannone.githubchallenge.R;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.ui.base.BaseRecyclerViewAdapter;

class RepositoriesAdapter extends BaseRecyclerViewAdapter<RepositoriesAdapter.PropertyViewHolder> {

	class PropertyViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.repo_title_text_view)
		TextView titleText;
		@BindView(R.id.repo_code_text_view) TextView userText;
		@BindView(R.id.repo_address_text_view) TextView createdTimeText;
		@BindView(R.id.repo_image_profile)
		ImageView profileImage;

		public PropertyViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	private List<Repo> properties;
	private Context context;

	public RepositoriesAdapter(@NonNull List<Repo> properties, Context context) {
		this.properties = properties;
		this.context = context;
	}

	@Override public PropertyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.item_repositories, viewGroup, false);
		return new PropertyViewHolder(view);
	}

	@Override public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
		super.onBindViewHolder(viewHolder, i);
		PropertyViewHolder vh = (PropertyViewHolder) viewHolder; //safe cast
		Repo repo = properties.get(i);
		vh.titleText.setText(repo.getName());
		vh.userText.setText(repo.getUrl());
		vh.createdTimeText.setText(repo.getDescription());
		Glide.with(vh.profileImage)
				.load(ResourcesCompat.getDrawable(context.getResources(), R.drawable.all_ita, null))
				.into(vh.profileImage);
	}

	@Override public int getItemCount() {
		return properties.size();
	}

	public void replaceData(List<Repo> properties) {
		this.properties.clear();
		this.properties.addAll(properties);
		notifyDataSetChanged();
	}

	public Repo getItem(int position) {
		if (position < 0 || position >= properties.size()) {
			throw new InvalidParameterException("Invalid item index");
		}
		return properties.get(position);
	}

	public void clearData() {
		properties.clear();
		notifyDataSetChanged();
	}
}
