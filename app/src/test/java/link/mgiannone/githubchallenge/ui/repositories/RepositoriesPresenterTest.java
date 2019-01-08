package link.mgiannone.githubchallenge.ui.repositories;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.GitHubChallengeRepository;


public class RepositoriesPresenterTest {

	public static final String REPO_NAME_1 = "repoName1";
	public static final String REPO_NAME_2 = "repoName2";
	public static final String REPO_NAME_3 = "repoName3";

	private static final Repo REPO1 = new Repo();
	private static final Repo REPO2 = new Repo();
	private static final Repo REPO3 = new Repo();
	private static final List<Repo> NO_REPOS = Collections.emptyList();
	private static final List<Repo> THREE_REPOS = Arrays.asList(REPO1, REPO2, REPO3);

	public static final String OWNER = "owner";
	public static final String ACCESS_TOKEN_STRING = "access_token_string";
	public static final String ACCESS_TOKEN_TYPE = "access_token_type";

	private static final String EXCEPTION_MESSAGE1 = "error";
	private static final String EXCEPTION_MESSAGE2 = "http 403 forbidden";


	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private GitHubChallengeRepository repositoryMock;

	@Mock
	private RepositoriesContract.View viewMock;

	private TestScheduler testScheduler;

	private RepositoriesPresenter SUT;  //System Under Test


	@Before public void setUp() {
		testScheduler = new TestScheduler();
		SUT = new RepositoriesPresenter(repositoryMock, viewMock, testScheduler, testScheduler);
	}


	@Test public void repoPresenter_searchRepo_filteredDataIsNotEmpty_showOnViewExpected() throws Exception{
		// Given
		REPO1.setName(REPO_NAME_1);
		REPO2.setName(REPO_NAME_2);
		REPO3.setName(REPO_NAME_3);
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.loadRemoteRepos(
				eq(OWNER),
				eq(ACCESS_TOKEN_STRING),
				eq(ACCESS_TOKEN_TYPE),
				anyString())).willReturn(Observable.just(THREE_REPOS));

		// When
		SUT.searchRepo(OWNER);
		testScheduler.triggerActions();

		// Then
		// Return a list of repos which should contains only question 1.
		then(viewMock).should().showRepos(THREE_REPOS);
	}

	@Test public void repoPresenter_searchRepo_filteredDataEmpty_showEmptyResultExpected() throws Exception{
		// Given
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.loadRemoteRepos(
				eq(OWNER),
				eq(ACCESS_TOKEN_STRING),
				eq(ACCESS_TOKEN_TYPE),
				anyString())).willReturn(Observable.just(NO_REPOS));

		// When
		SUT.searchRepo(OWNER);
		testScheduler.triggerActions();

		// Then
		// Return a list of repos which should contains only question 1.
		then(viewMock).should().clearRepos();
		then(viewMock).should().showEmptySearchResult();
	}

	@Test public void repoPresenter_remoteReposReturned_showReposOnViewExpected() throws Exception {
		// Given
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.loadRemoteRepos(
			eq(OWNER),
			eq(ACCESS_TOKEN_STRING),
			eq(ACCESS_TOKEN_TYPE),
			anyString())).willReturn(Observable.just(THREE_REPOS));

		// When
		SUT.presenterLoadRepos(true, OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().showRepos(THREE_REPOS);
	}

	@Test public void repoPresenter_handleReturnedData_showReposOnViewExpected() throws Exception {
		// Given
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.loadLocalRepos(
				eq(OWNER),
				eq(ACCESS_TOKEN_STRING),
				eq(ACCESS_TOKEN_TYPE),
				anyString())).willReturn(Observable.just(THREE_REPOS));

		// When
		SUT.presenterLoadRepos(false, OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().showRepos(THREE_REPOS);
	}

	@Test public void repoPresenter_handleReturnedData_showNoDataMessageExpected() throws Exception {
		// Given
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.loadRemoteRepos(
				eq(OWNER),
				eq(ACCESS_TOKEN_STRING),
				eq(ACCESS_TOKEN_TYPE),
				anyString())).willReturn(Observable.just(NO_REPOS));

		// When
		SUT.presenterLoadRepos(true, OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().showNoDataMessage();
	}

	@Test public void repoPresenter_handleError_showErrorMessageExpected() throws Exception {
		// Given
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.loadRemoteRepos(
				eq(OWNER),
				eq(ACCESS_TOKEN_STRING),
				eq(ACCESS_TOKEN_TYPE),
				anyString())).willReturn(Observable.error(
				new Exception(EXCEPTION_MESSAGE1)));

		// When
		SUT.presenterLoadRepos(true, OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().showErrorMessage(EXCEPTION_MESSAGE1);
	}

	@Test public void repoPresenter_handleError_startLoginExpected() throws Exception {
		// Given
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.loadRemoteRepos(
				eq(OWNER),
				eq(ACCESS_TOKEN_STRING),
				eq(ACCESS_TOKEN_TYPE),
				anyString())).willReturn(Observable.error(
				new Exception(EXCEPTION_MESSAGE2)));

		// When
		SUT.presenterLoadRepos(true, OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().startLogin();
	}

//	@Test public void repoPresenter_handleReturnedHeaderData_showErrorMessageExpected() throws Exception {
//		// Given
//		ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "justError");
//		String message = "justError";
//		okhttp3.Response.Builder builder = new okhttp3.Response.Builder();
//		okhttp3.Response rawResponse = builder.build();
//		Response<List<Headers>> response = Response.error(body, rawResponse);
//		String message2 = body.string();
//
//		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
//		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
//		given(repositoryMock.checkReposPerUser(
//				eq(OWNER),
//				eq(ACCESS_TOKEN_STRING),
//				eq(ACCESS_TOKEN_TYPE),
//				anyString())).willReturn(Observable.just(response));
//
//		// When
//		SUT.checkRepoPerUser(OWNER);
//		testScheduler.triggerActions();
//
//		// Then
//		then(viewMock).should().showErrorMessage(message);
//	}

	@Test public void repoPresenter_handleReturnedHeaderData_startLoginExpected() throws Exception {
		// Given
		ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "");
		Response<List<Headers>> response = Response.error(403, body);

		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.checkReposPerUser(
				eq(OWNER),
				eq(ACCESS_TOKEN_STRING),
				eq(ACCESS_TOKEN_TYPE),
				anyString())).willReturn(Observable.just(response));

		// When
		SUT.checkRepoPerUser(OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().startLogin();
	}

	@Test public void repoPresenter_handleHeaderError_showErrorMessageExpected() throws Exception {
		// Given
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.checkReposPerUser(
				eq(OWNER),
				eq(ACCESS_TOKEN_STRING),
				eq(ACCESS_TOKEN_TYPE),
				anyString())).willReturn(Observable.error(
				new Exception(EXCEPTION_MESSAGE1)));

		// When
		SUT.checkRepoPerUser(OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().showErrorMessage(EXCEPTION_MESSAGE1);
	}



}
