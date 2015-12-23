package minimize.com.hubrepos.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.minimize.android.rxrecycleradapter.RxAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
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
import minimize.com.hubrepos.models.ContributorStats;
import minimize.com.hubrepos.models.Issue;
import minimize.com.hubrepos.models.Item;
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

    private Item mItem;
    private List<Issue> mIssues;
    private List<ContributorStats> mContributors;

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
        } else {
            //enable back
            ActionBar supportActionBar = ((ContainerActivity) getActivity()).getSupportActionBar();
            if (supportActionBar != null)
                supportActionBar
                        .setDisplayHomeAsUpEnabled(true);
        }

        //set layout managers for the recyclerViews
        mBinding.recyclerViewContributors.setLayoutManager(new minimize.com.hubrepos.ui.LinearLayoutManager(getActivity()));
        mBinding.recyclerViewIssues.setLayoutManager(new minimize.com.hubrepos.ui.LinearLayoutManager(getActivity()));

        //restore state
        if (savedInstanceState != null) {
            //get item, issue and contributors
            mItem = Parcels.unwrap(savedInstanceState.getParcelable("item"));
            fillInTextViews(mItem.getOwner()
                    .getLogin(), mItem.getName());
            ArrayList<Parcelable> issues = savedInstanceState.getParcelableArrayList("issues");
            mIssues = new ArrayList<>();
            if (issues != null) {
                for (Parcelable issue : issues) {
                    mIssues.add(Parcels.unwrap(issue));
                }
            }
            ArrayList<Parcelable> contributors = savedInstanceState.getParcelableArrayList("contributors");
            mContributors = new ArrayList<>();
            if (contributors != null) {
                for (Parcelable contributor : contributors) {
                    mContributors.add(Parcels.unwrap(contributor));
                }
            }

            if(mContributors.size()>0){
                setupContributorsAdapter();
            } else{
                noContributors();
            }
            mProgressContributorsBinding.progressBar.setVisibility(View.GONE);

            if(mIssues.size()>0){
                setupIssuesAdapter();
            } else {
                noIssues();
            }

            mProgressIssuesBinding.progressBar.setVisibility(View.GONE);

        } else {
            Parcelable itemParcelable = getArguments().getParcelable(getString(R.string.item));
            AppComponent appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(getActivity().getApplication()))
                    .build();
            appComponent.inject(this);

            mItem = Parcels.unwrap(itemParcelable);
            if (mItem != null) {
                String owner = mItem.getOwner()
                        .getLogin();
                String repoName = mItem.getName();
                fillInTextViews(owner, repoName);

                //get issues from the api
                mGithubService.getContributorsStats(owner, repoName)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::fillInListWithContributors, throwable -> {
                            //show error message
                            mProgressContributorsBinding.progressBar.setVisibility(View.GONE);
                            mProgressContributorsBinding.textViewError.setText(R.string.error_contributors);
                            mProgressContributorsBinding.textViewError.setVisibility(View.VISIBLE);
                        });

                mGithubService.getIssues(owner, repoName)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::fillInListWithIssues, throwable -> {
                            //show error message
                            mProgressIssuesBinding.progressBar.setVisibility(View.GONE);
                            mProgressIssuesBinding.textViewError.setText(R.string.error_issues);
                            mProgressIssuesBinding.textViewError.setVisibility(View.VISIBLE);
                        });

            }
        }
        return mBinding.getRoot();
    }

    private void fillInTextViews(final String owner, final String repoName) {
        mBinding.textViewName.setText(repoName);
        mBinding.textViewDescription.setText(mItem.getDescription());
        mBinding.textViewStarGazzers.setText(String.valueOf(mItem.getStargazersCount()));
        mBinding.textViewOwner.setText(owner);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mItem != null)
            outState.putParcelable("item", Parcels.wrap(mItem));
        if (mIssues != null) {
            ArrayList<Parcelable> issues = new ArrayList<>();
            for (Issue issue : mIssues) {
                issues.add(Parcels.wrap(issue));
            }
            outState.putParcelableArrayList("issues", issues);
        }
        if(mContributors!=null){
            ArrayList<Parcelable> contributors = new ArrayList<>();
            for (ContributorStats contributorStat : mContributors) {
                contributors.add(Parcels.wrap(contributorStat));
            }
            outState.putParcelableArrayList("contributors", contributors);
        }
    }

    private void fillInListWithIssues(final List<Issue> issueEvents) {
        if (issueEvents.size() > 0) {
            mIssues = Observable.from(issueEvents)
                    .take(3)
                    .toList()
                    .toBlocking()
                    .first();

            setupIssuesAdapter();
        } else {
            noIssues();
        }
        mProgressIssuesBinding.progressBar.setVisibility(View.GONE);
    }

    private void noIssues() {
        mProgressIssuesBinding.textViewError.setText(R.string.no_issues);
        mProgressIssuesBinding.textViewError.setVisibility(View.VISIBLE);
    }

    private void setupIssuesAdapter() {
        RxAdapter<Issue, ItemIssueBinding> rxAdapter = new RxAdapter<>(R.layout.item_issue, mIssues);
        rxAdapter.asObservable()
                .subscribe(viewHolder -> {
                    ItemIssueBinding viewDataBinding = viewHolder.getViewDataBinding();
                    Issue issue = viewHolder.getItem();
                    viewDataBinding.textViewIssue.setText(issue.getTitle());
                    viewDataBinding.getRoot().setOnClickListener(v -> {
                        //link for the issue details
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(issue.getHtmlUrl()));
                        startActivity(i);
                    });
                });
        mBinding.recyclerViewIssues.setAdapter(rxAdapter);
    }

    private void fillInListWithContributors(final List<ContributorStats> contributorStats) {
        if (contributorStats.size() > 0) {
            mContributors = Observable.from(contributorStats)
                    .takeLast(3)
                    .toList()
                    .toBlocking()
                    .first();
            Collections.reverse(mContributors);
            setupContributorsAdapter();
        } else {
            noContributors();
        }
        mProgressContributorsBinding.progressBar.setVisibility(View.GONE);
    }

    private void noContributors() {
        mProgressContributorsBinding.textViewError.setText(R.string.no_contributors);
        mProgressContributorsBinding.textViewError.setVisibility(View.VISIBLE);
    }

    private void setupContributorsAdapter() {
        RxAdapter<ContributorStats, ItemContributorBinding> rxAdapter = new RxAdapter<>(R.layout.item_contributor, mContributors);
        rxAdapter.asObservable()
                .subscribe(viewHolder -> {
                    ItemContributorBinding viewDataBinding = viewHolder.getViewDataBinding();
                    ContributorStats contributorStats1 = viewHolder.getItem();
                    viewDataBinding.textViewContributor.setText(contributorStats1.getAuthor()
                            .getLogin());
                    viewDataBinding.textViewTotal.setText(String.valueOf(contributorStats1.getTotal()));
                    viewDataBinding.getRoot().setOnClickListener(v -> {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(contributorStats1.getAuthor().getHtmlUrl()));
                        startActivity(i);
                    });
                });
        mBinding.recyclerViewContributors.setAdapter(rxAdapter);
    }
}
