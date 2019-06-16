package xyz.aungpyaephyo.padc.firebase.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.aungpyaephyo.padc.firebase.R;
import xyz.aungpyaephyo.padc.firebase.components.mmfont.MMFontUtils;
import xyz.aungpyaephyo.padc.firebase.data.models.NewsFeedModel;
import xyz.aungpyaephyo.padc.firebase.views.pods.AddedPhotoViewPod;

/**
 * Created by aung on 8/18/17.
 */

public class AddNewsActivity extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_news)
    EditText etNewsContent;

    @BindView(R.id.vp_added_photo)
    AddedPhotoViewPod vpAddedPhoto;

    @BindView(R.id.vp_add_photo)
    ViewGroup vpAddPhoto;

    private ProgressDialog mProgressDialog;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AddNewsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        ButterKnife.bind(this, this);

        toolbar.setTitle(getString(R.string.screen_title_add_news));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            Uri uri = data.getData();
            onPhotoTaken(uri);
            //mTakenPhotosAdapter.addNewData(uri.toString());
        }
    }

    @OnClick(R.id.btn_add_news)
    public void onTapAddNews(View view) {
        final String newsContent = etNewsContent.getText().toString();
        if (TextUtils.isEmpty(newsContent)) {
            etNewsContent.setError("Need news content to publish.");
        } else if (TextUtils.isEmpty(vpAddedPhoto.getPhotoUrl())) {
            Snackbar.make(etNewsContent, "You should select a photo relating to the news.", Snackbar.LENGTH_LONG).show();
        } else {
            showProgressDialogInfinite("Publishing your news");
            NewsFeedModel.getInstance().uploadFile(Uri.parse(vpAddedPhoto.getPhotoUrl()), new NewsFeedModel.UploadFileCallback() {
                @Override
                public void onUploadSucceeded(String uploadedPaths) {
                    dismissProgressDialog();
                    NewsFeedModel.getInstance().addNews(newsContent, uploadedPaths);
                    onBackPressed();
                }

                @Override
                public void onUploadFailed(String msg) {
                    Snackbar.make(etNewsContent, "Your photo couldn't be uploaded because : " + msg, Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    @OnClick(R.id.vp_add_photo)
    public void onTapAddPhoto(View view) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void onPhotoTaken(Uri photoUrl) {
        if (TextUtils.isEmpty(photoUrl.toString())) {
            Snackbar.make(etNewsContent, "ERROR : Path to photo is empty.", Snackbar.LENGTH_LONG).show();
        } else {
            vpAddedPhoto.setData(photoUrl.toString());
            vpAddedPhoto.setVisibility(View.VISIBLE);
            vpAddPhoto.setVisibility(View.GONE);
        }
    }

    private void showProgressDialogInfinite(String msg) {
        //if (mProgressDialog == null) {
        mProgressDialog = new ProgressDialog(this, R.style.AppDialog);
        //}

        if (!MMFontUtils.isSupportUnicode()) {
            mProgressDialog.setMessage(Html.fromHtml(MMFontUtils.uni2zg(msg)));
        } else {
            mProgressDialog.setMessage(Html.fromHtml(msg));
        }

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
