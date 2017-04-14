package xyz.beerme.beerme;

import android.location.Location;
import android.media.Image;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jwnicholson on 2/25/2017.
 */

public class Post {

    private String mUid;
    private List<String> mTags;
    private Image mImage;
    private String mLikes;
    private String mDislikes;
    private int mVotes;
    private String mName;
    private String mLocation;
    private String mBeer;

    public Post()
    {
        //Needed for Firebase
    }

    public Post(String mUid, List<String> mTags, Image mImage, String mLikes, String mDislikes, int mVotes, String mName, String mLocation) {
        this.mUid = mUid;
        this.mTags = mTags;
        this.mImage = mImage;
        this.mLikes = mLikes;
        this.mDislikes = mDislikes;
        this.mVotes = mVotes;
        this.mName = mName;
        this.mLocation = mLocation;
    }

    public Post(String mUid, String mBeer, String mLikes,String mDislikes, String mLocation)
    {
        this.mUid = mUid;
        this.mLikes = mLikes;
        this.mDislikes = mDislikes;
        this.mLocation = mLocation;
        this.mBeer = mBeer;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public List<String> getmTags() {
        return mTags;
    }

    public void setmTags(List<String> mTags) {
        this.mTags = mTags;
    }

    public Image getmImage() {
        return mImage;
    }

    public void setmImage(Image mImage) {
        this.mImage = mImage;
    }

    public String getmLikes() {
        return mLikes;
    }

    public void setmLikes(String mLikes) {
        this.mLikes = mLikes;
    }

    public String getmDislikes() {
        return mDislikes;
    }

    public void setmDislikes(String mDislikes) {
        this.mDislikes = mDislikes;
    }

    public int getmVotes() {
        return mVotes;
    }

    public void setmVotes(int mVotes) {
        this.mVotes = mVotes;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmBeer() {
        return mBeer;
    }

    public void setmBeer(String mBeer) {
        this.mBeer = mBeer;
    }
}
