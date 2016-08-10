package alugueis.alugueis;

import alugueis.alugueis.abstractiontools.StandardFragment;
import alugueis.alugueis.dialogs.PermissionsDialog;
import alugueis.alugueis.location.LocationChangeListener;
import alugueis.alugueis.location.LocationSimpleListener;

import alugueis.alugueis.util.MapsUtil;
import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.Unbinder;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;

public class MapFragmentView extends StandardFragment implements OnMapReadyCallback {
    private static String TAG = "MapFragmentView";
    private Unbinder unbinder;
    private Marker currentLocation;
    private Marker myMarker;
    private GoogleMap googleMap;
    private LocationChangeListener locationChangeListener;

    @BindView(R.id.location_button)
    FloatingActionButton locationButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_activity, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MapFragment mapFragment = MapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        startLocationSettings();
        initFields();
    }

    private void initFields() {
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveCamera();
            }
        });
    }

    private void startLocationSettings() {
        if (ActivityCompat.checkSelfPermission(getAppCompatActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getAppCompatActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }

        locationChangeListener = new LocationChangeListener(getAppCompatActivity(), new LocationSimpleListener() {
            @Override
            public void onLocationChange(Location location) {
                setMapsMarkers(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });

        locationChangeListener.startLocationListener();
    }

    private void setMapsMarkers(LatLng latLng) {

        if (currentLocation == null) {
            currentLocation = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(getIcon(R.drawable.ic_current_location_circle_blue)));

            myMarker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(getIcon(R.drawable.ic_my_pin_location)));
            moveCamera();
        }
        currentLocation.setPosition(latLng);
    }

    private BitmapDescriptor getIcon(int resource) {
        return BitmapDescriptorFactory.fromResource(resource);
    }

    private void moveCamera() {
        myMarker.setPosition(currentLocation.getPosition());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation.getPosition()));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19f));
    }

    @Override
    public Toolbar getToolBar() {
        return null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            PermissionsDialog permissionsDialog = new PermissionsDialog();
            permissionsDialog.setCancelable(false);
            permissionsDialog.setTargetFragment(this, 0);
            permissionsDialog.show(getFragmentManager(), "PermissionsFragment");
            return;
        }
        startLocationSettings();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!MapsUtil.souldShowRequest(getActivity())) {
            MapsUtil.callApplicationPermissionsSettings(getActivity());
        } else {
            MapsUtil.requestLocationPermition(getActivity());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}


