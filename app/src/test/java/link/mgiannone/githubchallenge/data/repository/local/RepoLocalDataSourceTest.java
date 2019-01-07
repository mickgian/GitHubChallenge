package link.mgiannone.githubchallenge.data.repository.local;

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
import link.mgiannone.githubchallenge.data.database.RepoDao;
import link.mgiannone.githubchallenge.data.model.Repo;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

public class RepoLocalDataSourceTest {

	public static final String OWNER = "owner";
	public static final String ACCESS_TOKEN_STRING = "access_token_string";
	public static final String ACCESS_TOKEN_TYPE_STRING = "access_token_type_string";
	public static final String PER_PAGE_VALUE = "per_page_value";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	RepoDao repoDaoMock;

	private RepoLocalDataSource SUT;

	@Before public void setup() {
		SUT = new RepoLocalDataSource(repoDaoMock);
	}

	@Test
	public void repoLocalDataSource_checkReposPerUser_nullReturnedExpected() throws Exception {
		// When
		SUT.checkReposPerUser(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE_STRING,
				PER_PAGE_VALUE);

		// Then
		then(null);
	}

	@Test public void repoLocalDataSource_loadLocalData_returnFromDatabaseExpected() {
		// Given
		List<Repo> repos = Arrays.asList(new Repo(), new Repo());
		TestObserver<List<Repo>> subscriber = new TestObserver<>();
		given(repoDaoMock.getAllRepositories()).willReturn(Observable.just(repos));

		// When
		SUT.loadLocalRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE_STRING,
				PER_PAGE_VALUE).subscribe(subscriber);

		// Then
		then(repoDaoMock).should().getAllRepositories();
	}

	@Test public void repoLocalDataSource_loadRemoteData_returnNullExpected() {
		// When
		SUT.loadRemoteRepos(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE_STRING,
				PER_PAGE_VALUE);

		// Then
		then(null);
	}

	@Test public void repoLocalDataSource_addRepo_insertToDatabaseExpected() {
		// Given
		Repo repo = new Repo();

		// When
		SUT.addRepo(repo);

		// Then
		then(repoDaoMock).should().insert(repo);
	}

	@Test public void repoLocalDataSource_clearData_deleteAllDataInDatabaseExpected() {
		// When
		SUT.clearReposData();

		// Then
		then(repoDaoMock).should().deleteAll();
	}
}
