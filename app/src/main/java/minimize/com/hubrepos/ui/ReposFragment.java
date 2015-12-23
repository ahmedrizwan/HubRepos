package minimize.com.hubrepos.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimize.android.rxrecycleradapter.RxAdapter;
import com.minimize.android.rxrecycleradapter.SimpleViewHolder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import minimize.com.hubrepos.BR;
import minimize.com.hubrepos.HubReposApp;
import minimize.com.hubrepos.R;
import minimize.com.hubrepos.databinding.FragmentReposBinding;
import minimize.com.hubrepos.databinding.IncludeProgressBinding;
import minimize.com.hubrepos.databinding.ItemRepoBinding;
import minimize.com.hubrepos.models.Item;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class ReposFragment extends BaseFragment {

    FragmentReposBinding mBinding;
    IncludeProgressBinding mProgressBinding;
    boolean isTwoPane;
    List<Item> mItems;
    private String mLanguage;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_repos, container, false);
        mProgressBinding = DataBindingUtil.bind(mBinding.getRoot()
                .findViewById(R.id.progressLayout));
        mBinding.recyclerViewRepos.setLayoutManager(new LinearLayoutManager(getActivity()));
        isTwoPane = ((ContainerActivity) getActivity()).isTwoPane();
        inject(this);
        RxAdapter<Item, ItemRepoBinding> repoBindingRxAdapter = new RxAdapter<>(R.layout.item_repo, Collections.emptyList());
        mBinding.recyclerViewRepos.setAdapter(repoBindingRxAdapter);

        //restore state
        if (savedInstanceState != null) {
            ArrayList<Parcelable> items = savedInstanceState.getParcelableArrayList("items");
            mLanguage = savedInstanceState.getString("language");
            mBinding.textViewLanguage.setText(mLanguage);
            if (items != null) {
                mItems = new ArrayList<>();
                for (Parcelable item : items) {
                    mItems.add(Parcels.unwrap(item));
                }
                repoBindingRxAdapter.asObservable()
                        .subscribe(this::bind);

                repoBindingRxAdapter.updateDataSet(mItems);
                mProgressBinding.progressBar.setVisibility(View.GONE);
            }
        } else {
            //get mLan
            mLanguage = getArguments().getString(getString(R.string.language));
            mBinding.textViewLanguage.setText(mLanguage);
            //get details from api
            mGithubService.getRepositories("language:" + mLanguage)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(repo -> {
                        repoBindingRxAdapter.asObservable()
                                .subscribe(this::bind);
                        //update and hide progressBar
                        mItems = repo.getItems();
                        repoBindingRxAdapter.updateDataSet(mItems);
                        mProgressBinding.progressBar.setVisibility(View.GONE);
                    }, throwable -> {
                        mProgressBinding.progressBar.setVisibility(View.GONE);
                        mProgressBinding.textViewError.setVisibility(View.VISIBLE);
                        mProgressBinding.textViewError.setText(R.string.no_repos);
                    });
        }
        return mBinding.getRoot();
    }

    private void bind(final SimpleViewHolder<Item, ItemRepoBinding> simpleViewItem) {
        ItemRepoBinding viewDataBinding = simpleViewItem.getViewDataBinding();
        Item item = simpleViewItem.getItem();
        //bind
        int adapterPosition = simpleViewItem.getAdapterPosition();
        viewDataBinding.linearLayoutRepo.setBackgroundColor(adapterPosition % 2 == 0 ? ContextCompat.getColor(getActivity(), R.color.colorGray) : ContextCompat.getColor(getActivity(), android.R.color.white));
        viewDataBinding.setVariable(BR.item, item);
        viewDataBinding.executePendingBindings();
        viewDataBinding.getRoot()
                .setOnClickListener(v -> {
                    onRepoClick(item);
                });
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Parcelable> items = new ArrayList<>();
        if (mItems != null) {
            for (Item item : mItems) {
                items.add(Parcels.wrap(item));
            }
        }
        outState.putParcelableArrayList("items", items);
        outState.putString("language", mLanguage);
    }

    private void onRepoClick(final Item item) {
        //launch detail fragment
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.item), Parcels.wrap(item));
        detailsFragment.setArguments(bundle);
        //launch dialogFragment if twoPane else simply fragment launch
        if (isTwoPane) {
            //show as dialog
            detailsFragment.show(getActivity().getSupportFragmentManager(), "Details");
        } else {
            HubReposApp.launchFragmentWithSharedElements(false, this, detailsFragment, R.id.container, null);
        }
    }

}
