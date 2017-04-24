package xyz.beerme.beerme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import xyz.beerme.beerme.Post;

public class ListViewAdapter{

    Activity activity;
    List<Post> lstPosts;
    LayoutInflater inflater;

    public ListViewAdapter(Activity activity, List<Post> lstPosts) {
        this.activity = activity;
        this.lstPosts = lstPosts;
    }


    public int getCount() {
        return lstPosts.size();
    }


    public Object getItem(int i) {
        return lstPosts.get(i);
    }


    public long getItemId(int i) {
        return i;
    }
}