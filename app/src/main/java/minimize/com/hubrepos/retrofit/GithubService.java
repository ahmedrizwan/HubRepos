package minimize.com.hubrepos.retrofit;

import minimize.com.hubrepos.realm.Repo;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by ahmedrizwan on 21/12/2015.
 */
public interface GithubService {

    @GET("search/repositories?q=language:java")
    Observable<Repo> getRepositories();

}
