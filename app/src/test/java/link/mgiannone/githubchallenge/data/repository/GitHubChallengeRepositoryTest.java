package link.mgiannone.githubchallenge.data.repository;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import link.mgiannone.githubchallenge.data.model.Repo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class GitHubChallengeRepositoryTest {
	private static final Repo repo1 = new Repo();
	private static final Repo repo2 = new Repo();
	private static final Repo repo3 = new Repo();
	private static final List<Repo> repositories = Arrays.asList(repo1, repo2, repo3);

	@Mock @Local private RepoDataSource localReposDataSource;

	@Mock @Remote private RepoDataSource remoteReposDataSource;

	@Mock @Remote private BranchDataSource remoteBranchDataSource;

	@Mock @Remote private CommitDataSource remoteCommitDataSource;

	@Mock @Remote private AccessTokenDataSource remoteAccessTokenDataSource;

	private GitHubChallengeRepository repository;

	private TestObserver<List<Repo>> reposTestObserver;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		repository = new GitHubChallengeRepository(localReposDataSource, remoteReposDataSource, remoteBranchDataSource, remoteCommitDataSource, remoteAccessTokenDataSource);

		reposTestObserver = new TestObserver<>();
	}

	@Test
	public void loadRepos_returnCache_IfItIsAvailableExpected() {
		// Given
		repository.repoCaches.addAll(repositories);

		// When
		repository.loadLocalRepos(
				"owner",
				"access_token_string",
				"access_token_type",
				"per_page_value").subscribe(reposTestObserver);

		// Then
		// No interaction with local storage or remote source
		verifyZeroInteractions(localReposDataSource);
		verifyZeroInteractions(remoteReposDataSource);

		reposTestObserver.assertValue(repositories);
	}


	@Test public void getRepo_returnFromCacheExpected() {
		// Given
		repo1.setId(1);
		repo2.setId(2);
		repo3.setId(3);
		repository.repoCaches.addAll(repositories);
		TestObserver<Repo> subscriber = new TestObserver<>();

		// When
		repository.getRepo(1).subscribe(subscriber);

		// Then
		// No interaction with local storage or remote source
		then(localReposDataSource).shouldHaveZeroInteractions();
		then(remoteReposDataSource).shouldHaveZeroInteractions();
		// Should return correct repo
		subscriber.assertValue(repo1);
	}

	@Test public void refreshData_clearOldDataFromLocalExpected() {
		// Given
		given(remoteReposDataSource.loadRemoteRepos(
				"owner",
				"access_token_string",
				"access_token_type",
				"per_page_value")).willReturn(Observable.just(repositories));

		// When
		repository.loadRemoteRepos("owner",
				"access_token_string",
				"access_token_type",
				"per_page_value").subscribe(reposTestObserver);

		// Then
		then(localReposDataSource).should().clearReposData();
	}

	@Test public void refreshData_addNewDataToCacheExpected() {
		// Given
		given(remoteReposDataSource.loadRemoteRepos("owner",
				"access_token_string",
				"access_token_type",
				"per_page_value")).willReturn(Observable.just(repositories));

		// When
		repository.loadRemoteRepos("owner",
				"access_token_string",
				"access_token_type",
				"per_page_value").subscribe(reposTestObserver);

		// Then
		assertThat(repository.repoCaches, equalTo(repositories));
	}

	//TODO refactor this
	@Test public void refreshData_addNewDataToLocalExpected() {
		// Given
		given(remoteReposDataSource.loadRemoteRepos("owner",
				"access_token_string",
				"access_token_type",
				"per_page_value")).willReturn(Observable.just(repositories));

		// When
		repository.loadRemoteRepos("owner",
				"access_token_string",
				"access_token_type",
				"per_page_value").subscribe(reposTestObserver);

		// Then
		assertThat(repository.repoCaches, empty());
//		then(localReposDataSource).should().addRepo(repo1);
//		then(localReposDataSource).should().addRepo(repo2);
//		then(localReposDataSource).should().addRepo(repo3);
	}

	@Test public void clearData_clearCachesAndLocalDataExpected() {
		// Given
		repository.repoCaches.addAll(repositories);

		// When
		repository.clearReposData();

		// Then
		assertThat(repository.repoCaches, empty());
		then(localReposDataSource).should().clearReposData();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void addRepo_throwExceptionExpected() {
		repository.addRepo(repo1);
	}
}

