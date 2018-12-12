package link.mgiannone.githubchallenge.data.api;

import java.util.List;

import io.reactivex.Flowable;
import link.mgiannone.githubchallenge.data.model.Repo;
import retrofit2.http.GET;

public interface RepoService {

	@GET("/easyestate/Property/GetProperty")
	Flowable<List<Repo>> loadRepositories();

}
