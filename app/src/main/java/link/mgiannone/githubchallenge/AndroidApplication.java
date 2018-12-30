package link.mgiannone.githubchallenge;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import com.squareup.leakcanary.LeakCanary;

import link.mgiannone.githubchallenge.data.DaggerGitHubChallengeRepositoryComponent;
import link.mgiannone.githubchallenge.data.GitHubChallengePrefModule;
import link.mgiannone.githubchallenge.data.GitHubChallengeRepositoryComponent;
import timber.log.Timber;

public class AndroidApplication extends Application {

  private GitHubChallengeRepositoryComponent gitHubChallengeRepositoryComponent;
  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();

    AndroidApplication.context = getApplicationContext();

    initializeDependencies();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
      Stetho.initializeWithDefaults(this);
    }

    if (LeakCanary.isInAnalyzerProcess(this)) {
      return;
    }
    LeakCanary.install(this);
  }

  private void initializeDependencies() {
    gitHubChallengeRepositoryComponent = DaggerGitHubChallengeRepositoryComponent.builder()
        .appModule(new AppModule(this))
        .gitHubChallengePrefModule(new GitHubChallengePrefModule(this))
        .build();
  }

  public GitHubChallengeRepositoryComponent getGitHubChallengeRepositoryComponent() {
    return gitHubChallengeRepositoryComponent;
  }

  //to get application context from any class
  public static Context getAppContext() {
    return AndroidApplication.context;
  }
}
