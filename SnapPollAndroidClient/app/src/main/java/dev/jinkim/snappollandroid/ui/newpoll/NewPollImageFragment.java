package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gc.materialdesign.views.ButtonFloat;
import com.squareup.picasso.Picasso;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollImageFragment extends Fragment {
    public static final String TAG = "CreatePollFragment";

    private static final int REQ_CODE_PICK_IMAGE = 1;
    public static final int RESULT_OK = -1;

    private View rootView;
    private NewPollActivity mActivity;

    private ImageView ivImage;
    private Uri selectedImageUri;
    private ButtonFloat btnChooseImage;
    private Uri uriSelectedImage;

    private NewPollController controller;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_new_poll_image, container, false);
        setHasOptionsMenu(true);

        mActivity = (NewPollActivity) getActivity();
        controller = mActivity.getController();

        mActivity.getSupportActionBar().setTitle("Select Image");

        initializeViews(rootView);

        return rootView;
    }

    private void initializeViews(View view) {
        ivImage = (ImageView) view.findViewById(R.id.new_poll_image_iv_thumbnail);
        btnChooseImage = (ButtonFloat) view.findViewById(R.id.btn_choose_image);
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(v);
            }
        });

        if (selectedImageUri != null) {
            Picasso.with(mActivity)
                    .load(selectedImageUri)
                    .centerInside()
                    .into(ivImage);
        } else {
            Picasso.with(mActivity)
                    .load(R.drawable.ic_placeholder_image)
                    .fit().centerInside()
                    .into(ivImage);
        }
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

                    // display selected image
                    updateThumbnail(uriSelectedImage);

                    // save the image in the controller
                    controller.setUriSelectedImg(uriSelectedImage);
//
//                    // clear previously uploaded imgur response
//                    if (currentImgurResponse != null)
//                        currentImgurResponse = null;
                }
        }
    }

    private void updateThumbnail(Uri selectedImage) {
        Log.d(TAG, "updateThumbnail");

//        flThumbnailContainer.setVisibility(View.VISIBLE);

        Picasso.with(getActivity())
                .load(selectedImage)
                .fit().centerInside()
                .into(ivImage);
    }
}
