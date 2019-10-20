//package com.example.qurrah.Fragments;
//
//import android.content.Context;
//import android.net.Uri;
//import com.google.android.gms.location.LocationServices;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.qurrah.R;
//public class MapFragment extends Fragment {
//
//    private View view;
//
//    public  GoogleMap map;
//    private Location location;
//    private LatLng mAddress1;
//    public static boolean inMap=false;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    //  /* TODO Auto-generated method stub*/
//        view= inflater.inflate(R.layout.contact_map, container, false);
//        setMapView();
//        return view;
//    }
//
//    private void setMapView(){
//        CurrentLocation mCurrentLoc=new CurrentLocation(getActivity());
//        location = CurrentLocation.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if (location != null) {
//            mCurrentLoc.onLocationChanged(location);
//        } else {
//            mCurrentLoc.onLocationChanged(location);
//        }
//
//        //class="com.google.android.gms.maps.SupportMapFragment"
//
//        map = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.mapfragment)).getMap();
//
//
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(mAddress1).zoom(8).bearing(0) // Sets the orientation of the camera to east
//                .tilt(30)    // Sets the tilt of the camera to 30 degrees
//                .build();    // Creates a CameraPosition from the builder
//        map.setBuildingsEnabled(true);
//        map.setMyLocationEnabled(true);
//        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//
//    }
//}