package link.mgiannone.githubchallenge.data.repository.remote;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import link.mgiannone.githubchallenge.data.api.RepoService;
import link.mgiannone.githubchallenge.data.model.Repo;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@RunWith(MockitoJUnitRunner.class)
public class RepoRemoteDataSourceTest  {

	// region constants
	public static final String OWNER = "owner";
	public static final String ACCESS_TOKEN_STRING = "access_token_string";
	public static final String ACCESS_TOKEN_TYPE = "access_token_type";
	public static final String PER_PAGE_VALUE = "per_page_value";
	// endregion constants

	// region helper
	@Mock
	RepoService repoServiceMock;
	// endregion helper fields

	private RepoRemoteDataSource SUT;

	@Before
	public void setUp() throws Exception {
		SUT = new RepoRemoteDataSource(repoServiceMock);
	}

	@Test
	public void repoRemoteDataSource_checkReposPerUser_headerFromRemoteServiceExpected() throws Exception {
		// Arrange
		// Act
		// Assert
	}

	@Test
	public void repoRemoteDataSource_loadRemoteData_observableListRepoFromRemoteServiceExpected() throws Exception {
		// Arrange
		List<Repo> repos = Arrays.asList(new Repo(), new Repo());
		TestObserver<List<Repo>> subscriber = new TestObserver<>();
		given(repoServiceMock.loadRepositories(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE)).willReturn(Observable.just(repos));
		// Act
		SUT.loadRemoteRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE).subscribe(subscriber);
		// Assert
		then(repoServiceMock).should().loadRepositories(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void repoRemoteDataSource_addRepo_throwsUnsupportedOperationExpected() throws Exception {
		// Arrange
		Repo repo = new Repo();
		// Act
		SUT.addRepo(repo);
		// Assert
		then(repoServiceMock).shouldHaveZeroInteractions();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void repoRemoteDataSource_clearReposData_throwsUnsupportedOperationExpected() throws Exception {
		// Act
		SUT.clearReposData();
		// Assert
		then(repoServiceMock).shouldHaveZeroInteractions();
	}
}
