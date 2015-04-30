package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFloat;
import com.soundcloud.android.crop.Crop;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.event.BusProvider;
import dev.jinkim.snappollandroid.event.ImagePickedFromGalleryEvent;
import dev.jinkim.snappollandroid.ui.adapter.SelectImageSourceAdapter;
import dev.jinkim.snappollandroid.util.ImageFileUtil;

/**
 * Created by Jin on 3/6/15.
 * <p/>
 * Fragment for attaching an image (from Gallery or Camera) in creating a poll
 */
public class NewPollSelectImageFragment extends Fragment {
    public static final String TAG = NewPollSelectImageFragment.class.getSimpleName();


    private View rootView;
    private NewPollActivity mActivity;

    private ImageView ivImage;
    //    private Uri selectedImageUri;
    private ButtonFloat fabChooseImage;
    private Uri selectedImageUri;


    private NewPollController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_new_poll_image, container, false);
        setHasOptionsMenu(true);

        mActivity = (NewPollActivity) getActivity();
        controller = mActivity.getController();

        mActivity.getSupportActionBar().setTitle(R.string.title_new_poll_image_frag);

        initializeViews(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initializeViews(View view) {
        ivImage = (ImageView) view.findViewById(R.id.new_poll_image_iv_thumbnail);
        fabChooseImage = (ButtonFloat) view.findViewById(R.id.fab_choose_image);
        fabChooseImage.setBackgroundColor(getResources().getColor(R.color.fab_background));
        fabChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseImageSourceDialog();
//                openImageIntent();

//                pickImageFromGallery(v);
            }
        });

//        if (controller.getUriSelectedImg() != null) {
//            Picasso.with(mActivity)
//                    .load(controller.getUriSelectedImg())
//                    .fit().centerInside()
//                    .into(ivImage);
//        } else {
        Picasso.with(mActivity)
                .load(R.drawable.ic_placeholder_image)
                .fit().centerInside()
                .into(ivImage);
//        }
    }

    private void showChooseImageSourceDialog() {

        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.dialog_title_select_image_source)
                .adapter(new SelectImageSourceAdapter(mActivity, R.array.image_sources))
                .build();

        ListView list = dialog.getListView();
        if (list != null) {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    mActivity.clearCapturedImageUri();
//                    mActivity.setCapturedPhotoPath(null);

                    switch (position) {
                        case 0:
                            // Camera
                            Toast.makeText(mActivity, "Camera selected", Toast.LENGTH_SHORT).show();
                            captureImageFromCamera();
                            break;

                        case 1:
                            // Gallery
                            Toast.makeText(mActivity, "Gallery selected", Toast.LENGTH_SHORT).show();
                            pickImageFromGallery();
                            break;

                        default:
                            break;
                    }

                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    public void pickImageFromGallery() {
        Log.d(TAG, "Button clicked to pick image");
//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        //noinspection HardCodedStringLiteral
        myIntent.setType("image/*");
        mActivity.startActivityForResult(myIntent, NewPollActivity.REQ_CODE_PICK_IMAGE_FROM_GALLERY);
    }

    public void captureImageFromCamera() {
        // Check if there is a camera.
        PackageManager packageManager = mActivity.getPackageManager();

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = ImageFileUtil.createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                Toast.makeText(mActivity, "There was a problem saving the photo...", Toast.LENGTH_SHORT).show();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = ImageFileUtil.getOutputMediaFileUri(ImageFileUtil.MEDIA_TYPE_IMAGE);

                mActivity.setSelectedImageUri(fileUri);
                mActivity.setCapturedPhotoPath(fileUri.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mActivity.getSelectedImageUri());
                startActivityForResult(takePictureIntent, NewPollActivity.REQ_CODE_CAPTURE_FROM_CAMERA);
            }
        }
    }

    //    public void captureImageFromCamera() {
//        File imageDirectory = new File("/sdcard/SnapPoll");
//        String path = imageDirectory.toString().toLowerCase();
//        String name = imageDirectory.getName().toLowerCase();
//
//
//    String fileName = ImageFileUtil.getUniqueImageFilename();
//        ContentValues values = new ContentValues();
//
//        values.put(MediaStore.Images.Media.TITLE, "Image");
//        values.put(MediaStore.Images.Media.BUCKET_ID, path.hashCode());
//        values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, name);
//
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
//        values.put("_data", "/sdcard/SnapPoll/" + ImageFileUtil.getUniqueImageFilename());
//
//        // capturedImageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
//        mActivity.setSelectedImageUri(
//                mActivity.getContentResolver()
//                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        new ContentValues()));
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, mActivity.getSelectedImageUri());
////        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//        startActivityForResult(intent, REQ_CODE_CAPTURE_FROM_CAMERA);
//    }


    private void updateThumbnail(Uri selectedImageUri) {
        Log.d(TAG, "updateThumbnail");

        Picasso.with(getActivity())
                .load(selectedImageUri)
                .fit().centerInside()
                .into(ivImage);
    }

    private void updateThumbnail(String selectedImagePath) {
        Log.d(TAG, "updateThumbnail");

        Picasso.with(getActivity())
                .load(selectedImagePath)
                .fit().centerInside()
                .into(ivImage);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mActivity.getSelectedImageUri() != null) {
            Picasso.with(mActivity)
                    .load(mActivity.getSelectedImageUri())
                    .fit().centerInside()
                    .into(ivImage);
        } else if (mActivity.getCapturedPhotoPath() != null) {
            Picasso.with(mActivity)
                    .load(mActivity.getCapturedPhotoPath())
                    .fit().centerInside()
                    .into(ivImage);
        }
    }

    /**
     * takes in relative position to be animated to
     *
     * @param position
     */
    public void moveFloatButton(float position) {
        fabChooseImage.animate().translationY(position);
    }

    /* Call Android Crop Activity */
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(mActivity.getCacheDir(), "cropped"));
        new Crop(source).output(destination).start(mActivity);
    }

    @Subscribe
    public void onImagePickedFromGalleryEvent(ImagePickedFromGalleryEvent event) {
        updateThumbnail(event.selectedImageUri);
    }

    @Subscribe
    public void onPhotoCapturedFromCameraEvent(PhotoCapturedFromCameraEvent event) {
        updateThumbnail(event.capturedPhotoPath);
    }



    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }
}
