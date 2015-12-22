package minimize.com.hubrepos.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimize.android.rxrecycleradapter.RxAdapter;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import minimize.com.hubrepos.R;
import minimize.com.hubrepos.dagger.AppComponent;
import minimize.com.hubrepos.dagger.AppModule;
import minimize.com.hubrepos.dagger.DaggerAppComponent;
import minimize.com.hubrepos.databinding.FragmentDetailsBinding;
import minimize.com.hubrepos.databinding.ItemContributorBinding;
import minimize.com.hubrepos.realm.ContributorStats;
import minimize.com.hubrepos.realm.Item;
import minimize.com.hubrepos.retrofit.GithubService;
import rx.Observable;
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
            mBinding.textViewName.setText(repoName);
            mBinding.textViewDescription.setText(item.getDescription());
            mBinding.textViewStarGazzers.setText(String.valueOf(item.getStargazersCount()));
            //set layout managers for the recyclerViews
            mBinding.recyclerViewContributors.setLayoutManager(new LinearLayoutManager(getActivity()));
            mBinding.recyclerViewIssues.setLayoutManager(new LinearLayoutManager(getActivity()));
            //get issues from the api
            mGithubService.getContributorsStats(owner, repoName)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::fillInListWithContributors, throwable -> {
                        Timber.e("Contributors : " + throwable.getMessage());
                    });

//            mGithubService.getIssueEvents(owner, repoName)
//                    .subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .take(3)
//                    .subscribe(issueEvents -> {
//                        Timber.e("Issues : " + issueEvents.size());
//                        String issues = "";
//                        for (Issue issueEvent : issueEvents) {
//                            if (issueEvent.getState()
//                                    .equals("open") || issueEvent.getState()
//                                    .equals("reopened"))
//                                issues += issueEvent.getTitle() + "\n";
//                        }
//                        mBinding.textViewThreeIssues.setText(issues);
//                    }, throwable -> {
//                        Timber.e("Issue : " + throwable.getMessage());
//                    });

        }
        return mBinding.getRoot();
    }

    private void fillInListWithContributors(final List<ContributorStats> contributorStats) {
        List<ContributorStats> first = Observable.from(contributorStats)
                .takeLast(3)
                .toList()
                .toBlocking()
                .first();
        Collections.reverse(first);
        RxAdapter<ContributorStats, ItemContributorBinding> rxAdapter = new RxAdapter<>(R.layout.item_contributor, first);
        rxAdapter.asObservable()
                .subscribe(viewHolder -> {
                    ItemContributorBinding viewDataBinding = viewHolder.getViewDataBinding();
                    ContributorStats contributorStats1 = viewHolder.getItem();
                    viewDataBinding.textViewContributor.setText(contributorStats1.getAuthor()
                            .getLogin());
                    viewDataBinding.textViewTotal.setText(String.valueOf(contributorStats1.getTotal()));
                });
        mBinding.recyclerViewContributors.setAdapter(rxAdapter);
    }
}
