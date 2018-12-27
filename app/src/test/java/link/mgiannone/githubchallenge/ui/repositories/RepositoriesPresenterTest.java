package link.mgiannone.githubchallenge.ui.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.GitHubChallengeRepository;

@RunWith(MockitoJUnitRunner.class)
public class RepositoriesPresenterTest {
	private static final Repo REPO1 = new Repo();
	private static final Repo REPO2 = new Repo();
	private static final Repo REPO3 = new Repo();
	private static final List<Repo> NO_REPOS = Collections.emptyList();
	private static final List<Repo> THREE_REPOS =
			Arrays.asList(REPO1, REPO2, REPO3);

	@Parameterized.Parameters
	public static Object[] data() {
		return new Object[] {NO_REPOS, THREE_REPOS};
	}

	@Parameterized.Parameter
	public List<Repo> questions;

	@Mock private GitHubChallengeRepository repository;

	@Mock private RepositoriesContract.View view;

	private TestScheduler testScheduler;

	private RepositoriesPresenter presenter;

	@Before public void setUp() {
		MockitoAnnotations.initMocks(this);
		testScheduler = new TestScheduler();
		presenter = new RepositoriesPresenter(repository, view, testScheduler, testScheduler);
	}


	@Test public void search_EmptyMessageShouldBeShownOnView_WhenNoDataMatchesQuery() {
		// Given
		REPO1.setName("activity onCreate");
		REPO2.setName("activity onDestroy");
		REPO3.setName("fragment");
		given(repository.loadRemoteRepos(
				"owner",
				"access_token_string",
				"access_token_type",
				"per_page_value")).willReturn(Observable.just(NO_REPOS));

		// When
		presenter.searchRepo("invalid question");
		testScheduler.triggerActions();

		// Then
		then(view).should().clearRepos();
		then(view).should().showEmptySearchResult();
		then(view).shouldHaveNoMoreInteractions();
	}
}
