package minimize.com.hubrepos.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

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
import minimize.com.hubrepos.databinding.IncludeProgressBinding;
import minimize.com.hubrepos.databinding.ItemContributorBinding;
import minimize.com.hubrepos.databinding.ItemIssueBinding;
import minimize.com.hubrepos.realm.ContributorStats;
import minimize.com.hubrepos.realm.Issue;
import minimize.com.hubrepos.realm.Item;
import minimize.com.hubrepos.retrofit.GithubService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class DetailsFragment extends DialogFragment {


    FragmentDetailsBinding mBinding;
    IncludeProgressBinding mProgressContributorsBinding;
    IncludeProgressBinding mProgressIssuesBinding;

    @Inject
    GithubService mGithubService;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);
        mProgressContributorsBinding = DataBindingUtil.bind(mBinding.getRoot()
                .findViewById(R.id.progressLayoutContributors));
        mProgressIssuesBinding = DataBindingUtil.bind(mBinding.getRoot()
                .findViewById(R.id.progressLayoutIssues));

        boolean twoPane = ((ContainerActivity) getActivity()).isTwoPane();
        if (twoPane) {
            //make it full screen
            getDialog().getWindow()
                    .requestFeature(Window.FEATURE_NO_TITLE);
        }

        Parcelable itemParcelable = getArguments().getParcelable(getString(R.string.item));
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
            mBinding.textViewOwner.setText(item.getOwner()
                    .getLogin());
            //set layout managers for the recyclerViews
            mBinding.recyclerViewContributors.setLayoutManager(new minimize.com.hubrepos.ui.LinearLayoutManager(getActivity()));
            mBinding.recyclerViewIssues.setLayoutManager(new minimize.com.hubrepos.ui.LinearLayoutManager(getActivity()));
            //get issues from the api
            mGithubService.getContributorsStats(owner, repoName)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::fillInListWithContributors, throwable -> {
                        mProgressContributorsBinding.progressBar.setVisibility(View.GONE);
                        mProgressContributorsBinding.textViewError.setText(R.string.error_contributors);
                        mProgressContributorsBinding.textViewError.setVisibility(View.VISIBLE);
                    });

            mGithubService.getIssueEvents(owner, repoName)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::fillInListWithIssues, throwable -> {
                        mProgressIssuesBinding.progressBar.setVisibility(View.GONE);
                        mProgressIssuesBinding.textViewError.setText(R.string.error_issues);
                        mProgressIssuesBinding.textViewError.setVisibility(View.VISIBLE);
                    });

        }
        return mBinding.getRoot();
    }

    private void fillInListWithIssues(final List<Issue> issueEvents) {
        if(issueEvents.size()>0) {
            List<Issue> filteredIssues = Observable.from(issueEvents)
                    .take(3)
                    .toList()
                    .toBlocking()
                    .first();
            RxAdapter<Issue, ItemIssueBinding> rxAdapter = new RxAdapter<>(R.layout.item_issue, filteredIssues);
            rxAdapter.asObservable()
                    .subscribe(viewHolder -> {
                        ItemIssueBinding viewDataBinding = viewHolder.getViewDataBinding();
                        Issue issue = viewHolder.getItem();
                        viewDataBinding.textViewIssue.setText(issue.getTitle());
                    });
            mBinding.recyclerViewIssues.setAdapter(rxAdapter);
        }else {
            mProgressIssuesBinding.textViewError.setText(R.string.no_issues);
            mProgressIssuesBinding.textViewError.setVisibility(View.VISIBLE);
        }
        mProgressIssuesBinding.progressBar.setVisibility(View.GONE);
    }

    private void fillInListWithContributors(final List<ContributorStats> contributorStats) {
        if(contributorStats.size()>0) {
            List<ContributorStats> filteredContributors = Observable.from(contributorStats)
                    .takeLast(3)
                    .toList()
                    .toBlocking()
                    .first();
            Collections.reverse(filteredContributors);
            RxAdapter<ContributorStats, ItemContributorBinding> rxAdapter = new RxAdapter<>(R.layout.item_contributor, filteredContributors);
            rxAdapter.asObservable()
                    .subscribe(viewHolder -> {
                        ItemContributorBinding viewDataBinding = viewHolder.getViewDataBinding();
                        ContributorStats contributorStats1 = viewHolder.getItem();
                        viewDataBinding.textViewContributor.setText(contributorStats1.getAuthor()
                                .getLogin());
                        viewDataBinding.textViewTotal.setText(String.valueOf(contributorStats1.getTotal()));
                    });
            mBinding.recyclerViewContributors.setAdapter(rxAdapter);
        } else {
            mProgressContributorsBinding.textViewError.setText(R.string.no_contributors);
            mProgressContributorsBinding.textViewError.setVisibility(View.VISIBLE);
        }
        mProgressContributorsBinding.progressBar.setVisibility(View.GONE);
    }
}
