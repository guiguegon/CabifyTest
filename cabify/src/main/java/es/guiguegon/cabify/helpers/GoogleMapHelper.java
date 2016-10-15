package es.guiguegon.cabify.helpers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import es.guiguegon.cabify.R;
import es.guiguegon.cabify.models.Marker;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Created by guiguegon on 23/10/2015.
 */
public class GoogleMapHelper implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener,
        ClusterManager.OnClusterItemClickListener<Marker> {

    public final static int NORMAL_ZOOM = 14;

    private SupportMapFragment supportMapFragment;
    private FragmentActivity activity;

    private GoogleMap mMap;
    private Polyline polyline;
    private ClusterManager<Marker> mClusterManager;

    // listeners
    private GoogleMap.OnMapLongClickListener onMapLongClickListener;
    private GoogleMap.OnMapClickListener onMapClickListener;
    private GoogleMap.OnMapLoadedCallback onMapLoadedListener;
    private ClusterManager.OnClusterItemClickListener<Marker> onClusterItemClickListener;

    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Marker> fixedMarkers = new ArrayList<>();

    @Inject
    public GoogleMapHelper() {
    }

    public void onStart(FragmentActivity activity) {
        this.activity = activity;
    }

    public void onStop() {
        activity = null;
        onMapClickListener = null;
        onMapLoadedListener = null;
        onMapClickListener = null;
        onClusterItemClickListener = null;
    }

    public void setOnMapLoadedListener(GoogleMap.OnMapLoadedCallback onMapLoadedListener) {
        this.onMapLoadedListener = onMapLoadedListener;
    }

    public void setOnMapLongClickListener(GoogleMap.OnMapLongClickListener onMapLongClickListener) {
        this.onMapLongClickListener = onMapLongClickListener;
    }

    public void setOnMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {
        this.onMapClickListener = onMapClickListener;
    }

    public void setOnClusterItemClickListener(
            ClusterManager.OnClusterItemClickListener<Marker> onClusterItemClickListener) {
        this.onClusterItemClickListener = onClusterItemClickListener;
    }

    private void initMapFragment() {
        supportMapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentByTag(SupportMapFragment.class.getSimpleName());
        if (supportMapFragment == null) {
            supportMapFragment = new SupportMapFragment();
        }
    }

    public void loadMap(int mapResId) {
        initMapFragment();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(mapResId, supportMapFragment, SupportMapFragment.class.getSimpleName())
                .commit();
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
    }

    @Override
    public void onMapLoaded() {
        if (onMapLoadedListener != null) {
            onMapLoadedListener.onMapLoaded();
        }
    }

    private void setupMap() {
        try {
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMapLoadedCallback(this);
            mMap.setOnMapLongClickListener(this);
            mMap.setOnMapClickListener(this);
            setupClusterManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClusterManager() {
        mClusterManager = new ClusterManager<>(activity, mMap);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setRenderer(
                new DefaultClusterRenderer<Marker>(activity, mMap, mClusterManager) {
                    @Override
                    protected boolean shouldRenderAsCluster(Cluster cluster) {
                        return false;
                    }
                });
    }

    private void refreshMap() {
        mClusterManager.clearItems();
        mClusterManager.cluster();
        mClusterManager.addItems(markers);
        mClusterManager.addItems(fixedMarkers);
        mClusterManager.cluster();
    }

    private void addPlace(Marker Marker) {
        markers.add(Marker);
    }

    private void addPlace(ArrayList<Marker> markers) {
        this.markers.addAll(markers);
    }

    public void clearMarkers() {
        markers.clear();
    }

    public void paintMarker(Marker marker) {
        addPlace(marker);
        refreshMap();
        animateCamera(marker.getPosition());
    }

    public void fixMarker(Marker marker) {
        fixedMarkers.add(marker);
        refreshMap();
        if (fixedMarkers.size() > 1) {
            paintPolyline();
        }
    }

    private void paintPolyline() {
        PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
        for (Marker marker : fixedMarkers) {
            polylineOptions.add(marker.getPosition());
            latLngBoundsBuilder.include(marker.getPosition());
        }
        animateCamera(latLngBoundsBuilder.build());
        polyline = mMap.addPolyline(polylineOptions);
    }

    public void removeFixedMarkers() {
        fixedMarkers.clear();
        if (polyline != null) {
            polyline.remove();
        }
        refreshMap();
    }

    public void paintMarker(ArrayList<Marker> markers) {
        addPlace(markers);
        refreshMap();
        animateCamera(generateLatLngBoundsFromMarkers());
    }

    private LatLngBounds generateLatLngBoundsFromMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker Marker : markers) {
            builder.include(Marker.getPosition());
        }
        return builder.build();
    }

    public void moveCamera(LatLng latLng) {
        moveCamera(latLng, NORMAL_ZOOM);
    }

    public void moveCamera(LatLng latLng, int zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void moveCamera(LatLngBounds latLngBounds) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,
                (int) activity.getResources().getDimension(R.dimen.map_padding)));
    }

    public void animateCamera(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        animateCamera(latLng, NORMAL_ZOOM);
    }

    public void animateCamera(LatLng latLng) {
        animateCamera(latLng, NORMAL_ZOOM);
    }

    public void animateCamera(LatLng latLng, int zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public void animateCamera(LatLngBounds latLngBounds) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,
                (int) activity.getResources().getDimension(R.dimen.map_padding)));
    }

    // My position
    public void initMyPosition() {
        checkPermission();
    }

    private void checkPermission() {
        try {
            PermissionsManager.requestMultiplePermissions((ViewGroup) supportMapFragment.getView(),
                    this::setMyPositionEnabled, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
        } catch (Exception e) {
            //empty
        }
    }

    private void setMyPositionEnabled() {
        if (ActivityCompat.checkSelfPermission(supportMapFragment.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(supportMapFragment.getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    // Click listeners
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (onMapLongClickListener != null) {
            onMapLongClickListener.onMapLongClick(latLng);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (onMapClickListener != null) {
            onMapClickListener.onMapClick(latLng);
        }
    }

    @Override
    public boolean onClusterItemClick(Marker Marker) {
        if (onClusterItemClickListener != null) {
            onClusterItemClickListener.onClusterItemClick(Marker);
        }
        return false;
    }
}

