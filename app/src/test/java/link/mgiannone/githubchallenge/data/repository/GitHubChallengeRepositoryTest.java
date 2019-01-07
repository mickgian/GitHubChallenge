package link.mgiannone.githubchallenge.data.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.prefs.PreferencesHelper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class GitHubChallengeRepositoryTest {

	private static final Repo repo1 = new Repo();
	private static final Repo repo2 = new Repo();
	private static final Repo repo3 = new Repo();
	private static final List<Repo> repositories = Arrays.asList(repo1, repo2, repo3);
	private static final List<Repo> singleRepo = Arrays.asList(repo1);

	public static final String OWNER = "owner";
	public static final String ACCESS_TOKEN_STRING = "access_token_string";
	public static final String ACCESS_TOKEN_TYPE = "access_token_type";
	public static final String PER_PAGE_VALUE = "per_page_value";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock @Local private RepoDataSource localReposDataSourceMock;

	@Mock @Remote private RepoDataSource remoteReposDataSourceMock;

	@Mock @Remote private BranchDataSource remoteBranchDataSourceMock;

	@Mock @Remote private CommitDataSource remoteCommitDataSourceMock;

	@Mock @Remote private AccessTokenDataSource remoteAccessTokenDataSourceMock;

	@Mock private PreferencesHelper preferencesHelperMock;

	private GitHubChallengeRepository repository;

	private TestObserver<List<Repo>> reposTestObserver;

	@Before
	public void setup() {

		repository = new GitHubChallengeRepository(
				localReposDataSourceMock,
				remoteReposDataSourceMock,
				remoteBranchDataSourceMock,
				remoteCommitDataSourceMock,
				remoteAccessTokenDataSourceMock,
				preferencesHelperMock);

		reposTestObserver = new TestObserver<>();
	}

	@Test
	public void repository_loadRepos_returnCacheIfItIsAvailableExpected() {
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
		verifyZeroInteractions(localReposDataSourceMock);
		verifyZeroInteractions(remoteReposDataSourceMock);

		reposTestObserver.assertValue(repositories);
	}

	@Test public void repository_loadRepos_returnFromLocalOrRemoteExpected() {
		// Given
		doReturn(Observable.just(repositories)).when(remoteReposDataSourceMock).loadRemoteRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE
		);

		// When
		repository.loadRemoteRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE
		).subscribe(reposTestObserver);

		// Then
		// Load from remote not from local storage
		verify(remoteReposDataSourceMock).loadRemoteRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE
		);
		verify(localReposDataSourceMock, never()).loadLocalRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE
		);
	}

	@Test public void repository_getRepo_returnFromCacheExpected() {
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
		then(localReposDataSourceMock).shouldHaveZeroInteractions();
		then(remoteReposDataSourceMock).shouldHaveZeroInteractions();
		// Should return correct repo
		subscriber.assertValue(repo1);
	}

	@Test public void repository_addRepos_addReposToCacheAndLocalDBExpected() {
		// When
		repository.addReposToCacheAndLocalDB(repo1);

		// Then
		assertThat(repository.repoCaches, is(singleRepo));
		then(localReposDataSourceMock).should().addRepo(repo1);
	}

	@Test public void repository_clearData_clearCachesAndLocalDataExpected() {
		// Given
		repository.repoCaches.addAll(repositories);

		// When
		repository.clearReposData();

		// Then
		assertThat(repository.repoCaches, empty());
		then(localReposDataSourceMock).should().clearReposData();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void repository_addRepo_throwExceptionExpected() {
		repository.addRepo(repo1);
	}

	@Test
	public void repository_getCurrentTempOwner_tempOwnerExpected() throws Exception{

		when(repository.getCurrentTempOwner()).thenReturn(OWNER);

		String result = repository.getCurrentTempOwner();

		assertThat(result, is(OWNER));

	}

	@Test
	public void repository__getAccessTokenString_accessTokenStringExpected() throws Exception{

		when(repository.getAccessTokenString()).thenReturn(ACCESS_TOKEN_STRING);

		String result = repository.getAccessTokenString();

		assertThat(result, is(ACCESS_TOKEN_STRING));

	}

	@Test
	public void repository_getAccessTokenType_accessTokenTypeExpected() throws Exception{

		when(repository.getAccessTokenType()).thenReturn(ACCESS_TOKEN_TYPE);

		String result = repository.getAccessTokenType();

		assertThat(result, is(ACCESS_TOKEN_TYPE));

	}



}

