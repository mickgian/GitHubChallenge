package link.mgiannone.githubchallenge.data;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import link.mgiannone.githubchallenge.data.api.BranchService;
import link.mgiannone.githubchallenge.data.api.HeaderInterceptor;
import link.mgiannone.githubchallenge.data.api.RepoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class GitHubChallengeApiServiceModule {
	private static final String BASE_URL = "base_url";

	@Provides
	@Named(BASE_URL)
	String provideBaseUrl() {
		return Config.GITHUB_API_HOST;
	}

	@Provides
	@Singleton
	HeaderInterceptor provideHeaderInterceptor() {
		return new HeaderInterceptor();
	}

	@Provides
	@Singleton
	HttpLoggingInterceptor provideHttpLoggingInterceptor() {
		return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);
	}

	@Provides
	@Singleton
	OkHttpClient provideHttpClient(HeaderInterceptor headerInterceptor,
								   HttpLoggingInterceptor httpInterceptor) {
		return new OkHttpClient.Builder().addInterceptor(headerInterceptor)
				.addInterceptor(httpInterceptor)
				.build();
	}

	@Provides
	@Singleton
	Converter.Factory provideGsonConverterFactory() {
		return GsonConverterFactory.create();
	}

	@Provides
	@Singleton
	CallAdapter.Factory provideRxJavaAdapterFactory() {
		return RxJava2CallAdapterFactory.create();
	}

	@Provides
	@Singleton
	Retrofit provideRetrofit(@Named(BASE_URL) String baseUrl, Converter.Factory converterFactory,
							 CallAdapter.Factory callAdapterFactory, OkHttpClient client) {
		return new Retrofit.Builder().baseUrl(baseUrl)
				.addConverterFactory(converterFactory)
				.addCallAdapterFactory(callAdapterFactory)
				.client(client)
				.build();
	}

	@Provides
	@Singleton
	RepoService provideRepositoryService(Retrofit retrofit) {
		return retrofit.create(RepoService.class);
	}

	@Provides
	@Singleton
	BranchService provideBranchesService(Retrofit retrofit) {
		return retrofit.create(BranchService.class);
	}

}

