package es.guiguegon.cabify.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.guiguegon.cabify.R;
import es.guiguegon.cabify.adapters.EstimateAdapter;
import es.guiguegon.cabify.models.Estimate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guille on 12/10/2016.
 */

public class EstimatesDialogFragment extends BottomSheetDialogFragment {

    private static final String ARGUMENT_ESTIMATES = "argument_estimates";
    @BindView(R.id.estimates_recycler_view)
    RecyclerView estimatesRecyclerView;

    EstimateAdapter estimateAdapter;
    Unbinder unbinder;
    private List<Estimate> estimateList;

    public static EstimatesDialogFragment newInstance(ArrayList<Estimate> estimateList) {
        EstimatesDialogFragment fragment = new EstimatesDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(EstimatesDialogFragment.ARGUMENT_ESTIMATES, estimateList);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            this.estimateList = getArguments().getParcelableArrayList(ARGUMENT_ESTIMATES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_estimates, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        estimateAdapter = new EstimateAdapter();
        estimatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        estimatesRecyclerView.setAdapter(estimateAdapter);
        estimateAdapter.addEstimatees(estimateList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
