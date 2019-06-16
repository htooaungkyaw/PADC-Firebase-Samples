package xyz.aungpyaephyo.padc.firebase.data.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.aungpyaephyo.padc.firebase.FirebaseApp;
import xyz.aungpyaephyo.padc.firebase.data.vo.NewsFeedVO;
import xyz.aungpyaephyo.padc.firebase.events.FirebaseEvents;

/**
 * Created by aung on 8/18/17.
 */

public class NewsFeedModel {

    private static final String PATH_SAMPLE_DATA = "sample_data";
    private static final String OFFLINE_NEWSFEED = "news_feed.json";

    private static final String MM_NEWS_FEED = "mmNewsFeed";

    private static NewsFeedModel objInstance;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mNewsFeedDR;

    private FirebaseFirestore mFirestore;

    private NewsFeedModel() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public static NewsFeedModel getInstance() {
        if (objInstance == null) {
            objInstance = new NewsFeedModel();
        }
        return objInstance;
    }

    public List<NewsFeedVO> getSampleNewsFeed(Context context) {
        try {
            String newsFeed = loadDummyData(context, OFFLINE_NEWSFEED);
            Type listType = new TypeToken<List<NewsFeedVO>>() {
            }.getType();
            List<NewsFeedVO> newsFeedList = new Gson().fromJson(newsFeed, listType);
            return newsFeedList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void loadNewsFeed() {
        mNewsFeedDR = mDatabaseReference.child(MM_NEWS_FEED);
        mNewsFeedDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    List<NewsFeedVO> newsFeedList = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NewsFeedVO newsFeed = snapshot.getValue(NewsFeedVO.class);
                        newsFeedList.add(newsFeed);
                    }


                    uploadNewsFeedToFirestore(newsFeedList);

                    FirebaseEvents.NewsFeedLoadedEvent event = new FirebaseEvents.NewsFeedLoadedEvent(newsFeedList);
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void uploadNewsFeedToFirestore(List<NewsFeedVO> news){

        final Map<String, NewsFeedVO> map = new HashMap<>();

        for (NewsFeedVO newsFeed : news) {
            map.put(newsFeed.getFeedId()+"abcd", newsFeed);
        }

        final CollectionReference mmNewsFeed = mFirestore.collection("mmNewsFeed");
        mmNewsFeed.document("news").set(map);

        // Get a new write batch
        /*
        WriteBatch batch = mFirestore.batch();

                for (NewsFeedVO newsFeed : news) {
            final DocumentReference documentReference =
                    mFirestore.collection("mmNewsFeed").
                            document(newsFeed.getFeedId()+"abcd");

            batch.set(documentReference, newsFeed);
        }

                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(FirebaseApp.TAG, "uploading complete");
                    }
                });

                */

    }

    public void loadNewsFeedFromFirestore(){


    }

    /**
     * Read text from assets folder.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    private byte[] readJsonFile(Context context, String fileName) throws IOException {
        InputStream inStream = context.getAssets().open(fileName);
        int size = inStream.available();
        byte[] buffer = new byte[size];
        inStream.read(buffer);
        inStream.close();
        return buffer;
    }

    /**
     * @param fileName - name of Json File.
     * @return JSONObject from loaded file.
     * @throws IOException
     * @throws JSONException
     */
    private String loadDummyData(Context context, String fileName) throws IOException, JSONException {
        byte[] buffer = readJsonFile(context, PATH_SAMPLE_DATA + "/" + fileName);
        return new String(buffer, "UTF-8").toString();
    }
}
