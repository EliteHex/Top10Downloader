package com.timbuchalka.top10downloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Shadow on 11/18/2016.
 */

public class FeedAdapter<T extends  FeedEntry> extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<T> applications;

    public FeedAdapter(Context context, int resource, List<T> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
        //super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null)
        {
            Log.d(TAG, "getView: called with NULL convertView");
            convertView = layoutInflater.inflate(layoutResource,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            Log.d(TAG, "getView: provided a convertView");
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        TextView tvArtist = (TextView)convertView.findViewById(R.id.tvArtist);
        TextView tvSummary = (TextView)convertView.findViewById(R.id.tvSummary);*/

        T currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }


    private class ViewHolder
    {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v)
        {
            this.tvName = (TextView)v.findViewById(R.id.tvName);
            this.tvArtist = (TextView)v.findViewById(R.id.tvArtist);
            this.tvSummary = (TextView)v.findViewById(R.id.tvSummary);
        }
    }
}
