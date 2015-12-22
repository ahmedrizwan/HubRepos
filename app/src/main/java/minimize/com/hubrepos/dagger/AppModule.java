package minimize.com.hubrepos.dagger;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import minimize.com.hubrepos.R;
import minimize.com.hubrepos.realm.converters.QLRealmStringAdapter;
import minimize.com.hubrepos.realm.converters.RealmString;
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
    public Realm providesRealm(Context context) {
        //Realm Db
        RealmConfiguration config = new RealmConfiguration.Builder(context).build();
        Realm.setDefaultConfiguration(config);
        return Realm.getDefaultInstance();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(Context context, Gson gson){
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(context.getString(R.string.base_url))
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    public Gson providesGsonForRealm() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass()
                                .equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(RealmString.class, new QLRealmStringAdapter())
                .create();
    }

}