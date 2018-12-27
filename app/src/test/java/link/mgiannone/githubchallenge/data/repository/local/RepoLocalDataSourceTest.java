package link.mgiannone.githubchallenge.data.repository.local;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import link.mgiannone.githubchallenge.data.database.RepoDao;
import link.mgiannone.githubchallenge.data.model.Repo;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class RepoLocalDataSourceTest {

	// region constants
	public static final String OWNER = "owner";
	public static final String ACCESS_TOKEN_STRING = "access_token_string";
	public static final String ACCESS_TOKEN_TYPE_STRING = "access_token_type_string";
	public static final String PER_PAGE_VALUE = "per_page_value";
	// endregion constants

	// region helper fields
	@Mock
	RepoDao repoDaoMock;
	// endregion helper fields

	private RepoLocalDataSource SUT; //SUT stands for System Under Test

	@Before public void setup() {
		SUT = new RepoLocalDataSource(repoDaoMock);
	}

	@Test
	public void repoLocalDataSource_checkReposPerUser_nullReturnedExpected() throws Exception {
		// Act
		SUT.checkReposPerUser(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE_STRING,
				PER_PAGE_VALUE);

		// Assert
		then(null);
	}

	@Test public void repoLocalDataSource_loadLocalData_returnFromDatabaseExpected() {
		// Arrange
		List<Repo> repos = Arrays.asList(new Repo(), new Repo());
		TestObserver<List<Repo>> subscriber = new TestObserver<>();
		given(repoDaoMock.getAllRepositories()).willReturn(Observable.just(repos));

		// Act
		SUT.loadLocalRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE_STRING,
				PER_PAGE_VALUE).subscribe(subscriber);

		// Assert
		then(repoDaoMock).should().getAllRepositories();
	}

	@Test public void repoLocalDataSource_loadRemoteData_returnNullExpected() {
		// Act
		SUT.loadRemoteRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE_STRING,
				PER_PAGE_VALUE);

		// Assert
		then(null);
	}

	@Test public void repoLocalDataSource_addRepo_insertToDatabaseExpected() {
		// Arrange
		Repo repo = new Repo();

		// Act
		SUT.addRepo(repo);

		// Assert
		then(repoDaoMock).should().insert(repo);
	}

	@Test public void repoLocalDataSource_clearData_deleteAllDataInDatabaseExpected() {
		// Act
		SUT.clearReposData();

		// Assert
		then(repoDaoMock).should().deleteAll();
	}
}
