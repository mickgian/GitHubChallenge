package link.mgiannone.githubchallenge.data.repository.remote;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import link.mgiannone.githubchallenge.data.api.RepoService;
import link.mgiannone.githubchallenge.data.model.Repo;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


public class RepoRemoteDataSourceTest  {

	public static final String OWNER = "owner";
	public static final String ACCESS_TOKEN_STRING = "access_token_string";
	public static final String ACCESS_TOKEN_TYPE = "access_token_type";
	public static final String PER_PAGE_VALUE = "per_page_value";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	RepoService repoServiceMock;

	private RepoRemoteDataSource SUT;

	@Before
	public void setUp() throws Exception {
		SUT = new RepoRemoteDataSource(repoServiceMock);
	}

	@Test
	public void repoRemoteDataSource_checkReposPerUser_headerFromRemoteServiceExpected() throws Exception {
		// Given
		// When
		// Then
	}

	@Test
	public void repoRemoteDataSource_loadRemoteData_observableListRepoFromRemoteServiceExpected() throws Exception {
		// Given
		List<Repo> repos = Arrays.asList(new Repo(), new Repo());
		TestObserver<List<Repo>> subscriber = new TestObserver<>();
		given(repoServiceMock.loadRepositories(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE)).willReturn(Observable.just(repos));
		// When
		SUT.loadRemoteRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE).subscribe(subscriber);
		// Then
		then(repoServiceMock).should().loadRepositories(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void repoRemoteDataSource_loadLocalRepo_throwsUnsupportedOperationExpected() throws Exception {
		// Given
		TestObserver<List<Repo>> subscriber = new TestObserver<>();
		// When
		SUT.loadLocalRepos(OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE).subscribe(subscriber);
		// Then
		then(repoServiceMock).shouldHaveZeroInteractions();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void repoRemoteDataSource_addRepo_throwsUnsupportedOperationExpected() throws Exception {
		// Given
		Repo repo = new Repo();
		// When
		SUT.addRepo(repo);
		// Then
		then(repoServiceMock).shouldHaveZeroInteractions();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void repoRemoteDataSource_clearReposData_throwsUnsupportedOperationExpected() throws Exception {
		// When
		SUT.clearReposData();
		// Then
		then(repoServiceMock).shouldHaveZeroInteractions();
	}
}
