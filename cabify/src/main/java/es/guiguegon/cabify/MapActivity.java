package es.guiguegon.cabify;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.karumi.dexter.Dexter;
import es.guiguegon.cabify.adapters.AddressAdapter;
import es.guiguegon.cabify.dialogs.EstimatesDialogFragment;
import es.guiguegon.cabify.helpers.GoogleMapHelper;
import es.guiguegon.cabify.helpers.LocationHelper;
import es.guiguegon.cabify.models.Estimate;
import es.guiguegon.cabify.models.Marker;
import es.guiguegon.cabify.models.Stop;
import es.guiguegon.cabify.models.StopBuilder;
import es.guiguegon.cabify.net.CabifyApi;
import es.guiguegon.cabify.net.requests.EstimateRequest;
import es.guiguegon.cabify.utils.LocationUtils;
import es.guiguegon.cabify.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by guiguegon on 09/09/16.
 */
public class MapActivity extends AppCompatActivity
        implements GoogleMap.OnMapLoadedCallback, LocationListener,
        LocationHelper.LocationHelperListener, AddressAdapter.AddressAdapterListener,
        GoogleMap.OnMapClickListener, ClusterManager.OnClusterItemClickListener<Marker> {

    // Dependencies
    LocationHelper locationHelper;
    GoogleMapHelper googleMapHelper;
    CabifyApi cabifyApi;

    // Views
    @BindView(R.id.my_location_btn)
    ImageView myLocationBtn;
    @BindView(R.id.toolbar_progress)
    ProgressBar loadingBar;
    @BindView(R.id.toolbar_search_submit)
    ImageView submitBtn;
    @BindView(R.id.toolbar_edittext)
    EditText submitEditText;
    @BindView(R.id.results_layout)
    CardView resultsLayout;
    @BindView(R.id.cabify_legend)
    CardView cabifyLegendLayout;
    @BindView(R.id.cabify_legend_text)
    TextView cabifyLegendText;
    @BindView(R.id.results_recycler)
    RecyclerView resultsRecycler;
    @BindView(R.id.location_selected_layout)
    CardView locationSelectedLayout;
    @BindView(R.id.item_address_raw_address)
    TextView locationSelectedRawAddress;
    @BindView(R.id.location_selected_fab)
    FloatingActionButton locationSelectedFAB;

    // Adapters
    AddressAdapter addressAdapter;

    // Vars
    Marker selectedMarker;
    Stop start;
    Stop stop;
    List<Estimate> estimates;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, MapActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dexter.initialize(this);
        googleMapHelper = GoogleMapHelper.getInstance();
        locationHelper = LocationHelper.getInstance();
        cabifyApi = CabifyApi.getInstance();
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadMap();
        setClickListeners();
        locationResultPanel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleMapHelper.onStart(this);
        googleMapHelper.setOnMapClickListener(this);
        googleMapHelper.setOnClusterItemClickListener(this);
        locationHelper.onStart(this);
        locationHelper.setLocationHelperListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleMapHelper.onStop();
        locationHelper.onStop();
    }

    private void locationResultPanel() {
        addressAdapter = new AddressAdapter();
        addressAdapter.setAddressAdapterListener(this);
        resultsRecycler.setAdapter(addressAdapter);
        resultsRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        resultsRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    public void fillSelectedResult(Address address) {
        fillSelectedResult(new Marker(address));
    }

    public void fillSelectedResult(Marker marker) {
        this.selectedMarker = marker;
        locationSelectedRawAddress.setText(LocationUtils.getFullAddress(marker));
    }

    private void setClickListeners() {
        myLocationBtn.setOnClickListener(this::onMyLocationBtn);
        submitBtn.setOnClickListener(this::onSubmitBtn);
        locationSelectedFAB.setOnClickListener(this::onLocationSelected);
    }

    private void loadMap() {
        googleMapHelper.loadMap(R.id.map_view);
        googleMapHelper.setOnMapLoadedListener(this);
    }

    // Panels
    private void queryResultsPanel(boolean queryResultsPanel) {
        //this.queryResultsPanel = queryResultsPanel;
        Animation animation;
        if (queryResultsPanel) {
            animation = AnimationUtils.loadAnimation(this, R.anim.ytranslate_from_100_to_0_in_400);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    resultsLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            resultsLayout.startAnimation(animation);
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.ytranslate_from_0_to_100_in_400);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    resultsLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            resultsLayout.startAnimation(animation);
        }
    }

    private void selectedLocationPanel(boolean selectedLocationPanel) {
        //this.selectedLocationPanel = selectedLocationPanel;
        Animation animation;
        if (selectedLocationPanel) {
            animation = AnimationUtils.loadAnimation(this, R.anim.ytranslate_from_100_to_0_in_400);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    locationSelectedLayout.setVisibility(View.VISIBLE);
                    locationSelectedFAB.animate().alpha(1f).setDuration(400).start();
                    locationSelectedFAB.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            locationSelectedLayout.startAnimation(animation);
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.ytranslate_from_0_to_100_in_400);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    locationSelectedFAB.animate().alpha(0f).setDuration(400).start();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    locationSelectedLayout.setVisibility(View.GONE);
                    locationSelectedFAB.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        locationSelectedLayout.startAnimation(animation);
    }

    // Loading
    private void setLoadingAddress() {
        submitBtn.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);
    }

    private void setLoadingComplete() {
        submitBtn.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.GONE);
    }

    // OnClickListeners
    private void onSubmitBtn(View view) {
        if (!TextUtils.isEmpty(submitEditText.getText())) {
            locationHelper.getLocationFromAddressAsync(submitEditText.getText().toString());
            setLoadingAddress();
            Utils.hideKeyboardFrom(this, getCurrentFocus());
        }
    }

    private void onMyLocationBtn(View view) {
        locationHelper.requestLocationUpdate((ViewGroup) view.getRootView(),
                LocationUtils.createRequestLocationUpdateOnce(), this);
    }

    private void onLocationSelected(View view) {
        if (selectedMarker != null && start == null) {
            googleMapHelper.fixMarker(selectedMarker);
            start = new StopBuilder().setMarker(selectedMarker).createStop();
            cleanFields();
            cabifyLegendText.setText(R.string.cabify_legend_end);
        } else if (selectedMarker != null && stop == null) {
            googleMapHelper.fixMarker(selectedMarker);
            stop = new StopBuilder().setMarker(selectedMarker).createStop();
            getEstimate();
        }
    }

    private void cleanFields() {
        selectedMarker = null;
        submitEditText.setText("");
        selectedLocationPanel(false);
    }

    private void getEstimate() {
        cleanFields();
        setLoadingAddress();
        Observable<List<Estimate>> estimatesObservable =
                cabifyApi.estimateTrip(EstimateRequest.createEstimateRequest(start, stop));
        estimatesObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Estimate>>() {
                    @Override
                    public void onCompleted() {
                        Log.i("[MapActivity]", "[onCompleted]");
                        setLoadingComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("[MapActivity]", "[onError]");
                        setLoadingComplete();
                        stop = null;
                        start = null;
                        new AlertDialog.Builder(MapActivity.this).setMessage(
                                "Something wrong happen, try again").create().show();
                    }

                    @Override
                    public void onNext(List<Estimate> estimates) {
                        Log.i("[MapActivity]", "[onNext]");
                        setEstimates(estimates);
                        setCabifyTextResults();
                        loadEstimateDialogFragment();
                    }
                });
    }

    private void setCabifyTextResults() {
        cabifyLegendText.setText(R.string.cabify_legend_see_results);
        cabifyLegendText.setOnClickListener(this::onCabifyLegend);
    }

    private void onCabifyLegend(View view) {
        loadEstimateDialogFragment();
    }

    private void setEstimates(List<Estimate> estimates) {
        this.estimates = estimates;
    }

    private void loadEstimateDialogFragment() {
        EstimatesDialogFragment.newInstance(new ArrayList<>(estimates))
                .show(getSupportFragmentManager(), EstimatesDialogFragment.class.getSimpleName());
    }

    // GoogleMap.OnMapLoadedCallback interface
    @Override
    public void onMapLoaded() {
        googleMapHelper.initMyPosition();
    }

    // GoogleMap.OnMapClickListener interface
    @Override
    public void onMapClick(LatLng latLng) {
        Log.i("[MapActivity]", "onMapClick " + latLng.toString());
        locationHelper.getLocationFromLatLngAsync(latLng);
        setLoadingAddress();
        Utils.hideKeyboardFrom(this, getCurrentFocus());
    }

    // ClusterManager.OnClusterItemClickListener<Marker> interface
    @Override
    public boolean onClusterItemClick(Marker marker) {
        Log.i("[MapActivity]", "onMapClick " + marker.getPosition().toString());
        selectedMarker = marker;
        fillSelectedResult(marker);
        selectedLocationPanel(true);
        return true;
    }

    // LocationListener interface
    @Override
    public void onLocationChanged(Location location) {
        googleMapHelper.initMyPosition();
        googleMapHelper.animateCamera(location);
    }

    // LocationHelper.LocationHelperListener interface
    @Override
    public void onGotAddress(List<Address> addresses) {
        if (start == null && stop == null) {
            googleMapHelper.removeFixedMarkers();
        }
        setLoadingComplete();
        addressAdapter.setAddresses(addresses);
        queryResultsPanel(true);
        selectedLocationPanel(false);
    }

    @Override
    public void onError() {
        setLoadingComplete();
    }

    // AddressAdapterListener interface
    @Override
    public void onAddressClick(Address address) {
        fillSelectedResult(address);
        googleMapHelper.clearMarkers();
        googleMapHelper.paintMarker(new Marker(address));
        queryResultsPanel(false);
        selectedLocationPanel(true);
    }
}
