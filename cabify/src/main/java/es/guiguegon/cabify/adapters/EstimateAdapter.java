package es.guiguegon.cabify.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import es.guiguegon.cabify.R;
import es.guiguegon.cabify.models.Estimate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by guiguegon on 16/12/15.
 */
public class EstimateAdapter extends RecyclerView.Adapter<EstimateAdapter.EstimateViewHolder> {

    List<Estimate> estimates;

    public EstimateAdapter() {
        estimates = new ArrayList<>();
    }

    public void setEstimatees(List<Estimate> estimates) {
        clear();
        addEstimatees(estimates);
    }

    public void addEstimatees(List<Estimate> estimates) {
        this.estimates.addAll(estimates);
        Collections.sort(this.estimates);
        notifyDataSetChanged();
    }

    public void clear() {
        estimates.clear();
        notifyDataSetChanged();
    }

    @Override
    public EstimateViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new EstimateViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_estimate, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(EstimateViewHolder viewHolder, int position) {
        try {
            fillEstimateViewHoler(viewHolder, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillEstimateViewHoler(EstimateViewHolder viewHolder, int position) {
        Estimate estimate = getEstimate(position);
        Glide.with(viewHolder.itemView.getContext())
                .load(estimate.getVehicleType().getIcons().getRegular())
                .into(viewHolder.estimateVehicleTypeImage);
        viewHolder.estimateVehicleTypeTitle.setText(estimate.getVehicleType().getName());
        viewHolder.estimateVehicleTypeDescription.setText(
                estimate.getVehicleType().getDescription());
        viewHolder.estimatePrice.setText(estimate.getPriceFormatted());
        viewHolder.estimateCurrency.setText(estimate.getCurrency());
    }

    private Estimate getEstimate(int position) {
        return estimates.get(position);
    }

    @Override
    public int getItemCount() {
        return estimates.size();
    }

    public class EstimateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.estimate_vehicle_type_image)
        ImageView estimateVehicleTypeImage;
        @BindView(R.id.estimate_vehicle_type_title)
        TextView estimateVehicleTypeTitle;
        @BindView(R.id.estimate_vehicle_type_description)
        TextView estimateVehicleTypeDescription;
        @BindView(R.id.estimate_price)
        TextView estimatePrice;
        @BindView(R.id.estimate_currency)
        TextView estimateCurrency;

        public EstimateViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
