package link.mgiannone.githubchallenge.ui.repositories;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoriesPresenterModule {
	private RepositoriesContract.View view;

	public RepositoriesPresenterModule(RepositoriesContract.View view) {
		this.view = view;
	}

	@Provides
	public RepositoriesContract.View provideView() {
		return view;
	}
}
