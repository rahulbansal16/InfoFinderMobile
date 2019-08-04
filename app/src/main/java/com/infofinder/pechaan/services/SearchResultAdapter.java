package com.infofinder.pechaan.services;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.infofinder.pechaan.Constants;
import com.infofinder.pechaan.R;
import com.infofinder.pechaan.SearchActivity;
import com.infofinder.pechaan.models.ContactResultModel;
import com.infofinder.pechaan.models.EdgesModel;
import java.util.Collections;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    List<ContactResultModel> contactResultModels;
    Context context;
    private static String MARK = " ---> ";
    private SearchActivity activity;
    private ToastService toastService;

    private boolean showDialogForPermission(String permission, int code ){
        if (ContextCompat.checkSelfPermission(activity, permission)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        code);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        code);
            }
        } else {
            return true;
        }
        return false;
    }

    public SearchResultAdapter(List<ContactResultModel> contactResultModels, SearchActivity appCompatActivity)
    {
        this.contactResultModels = contactResultModels;
        this.activity = appCompatActivity;
        toastService = new ToastService(appCompatActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }


    private String createTextForEdges(List<EdgesModel> edgesModels){
        StringBuilder summary = new StringBuilder();
        summary.append(MARK);
        for( EdgesModel edgesModel :edgesModels ){
            summary.append(edgesModel.getContactName());
            summary.append(MARK);
        }
        return summary.toString();
    }

    private String getSummary(ContactResultModel contactResultModel, Button edge1, Button edge2){
        final List<EdgesModel> edgesModels = contactResultModel.getEdges();
        if ( edgesModels.size() == 0 ){
            edge1.setVisibility(View.INVISIBLE);
            edge2.setVisibility(View.INVISIBLE);
            return Constants.NO_COMMON_CONTACT;
        }
        if ( edgesModels.size() == 1){
            edge2.setVisibility(View.INVISIBLE);
            edge1.setVisibility(View.VISIBLE);
            edge1.setText("Call " + edgesModels.get(0).getContactName());
            edge1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCallIntent("+91"+edgesModels.get(0).getNodeNumber());
                }
            });
            return Constants.ONE_COMMON_CONTACT;
        }
        if (edgesModels.size() == 2){
            edge1.setVisibility(View.VISIBLE);
            edge1.setText("Call "+edgesModels.get(0).getContactName());
            edge1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCallIntent("+91"+edgesModels.get(0).getNodeNumber());
                }
            });

            edge2.setVisibility(View.VISIBLE);
            edge2.setText("Call "+edgesModels.get(1).getContactName());
//            edge2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    addCallIntent("+91"+edgesModels.get(1).getNodeNumber());
//                }
//            });
            return "You know " + edgesModels.get(0).getContactName() + ", who knows " + edgesModels.get(1).getContactName() ;
        }

        edge1.setVisibility(View.VISIBLE);
        edge1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCallIntent("+91"+edgesModels.get(0).getNodeNumber());
            }
        });
        edge1.setText("Call "+edgesModels.get(0).getContactName());
        edge2.setVisibility(View.VISIBLE);
        edge2.setText("Call "+edgesModels.get(1).getContactName());
        edge2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCallIntent("+91"+edgesModels.get(1).getNodeNumber());
            }
        });

        return Constants.MORE_THAN_TWO_COMMON_CONTACT + contactResultModel.getDestinationNumber();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ContactResultModel contactResultModel = contactResultModels.get(position);
        Collections.sort(contactResultModel.getEdges());

//        holder.source.setText(contactResultModel.getSourceNumber());
        holder.edges.setText(contactResultModel.getSourceNumber() + this.createTextForEdges(contactResultModel.getEdges()) + contactResultModel.getDestinationName());
//        holder.destination.setText(contactResultModel.getDestinationName());

//        holder.textTvShow.setText(tvShow.getTvshow());
        holder.summary.setText(getSummary(contactResultModel, holder.edge1, holder.edge2));
//        holder.imgTvShow.setImageResource(tvShow.getImgTvshow());
        holder.cv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Toast.makeText(context,"The position is: "+ position,Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addCallIntent(String number){
        if (showDialogForPermission(Manifest.permission.CALL_PHONE, 1) ) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + number));//change the number
            context.startActivity(callIntent);
        }
    }

    @Override
    public int getItemCount() {
        return contactResultModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
//        ImageView imgTvShow;
//        TextView source;
//        TextView destination;
        TextView edges;
        CardView cv;
        TextView summary;
        Button edge1,edge2;


        public ViewHolder(View itemView)
        {
            super(itemView);
//            imgTvShow = (ImageView)itemView.findViewById(R.id.imgTvshow);
//            source = itemView.findViewById(R.id.source);
            edges = itemView.findViewById(R.id.edges);
//            destination = itemView.findViewById(R.id.destination);
            summary = itemView.findViewById(R.id.summary);
            edge1 = itemView.findViewById(R.id.edge1);
            edge2 = itemView.findViewById(R.id.edge2);
            cv = itemView.findViewById(R.id.cv);
        }

    }
}