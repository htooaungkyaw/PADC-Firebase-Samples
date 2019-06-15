package xyz.aungpyaephyo.padc.firebase.data.vo;

/**
 * Created by aung on 8/18/17.
 */

public class LikeVO {

    private long likeId;

    private long userId;

    private long timeStamp;

    public long getLikeId() {
        return likeId;
    }

    public long getUserId() {
        return userId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public static LikeVO initLike(String uid) {
        LikeVO like = new LikeVO();
        like.likeId = System.currentTimeMillis() / 1000;
        like.userId = Long.parseLong(uid);
        like.timeStamp = System.currentTimeMillis();
        return like;
    }
}
