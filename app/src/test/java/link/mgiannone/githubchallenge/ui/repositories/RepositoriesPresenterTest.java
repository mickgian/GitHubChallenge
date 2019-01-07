package link.mgiannone.githubchallenge.ui.repositories;

import android.net.Uri;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import link.mgiannone.githubchallenge.data.model.Owner;
import link.mgiannone.githubchallenge.data.model.Repo;
import link.mgiannone.githubchallenge.data.repository.GitHubChallengeRepository;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;

public class RepositoriesPresenterTest {

	@BeforeClass
	public static void setupClass() {
		RxAndroidPlugins.setInitMainThreadSchedulerHandler(
				__ -> Schedulers.trampoline());
	}

	private static final Repo REPO1 = new Repo();
	private static final Repo REPO2 = new Repo();
	private static final Repo REPO3 = new Repo();
	private static final List<Repo> NO_REPOS = Collections.emptyList();
	private static final List<Repo> THREE_REPOS = Arrays.asList(REPO1, REPO2, REPO3);

	public static final String OWNER = "owner";
	public static final String ACCESS_TOKEN_STRING = "access_token_string";
	public static final String ACCESS_TOKEN_TYPE = "access_token_type";
	public static final String PER_PAGE_VALUE = "per_page_value";

	@Parameterized.Parameters
	public static Object[] data() {
		return new Object[] {NO_REPOS, THREE_REPOS};
	}

	@Parameterized.Parameter
	public List<Repo> repos;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock private GitHubChallengeRepository repositoryMock;

	@Mock private RepositoriesContract.View viewMock;

	@Mock private Response<List<Headers>> responseListHeaders;


	private TestScheduler testScheduler;

	private RepositoriesPresenter SUT;  //System Under Test

	@Before public void setUp() {
		testScheduler = new TestScheduler();
		SUT = new RepositoriesPresenter(repositoryMock, viewMock, testScheduler, testScheduler);
	}


	@Test public void repoPresenter_resultShouldBeShownOnView_WhenFilteredDataIsNotEmpty() throws Exception{
		// Given
		Owner owner = new Owner();
		owner.setLogin("owner");
		REPO1.setOwner(owner);
		REPO2.setOwner(owner);
		REPO3.setOwner(owner);
		given(repositoryMock.loadRemoteRepos(
				"owner",
				"access_token_string",
				"access_token_type",
				"per_page_value")).willReturn(Observable.just(THREE_REPOS));

		// When
		SUT.searchRepo("owner");
		testScheduler.triggerActions();

		// Then
		// Return a list of repos which should contains only question 1.
		then(viewMock).should().showRepos(Arrays.asList(REPO1, REPO2, REPO3));
		then(viewMock).shouldHaveNoMoreInteractions();
	}

//	@Test
//	public void repoPresenter_checkRepoPerUser_responseHeadersListExpected() throws InterruptedException, IOException {
//
//
//		MockWebServer mockWebServer = new MockWebServer();
//
//		TestObserver testObserver = new TestObserver<Response<List<Headers>>>();
//
//		String path = "\"users/{username}/repos\"";
//
//		// Mock a response with status 200 and sample JSON output
//		MockResponse mockResponse = new MockResponse()
//				.setResponseCode(200)
//				.setBody("{}")
//				.setHeader("Status", "status_value")
//				.setHeader("X-RateLimit-Limit", "60")
//				.setHeader("X-RateLimit-Remaining", "57");
//		// Enqueue request
//		mockWebServer.enqueue(mockResponse);
//
//		// Call the API
//		repositoryMock.checkReposPerUser(
//				OWNER,
//				ACCESS_TOKEN_STRING,
//				ACCESS_TOKEN_TYPE,
//				PER_PAGE_VALUE).subscribe((Consumer<? super Response<List<Headers>>>) Observable.just(Arrays.asList(mockResponse.getHeaders()));
//
//		testScheduler.triggerActions();
//
//		testObserver.awaitTerminalEvent(2, TimeUnit.SECONDS);
//
//		// No errors
//		testObserver.assertNoErrors();
//
//		// Make sure we made the request to the required path
//		assertEquals("60", mockResponse.getHeaders().get("X-RateLimit-Limit"));
//
//
//
//		// When
//		SUT.checkRepoPerUser(OWNER);
//		testScheduler.triggerActions();
//
//		// Then
//		then(viewMock).should().getOwner();
//
//		// Shut down the server. Instances cannot be reused.
//		mockWebServer.shutdown();
//	}

	//	@Test public void repoPresenter_checkRepoPerUser_responseHeadersListExpected() throws Exception {
//
//		// Mock a response with status 200 and some Headers
//		MockResponse mockResponse = new MockResponse()
//				.setResponseCode(200)
//				.setBody("{}")
//				.setHeader("Status", "status_value")
//				.setHeader("X-RateLimit-Limit", "60")
//				.setHeader("X-RateLimit-Remaining", "57");
//		List<Headers> headersList = Arrays.asList(mockResponse.getHeaders());
//
//		// Given
//		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
//		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
//		given(repositoryMock.checkReposPerUser(
//				OWNER,
//				ACCESS_TOKEN_STRING,
//				ACCESS_TOKEN_TYPE,
//				PER_PAGE_VALUE)).willReturn((Observable<Response<List<Headers>>>) Observable.just(headersList));
//
//		// When
//		SUT.checkRepoPerUser(OWNER);
//		testScheduler.triggerActions();
//
//		// Then
//		then(viewMock).should().getOwner();
//
//	}

	@Test public void repoPresenter_reposReturned_showReposOnViewExpected() throws Exception {
		// Given
		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.loadRemoteRepos(
			OWNER,
			ACCESS_TOKEN_STRING,
			ACCESS_TOKEN_TYPE,
			PER_PAGE_VALUE)).willReturn(Observable.just(THREE_REPOS));

		// When
		SUT.presenterLoadRepos(true, OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().showRepos(THREE_REPOS);

	}

	@Test public void repoPresenter_checkRepoPerUser_responseHeadersListExpected() throws Exception {
		// Given
		ResponseBody theBody = ResponseBody.create(MediaType.parse("text/html"), "");
		Response<List<Headers>> response = Response.error(403, theBody);

		given(repositoryMock.getAccessTokenString()).willReturn(ACCESS_TOKEN_STRING);
		given(repositoryMock.getAccessTokenType()).willReturn(ACCESS_TOKEN_TYPE);
		given(repositoryMock.checkReposPerUser(
				OWNER,
				ACCESS_TOKEN_STRING,
				ACCESS_TOKEN_TYPE,
				PER_PAGE_VALUE)).willReturn(Observable.just(response));

		// When
		SUT.checkRepoPerUser(OWNER);
		testScheduler.triggerActions();

		// Then
		then(viewMock).should().startLogin();

	}



}
