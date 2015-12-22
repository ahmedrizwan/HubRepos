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

import java.util.Arrays;

import minimize.com.hubrepos.BR;
import minimize.com.hubrepos.R;
import minimize.com.hubrepos.databinding.FragmentLanguagesBinding;
import minimize.com.hubrepos.databinding.ItemLanguageBinding;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 22/12/2015.
 */
public class LanguagesFragment extends BaseFragment {
    FragmentLanguagesBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_languages, container, false);
        String[] languages = getResources().getStringArray(R.array.languages);

        RxAdapter<String, ItemLanguageBinding> rxAdapter = new RxAdapter<>(R.layout.item_language, Arrays.asList(languages));

        rxAdapter.asObservable()
                .subscribe(simpleViewItem -> {
                    final ItemLanguageBinding viewDataBinding = simpleViewItem.getViewDataBinding();
                    final String item = simpleViewItem.getItem();
                    viewDataBinding.setVariable(BR.language, item);
                    View root = viewDataBinding.getRoot();
                    int adapterPosition = simpleViewItem.getAdapterPosition();
                    viewDataBinding.linearLayoutLanguage.setBackgroundColor(adapterPosition % 2 == 0 ? ContextCompat.getColor(getActivity(), R.color.colorDarkGray) : ContextCompat.getColor(getActivity(), R.color.colorGray));
                    root.setOnClickListener(v -> {
                        Timber.e("onCreateView : " + item);
                        //launch repos fragment passing in the language
                        ReposFragment reposFragment = new ReposFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(getString(R.string.language), item);
                        reposFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.container, reposFragment)
                                .commit();

                    });
                });
        mBinding.recyclerViewLanguages.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerViewLanguages.setAdapter(rxAdapter);
        return mBinding.getRoot();
    }


}
