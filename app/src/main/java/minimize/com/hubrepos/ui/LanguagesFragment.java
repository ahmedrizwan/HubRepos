package minimize.com.hubrepos.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.minimize.android.rxrecycleradapter.RxAdapter;

import java.util.Arrays;
import java.util.List;

import minimize.com.hubrepos.BR;
import minimize.com.hubrepos.HubReposApp;
import minimize.com.hubrepos.R;
import minimize.com.hubrepos.databinding.FragmentLanguagesBinding;
import minimize.com.hubrepos.databinding.ItemLanguageBinding;
import rx.Observable;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class LanguagesFragment extends BaseFragment {
    FragmentLanguagesBinding mBinding;
    ItemLanguageBinding mItemNotFoundBinding;
    boolean isTwoPane;
    private String[] mLanguages;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_languages, container, false);
        mItemNotFoundBinding = DataBindingUtil.bind(mBinding.getRoot()
                .findViewById(R.id.languageNotFound));

        //disable up
        ActionBar supportActionBar = ((ContainerActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar
                    .setDisplayHomeAsUpEnabled(false);

        mItemNotFoundBinding.getRoot()
                .setVisibility(View.GONE);

        isTwoPane = ((ContainerActivity) getActivity()).isTwoPane();
        mLanguages = getResources().getStringArray(R.array.languages);

        RxAdapter<String, ItemLanguageBinding> rxAdapter = new RxAdapter<>(R.layout.item_language, Arrays.asList(mLanguages));

        rxAdapter.asObservable()
                .subscribe(simpleViewItem -> {
                    final ItemLanguageBinding viewDataBinding = simpleViewItem.getViewDataBinding();
                    final String item = simpleViewItem.getItem();
                    viewDataBinding.setVariable(BR.language, item);
                    View root = viewDataBinding.getRoot();
                    int adapterPosition = simpleViewItem.getAdapterPosition();
                    viewDataBinding.cardViewLanguage.setBackgroundColor(adapterPosition % 2 == 0 ? ContextCompat.getColor(getActivity(), R.color.colorGray) : ContextCompat.getColor(getActivity(), android.R.color.white));
                    root.setOnClickListener(v -> {
                        languageClick(item);
                    });
                });

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        mBinding.recyclerViewLanguages.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerViewLanguages.setAdapter(rxAdapter);

        // Add search
        RxTextView.afterTextChangeEvents(mBinding.editTextSearch)
                .subscribe(textViewAfterTextChangeEvent -> {
                    String searchText = textViewAfterTextChangeEvent.view()
                            .getText()
                            .toString();
                    final List<String> filteredList = Observable.from(mLanguages)
                            .filter(s -> s.toLowerCase()
                                    .contains(searchText.toLowerCase()))
                            .toList()
                            .toBlocking()
                            .first();
                    rxAdapter.updateDataSet(filteredList);
                    if (filteredList.size() == 0) {
                        mItemNotFoundBinding.getRoot()
                                .setVisibility(View.VISIBLE);
                        mItemNotFoundBinding.textViewLanguage.setText("Not found! Tap here to Search for \"" + searchText + "\"");

                        mItemNotFoundBinding.getRoot()
                                .setOnClickListener(v -> languageClick(searchText));

                        mItemNotFoundBinding.getRoot()
                                .setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                    } else if (searchText.length() > 0) {
                        mItemNotFoundBinding.getRoot()
                                .setVisibility(View.VISIBLE);
                        mItemNotFoundBinding.textViewLanguage.setText("Get Repos for \"" + searchText + "\"");

                        mItemNotFoundBinding.getRoot()
                                .setOnClickListener(v -> languageClick(searchText));

                        mItemNotFoundBinding.getRoot()
                                .setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.white));
                    } else {
                        mItemNotFoundBinding.getRoot()
                                .setVisibility(View.GONE);
                    }
                });

        return mBinding.getRoot();
    }

    private void languageClick(final String item) {
        //launch repos fragment passing in the language
        ReposFragment reposFragment = new ReposFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.language), item);
        reposFragment.setArguments(bundle);
        if (isTwoPane) {
            ((ContainerActivity) getActivity()).hideSelectMessage();
        }
        HubReposApp.launchFragmentWithSharedElements                                 (((ContainerActivity) getActivity()).isTwoPane(), LanguagesFragment.this,
                reposFragment, isTwoPane ? R.id.containerRepos : R.id.container, null);
    }
}
