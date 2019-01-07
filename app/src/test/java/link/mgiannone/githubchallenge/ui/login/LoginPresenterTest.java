package link.mgiannone.githubchallenge.ui.login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import link.mgiannone.githubchallenge.data.repository.GitHubChallengeRepository;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

	// region constants
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String CODE = "code";
	// endregion constants

	// region helper
	@Mock
	private GitHubChallengeRepository repository;
	// endregion helper fields

	LoginPresenter SUT;

	@Before
	public void setUp() throws Exception {
		SUT = new LoginPresenter(repository);
	}

	@Test
	public void loginPresenter_success_parametersPassedToRepositoryMethod() throws Exception {
		// Arrange
		ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
		// Act
		SUT.getAccessToken(CLIENT_ID, CLIENT_SECRET, CODE);
		verify(repository, times(1)).recoverAccessToken(ac.capture(), ac.capture(), ac.capture());
		// Assert
		List<String> captures = ac.getAllValues();
		assertThat(captures.get(0), is(CLIENT_ID));
		assertThat(captures.get(1), is(CLIENT_SECRET));
		assertThat(captures.get(2), is(CODE));
	}
}
