package xyz.beerme.beerme;

import android.content.Context;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Josh on 4/24/2017.
 */

public class CustomAdapter extends ArrayAdapter<Post>{

    public CustomAdapter(Context context, ArrayList<Post> posts) {
        super(context, R.layout.custom_row_layout, posts);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        View customView = mInflater.inflate(R.layout.custom_row_layout, parent, false);

        //View initialization
        Post singlePost = getItem(position);
        TextView nameText = (TextView) customView.findViewById(R.id.name_text_view);
        TextView beerText = (TextView) customView.findViewById(R.id.beer_text_view);
        TextView likesText = (TextView) customView.findViewById(R.id.likes_text_view);
        TextView dislikesText = (TextView) customView.findViewById(R.id.dislikes_text_view);
        TextView locationText = (TextView) customView.findViewById(R.id.location_text_view);
        ImageView imageView = (ImageView) customView.findViewById(R.id.fb_image);

        //Sets all views using Post object
        nameText.setText(singlePost.getmAuthor());
        imageView.setImageResource(android.R.color.transparent);
        beerText.setText("Beer: " + singlePost.getmBeer());
        likesText.setText("Likes: " + singlePost.getmLikes());
        dislikesText.setText("Dislikes: " + singlePost.getmDislikes());
        locationText.setText("Location: " + singlePost.getmLocation());

        //Checks if photo is present as this is optional
        if(singlePost.getmUrl() != null) {
            Picasso.with(getContext()).load(singlePost.getmUrl()).into(imageView);
        }

        return customView;
    }
}
