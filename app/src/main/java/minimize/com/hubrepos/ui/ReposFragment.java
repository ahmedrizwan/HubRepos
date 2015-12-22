package minimize.com.hubrepos.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimize.android.rxrecycleradapter.RxAdapter;

import org.parceler.Parcels;

import java.util.Collections;

import minimize.com.hubrepos.BR;
import minimize.com.hubrepos.HubReposApp;
import minimize.com.hubrepos.R;
import minimize.com.hubrepos.databinding.FragmentReposBinding;
import minimize.com.hubrepos.databinding.IncludeProgressBinding;
import minimize.com.hubrepos.databinding.ItemRepoBinding;
import minimize.com.hubrepos.realm.Item;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class ReposFragment extends BaseFragment {

    FragmentReposBinding mBinding;
    IncludeProgressBinding mProgressBinding;
    boolean isTwoPane;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_repos, container, false);
        mProgressBinding = DataBindingUtil.bind(mBinding.getRoot()
                .findViewById(R.id.progressLayout));
        mBinding.recyclerViewRepos.setLayoutManager(new LinearLayoutManager(getActivity()));
        isTwoPane = ((ContainerActivity) getActivity()).isTwoPane();
        inject(this);

        //get language
        String language = getArguments().getString(getString(R.string.language));
        RxAdapter<Item, ItemRepoBinding> repoBindingRxAdapter = new RxAdapter<>(R.layout.item_repo, Collections.emptyList());
        mBinding.recyclerViewRepos.setAdapter(repoBindingRxAdapter);
        mBinding.textViewLanguage.setText(language);
        mGithubService.getRepositories("language:" + language)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repo -> {
                    repoBindingRxAdapter.asObservable()
                            .subscribe(simpleViewItem -> {
                                ItemRepoBinding viewDataBinding = simpleViewItem.getViewDataBinding();
                                Item item = simpleViewItem.getItem();
                                int adapterPosition = simpleViewItem.getAdapterPosition();
                                viewDataBinding.linearLayoutRepo.setBackgroundColor(adapterPosition % 2 == 0 ? ContextCompat.getColor(getActivity(), R.color.colorGray) : ContextCompat.getColor(getActivity(), R.color.colorDarkGray));
                                viewDataBinding.setVariable(BR.item, item);
                                viewDataBinding.executePendingBindings();
                                viewDataBinding.getRoot()
                                        .setOnClickListener(v -> {
                                            onRepoClick(item);
                                        });
                            });
                    repoBindingRxAdapter.updateDataSet(repo.getItems());
                    mProgressBinding.progressBar.setVisibility(View.GONE);
                }, throwable -> {
                    mProgressBinding.textViewError.setText(throwable.getMessage());
                });
        return mBinding.getRoot();
    }

    private void onRepoClick(final Item item) {
        //launch detail fragment
        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("item", Parcels.wrap(item));
        detailsFragment.setArguments(bundle);
        if (isTwoPane) {
            //show as dialog
            detailsFragment.show(getActivity().getSupportFragmentManager(), "Details");
        } else {
            HubReposApp.launchFragmentWithSharedElements(false, this, detailsFragment, R.id.container, null);
        }
    }

}
