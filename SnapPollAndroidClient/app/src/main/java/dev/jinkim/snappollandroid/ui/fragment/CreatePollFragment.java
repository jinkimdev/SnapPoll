package dev.jinkim.snappollandroid.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.web.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jin on 11/27/14.
 */
public class CreatePollFragment extends Fragment {

    public static final String TAG = "#### CreatePollFragment";

    private static final int REQ_CODE_PICK_IMAGE = 1;
    public static final int RESULT_OK = -1;

    private Button btnTest;
    private Button btnAttachImage;
    private ImageView ivThumbnail;

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

        Button btnTest = (Button) rootView.findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Uri selectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "Image selected: " + selectedImage.toString());
                    updateThumbnail(selectedImage);
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
