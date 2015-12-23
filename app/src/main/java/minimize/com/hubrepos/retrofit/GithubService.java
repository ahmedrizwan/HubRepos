package minimize.com.hubrepos.retrofit;

import java.util.List;

import minimize.com.hubrepos.models.ContributorStats;
import minimize.com.hubrepos.models.Issue;
import minimize.com.hubrepos.models.Repo;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by ahmedrizwan on 21/12/2015.
 */
public interface GithubService {

    @GET("search/repositories?")
    Observable<Repo> getRepositories(@Query("q") String language);

    @GET("repos/{owner}/{name}/stats/contributors")
    Observable<List<ContributorStats>> getContributorsStats(@Path("owner") String owner, @Path("name") String repoName);

    @GET("repos/{owner}/{name}/issues")
    Observable<List<Issue>> getIssueEvents(@Path("owner") String owner, @Path("name") String repoName);
}
