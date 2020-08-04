package com.example.myapplication.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DriveActivity;
import com.example.myapplication.FallDetectActivity;
import com.example.myapplication.HeartActivity;
import com.example.myapplication.R;
import com.example.myapplication.ReminderActivity;
import com.example.myapplication.SleepActivity;
import com.example.myapplication.StepsActivity;

import java.util.ArrayList;

public class FeaturedAdapter extends RecyclerView.Adapter <FeaturedAdapter.FeaturedViewHolder>
{
    Context context;
    ArrayList<FeaturedHelperClass> featuredLocations;

    public FeaturedAdapter(ArrayList<FeaturedHelperClass> featuredLocations, Activity context)
    {
        this.featuredLocations = featuredLocations;
        this.context = context;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_card_design, parent, false);
        FeaturedViewHolder featuredViewHolder = new FeaturedViewHolder(view);
        return featuredViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position)
    {
        context.getPackageName();
        final FeaturedHelperClass featuredHelperClass = featuredLocations.get(position);

        holder.image.setImageResource(featuredHelperClass.getImage());
        holder.title.setText(featuredHelperClass.getTitle());
        holder.desc.setText(featuredHelperClass.getDescription());
        holder.layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (featuredHelperClass.getTitle() == "Heart Rate")
                {
                    Intent intent = new Intent(context, HeartActivity.class);
                    context.startActivity(intent);
                }
                else if (featuredHelperClass.getTitle() == "Steps Count")
                {
                    Intent intent = new Intent(context, StepsActivity.class);
                    context.startActivity(intent);
                }
                else if (featuredHelperClass.getTitle() == "Sleep Analysis")
                {
                    Intent intent = new Intent(context, SleepActivity.class);
                    context.startActivity(intent);
                }
                else if (featuredHelperClass.getTitle() == "Medication")
                {
                    Intent intent = new Intent(context, ReminderActivity.class);
                    context.startActivity(intent);
                }
                else if (featuredHelperClass.getTitle() == "Drive Mode")
                {
                    Intent intent = new Intent(context, DriveActivity.class);
                    context.startActivity(intent);
                }
                else if (featuredHelperClass.getTitle() == "Fall Detection")
                {
                    Intent intent = new Intent(context, FallDetectActivity.class);
                    context.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return featuredLocations.size();
    }


    public static class FeaturedViewHolder extends RecyclerView.ViewHolder
    {

        ImageView image;
        TextView title, desc;
        RelativeLayout layout;
        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);

            //Hooks
            image = itemView.findViewById(R.id.featured_image);
            title = itemView.findViewById(R.id.featured_title);
            desc = itemView.findViewById(R.id.featured_desc);
            layout = itemView.findViewById(R.id.featured_layout);
        }
    }

}
