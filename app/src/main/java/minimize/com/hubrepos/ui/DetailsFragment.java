package minimize.com.hubrepos.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import javax.inject.Inject;

import minimize.com.hubrepos.R;
import minimize.com.hubrepos.dagger.AppComponent;
import minimize.com.hubrepos.dagger.AppModule;
import minimize.com.hubrepos.dagger.DaggerAppComponent;
import minimize.com.hubrepos.databinding.FragmentDetailsBinding;
import minimize.com.hubrepos.realm.ContributorStats;
import minimize.com.hubrepos.realm.Issue;
import minimize.com.hubrepos.realm.Item;
import minimize.com.hubrepos.retrofit.GithubService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class DetailsFragment extends DialogFragment {


    FragmentDetailsBinding mBinding;

    @Inject
    GithubService mGithubService;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        Parcelable itemParcelable = getArguments().getParcelable("item");
        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getActivity().getApplication()))
                .build();
        appComponent.inject(this);

        Item item = Parcels.unwrap(itemParcelable);
        if (item != null) {
            String owner = item.getOwner()
                    .getLogin();
            String repoName = item.getName();
            mGithubService.getContributorsStats(owner, repoName)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .takeLast(3)
                    .subscribe(contributorStatses -> {
                        String contributors = "";
                        for (ContributorStats contributorStatse : contributorStatses) {
                            contributors += contributorStatse.getAuthor()
                                    .getLogin() + "\n";
                        }
                        mBinding.textViewThreeTopContributors.setText(contributors);
                    });

            mGithubService.getIssueEvents(owner, repoName)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(issueEvents -> {
                        Timber.e("Issues : "+issueEvents.size());
                        String issues = "";
                        for (Issue issueEvent : issueEvents) {
                            if (issueEvent.getState()
                                    .equals("open") || issueEvent.getState()
                                    .equals("reopened"))
                                issues += issueEvent.getTitle()+ "\n";
                        }
                        mBinding.textViewThreeIssues.setText(issues);
                    });
            mBinding.textViewName.setText(repoName);
            mBinding.textViewDescription.setText(item.getDescription());
        }
        return mBinding.getRoot();
    }
}
