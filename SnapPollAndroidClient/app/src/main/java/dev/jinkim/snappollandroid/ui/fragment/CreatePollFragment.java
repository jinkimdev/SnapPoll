package dev.jinkim.snappollandroid.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;

import java.io.File;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.imgur.ImgurConstants;
import dev.jinkim.snappollandroid.model.ImgurResponse;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.util.FileUtils;
import dev.jinkim.snappollandroid.web.ImgurRestClient;
import dev.jinkim.snappollandroid.web.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Jin on 11/27/14.
 */
public class CreatePollFragment extends Fragment {

    public static final String TAG = "#### CreatePollFragment";

    private static final int REQ_CODE_PICK_IMAGE = 1;
    public static final int RESULT_OK = -1;

    private Button btnTest;
    private Button btnAttachImage;
    private ActionProcessButton btnSubmit;

    private ImageView ivThumbnail;

    private Uri uriSelectedImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_create_poll, container, false);

        ivThumbnail = (ImageView) rootView.findViewById(R.id.iv_thumbnail);
        Picasso.with(getActivity())
                .load(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .fit().centerInside()
                .into(ivThumbnail);

        btnSubmit = (ActionProcessButton) rootView.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setMode(ActionProcessButton.Mode.ENDLESS);
                btnSubmit.setProgress(1);

                //TODO: Upload image to imgur
                //TODO when url is retrieved, make another call to POST /poll
                // grab info on poll
                convertImage(uriSelectedImage);
            }
        });

        btnTest = (Button) rootView.findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setProgress(100);
                test();
            }
        });

        btnAttachImage = (Button) rootView.findViewById(R.id.btn_attach_image);
        btnAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(v);
            }
        });
        return rootView;
    }

    public void pickImage(View view) {
        Log.d(TAG, "Button clicked to pick image");
//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        myIntent.setType("image/*");
        startActivityForResult(myIntent, REQ_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    uriSelectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "Image selected: " + uriSelectedImage.toString());
                    updateThumbnail(uriSelectedImage);
                }
        }
    }

    private void updateThumbnail(Uri selectedImage) {
        Log.d(TAG, "updateThumbnail");

        Picasso.with(getActivity())
                .load(selectedImage)
                .fit().centerInside()
                .into(ivThumbnail);
    }

//    void setImage(Uri imageUri) {
//
//
//        mImageUri = imageUri;
//        mImagePreviewBitmap = BitmapUtils.decodeSampledBitmapFromUri(imageUri, 400, 400);
//        if (getView() != null) {
//            ((ImageView) getView().findViewById(R.id.choose_image_preview)).setImageBitmap(mImagePreviewBitmap);
//            new MyImgurUploadTask(imageUri).execute();
//        }
//    }


    private void convertImage(Uri uriSelectedImage) {

        //TODO: Check if this util works with various versions of Android
        File file = FileUtils.getFile(getActivity(), uriSelectedImage);

        //TODO: Catch exception and handle it
        TypedFile tf = new TypedFile("image/jpeg", file);

        ImgurRestClient imgurRest = new ImgurRestClient();
        imgurRest.getApiService().uploadImage("Client-ID " + ImgurConstants.MY_IMGUR_CLIENT_ID, tf, new Callback<ImgurResponse>() {
            @Override
            public void success(ImgurResponse imgurResponse, Response response) {
                Log.d(TAG, "Success: Image uploaded to Imgur");
                Log.d(TAG, "Imgur link: " + imgurResponse.data.link);
                Log.d(TAG, "Imgur deleteHash: " + imgurResponse.data.deletehash);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failure: Image not uploaded to Imgur");
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    void test() {
        RestClient rest = new RestClient();
//            rest.getApiService().getPolls(new Callback<List<Poll>>() {
//                @Override
//                public void success(List<Poll> polls, Response response) {
//                    Log.d("####", "Polls: " + polls.get(0).question);
//                    Log.d("####", "Time: " + polls.get(0).pollTimestamp);
//                }
//
//                @Override
//                public void failure(RetrofitError retrofitError) {
//                    // Log error here since request failed
//                    Log.d("####", "" + retrofitError.toString());
//                }
//            });

        rest.getApiService().getPoll(6, new Callback<Poll>() {
            @Override
            public void success(Poll poll, Response response) {
                Log.d("####", "Poll by id: " + poll.question);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
