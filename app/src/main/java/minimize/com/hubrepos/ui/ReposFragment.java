package minimize.com.hubrepos.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimize.android.rxrecycleradapter.RxAdapter;

import java.util.Collections;

import minimize.com.hubrepos.BR;
import minimize.com.hubrepos.R;
import minimize.com.hubrepos.databinding.FragmentReposBinding;
import minimize.com.hubrepos.databinding.IncludeProgressBinding;
import minimize.com.hubrepos.databinding.ItemRepoBinding;
import minimize.com.hubrepos.realm.Item;
import minimize.com.hubrepos.retrofit.GithubService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class ReposFragment extends BaseFragment {

    FragmentReposBinding mBinding;
    IncludeProgressBinding mProgressBinding;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_repos, container, false);
        mProgressBinding = DataBindingUtil.bind(mBinding.getRoot()
                .findViewById(R.id.progressLayout));
        mBinding.recyclerViewRepos.setLayoutManager(new LinearLayoutManager(getActivity()));
        inject(this);

        //get language
        String language = getArguments().getString(getString(R.string.language));
        Timber.e("onCreateView : " + language);
        RxAdapter<Item, ItemRepoBinding> repoBindingRxAdapter = new RxAdapter<>(R.layout.item_repo, Collections.emptyList());
        mBinding.recyclerViewRepos.setAdapter(repoBindingRxAdapter);
        mBinding.textViewLanguage.setText(language);
        GithubService githubService = mRetrofit.create(GithubService.class);
        githubService.getRepositories()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repo -> {

                    repoBindingRxAdapter.asObservable()
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(simpleViewItem -> {
                                ItemRepoBinding viewDataBinding = simpleViewItem.getViewDataBinding();
                                Item item = simpleViewItem.getItem();
                                viewDataBinding.setVariable(BR.item, item);
                                viewDataBinding.executePendingBindings();
                                Timber.e("Item : "+item.getName());
                            });
                    repoBindingRxAdapter.updateDataSet(repo.getItems());
                    mProgressBinding.progressBar.setVisibility(View.GONE);
                }, throwable -> {
                    mProgressBinding.textViewError.setText(throwable.getMessage());
                });
        return mBinding.getRoot();
    }

}
