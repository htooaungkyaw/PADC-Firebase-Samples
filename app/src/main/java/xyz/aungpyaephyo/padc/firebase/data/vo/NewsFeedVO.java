package xyz.aungpyaephyo.padc.firebase.data.vo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by aung on 8/18/17.
 */

public class NewsFeedVO {

    @SerializedName("feed_id")
    private long feedId;

    @SerializedName("postedDate")
    private long postedDate;

    @SerializedName("content")
    private String content;

    @SerializedName("image")
    private String image;

    @SerializedName("send_to")
    private int sendToCount;

    @SerializedName("news_author")
    private NewsAuthorVO newsAuthor;

    @SerializedName("likes")
    private List<LikeVO> likes;

    @SerializedName("comments")
    private List<CommentVO> comments;

    public long getFeedId() {
        return feedId;
    }

    public long getPosedDate() {
        return postedDate;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public int getSendToCount() {
        return sendToCount;
    }

    public NewsAuthorVO getNewsAuthor() {
        return newsAuthor;
    }

    public List<LikeVO> getLikes() {
        return likes;
    }

    public List<CommentVO> getComments() {
        return comments;
    }
}
