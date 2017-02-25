package xyz.beerme.beerme;

import android.location.Location;
import android.media.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jwnicholson on 2/25/2017.
 */

public class Post {

    private List<String> mTags;
    private Image mImage;
    private String mLikes;
    private String mDislikes;
    private int mVotes;
    private String mName;
    private String mUid;
    private Location mLocation;

    public Post()
    {
        //Needed for Firebase
    }

    public Post(List<String> mTags, Image mImage, String mLikes, String mDislikes, int mVotes, String mName, String mUid, Location mLocation) {
        this.mTags = mTags;
        this.mImage = mImage;
        this.mLikes = mLikes;
        this.mDislikes = mDislikes;
        this.mVotes = mVotes;
        this.mName = mName;
        this.mUid = mUid;
        this.mLocation = mLocation;
    }

    public List<String> getmTags() {
        return mTags;
    }

    public Image getmImage() {
        return mImage;
    }

    public String getmLikes() {
        return mLikes;
    }

    public String getmDislikes() {
        return mDislikes;
    }

    public int getmVotes() {
        return mVotes;
    }

    public String getmName() {
        return mName;
    }

    public String getmUid() {
        return mUid;
    }

    public Location getmLocation() {
        return mLocation;
    }

    public void setmTags(ArrayList<String> mTags) {
        this.mTags = mTags;
    }

    public void setmImage(Image mImage) {
        this.mImage = mImage;
    }

    public void setmLikes(String mLikes) {
        this.mLikes = mLikes;
    }

    public void setmDislikes(String mDislikes) {
        this.mDislikes = mDislikes;
    }

    public void setmVotes(int mVotes) {
        this.mVotes = mVotes;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }
}
