package minimize.com.hubrepos.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import minimize.com.hubrepos.R;
import minimize.com.hubrepos.retrofit.GithubService;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

@Module
public class AppModule {

    private Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public Context providesContext() {
        return mContext;
    }


    @Provides
    @Singleton
    public Retrofit providesRetrofit(Context context){
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.getString(R.string.base_url))
                .build();
    }

    @Provides
    @Singleton
    public GithubService providesGithubService(Retrofit retrofit){
        return retrofit.create(GithubService.class);
    }

}