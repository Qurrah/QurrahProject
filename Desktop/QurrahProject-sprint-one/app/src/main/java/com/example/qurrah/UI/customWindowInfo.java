package com.example.qurrah.UI;
import android.app.Activity;
import android.view.View;
import android.content.Context;
import com.example.qurrah.R;
import com.example.qurrah.Model.InfoWindowData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import android.widget.TextView;

public class customWindowInfo implements GoogleMap.InfoWindowAdapter {


    //private final View myContentsView;
    private Context context;
    customWindowInfo(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.infowindow_layout, null);

        TextView title = view.findViewById(R.id.titleInfoWind);
        TextView catogery = view.findViewById(R.id.categoryInfoWind);
        TextView type = view.findViewById(R.id.typeInfoWindo);


        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();



        title.setText(infoWindowData.getTitle());
        catogery.setText(infoWindowData.getCatogery());
        type.setText(infoWindowData.getType());

    return view;
    }
}
