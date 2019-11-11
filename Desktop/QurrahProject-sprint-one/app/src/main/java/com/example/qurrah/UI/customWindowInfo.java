package com.example.qurrah.UI;
import android.app.Activity;
import android.view.View;
import android.content.Context;
import android.*;
import com.example.qurrah.R;
import com.example.qurrah.Model.InfoWindowData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import android.widget.TextView;
import android.widget.ImageView;
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
        ImageView img = view.findViewById(R.id.info_window_image);

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();

        if (infoWindowData.getImg().equals("https://i-love-png.com/images/no-image_7299.png")){
            if (infoWindowData.getCatogery().equals( "حيوان")){
                img.setImageResource(R.drawable.pets);}
            else if (infoWindowData.getCatogery().equals( "انسان")){
                img.setImageResource(R.drawable.people);}
            else if (infoWindowData.getCatogery().equals( "اجهزة")){
                img.setImageResource(R.drawable.devices);}
            else if (infoWindowData.getCatogery().equals( "اخرى")){
                img.setImageResource(R.drawable.other);}}
        else{
          //  img.setImageResource(R.drawable.logo);
//            int imageId = context.getResources().getIdentifier(infoWindowData.getImg(),
//           "drawable", context.getPackageName());
//        img.setImageResource(imageId);
            Picasso.get().load(infoWindowData.getImg()).into(img);
        }
        title.setText(infoWindowData.getTitle());
        catogery.setText(infoWindowData.getCatogery());
        type.setText(infoWindowData.getType());

    return view;
    }
}
