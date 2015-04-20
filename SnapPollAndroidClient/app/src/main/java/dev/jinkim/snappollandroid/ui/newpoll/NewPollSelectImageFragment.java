package dev.jinkim.snappollandroid.ui.newpoll;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.ui.adapter.SelectImageSourceAdapter;

/**
 * Created by Jin on 3/6/15.
 */
public class NewPollSelectImageFragment extends Fragment {
    public static final String TAG = NewPollSelectImageFragment.class.getSimpleName();

    private static final int REQ_CODE_PICK_IMAGE_FROM_GALLERY = 11;
    private static final int REQ_CODE_CAPTURE_FROM_CAMERA = 22;
    public static final int RESULT_OK = -1;

    private View rootView;
    private NewPollActivity mActivity;

    private ImageView ivImage;
    //    private Uri selectedImageUri;
    private ButtonFloat fabChooseImage;
    private Uri uriSelectedImage;
    private Uri outputFileUri;


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

        if (controller.getUriSelectedImg() != null) {
            Picasso.with(mActivity)
                    .load(controller.getUriSelectedImg())
                    .fit().centerInside()
                    .into(ivImage);
        } else {
            Picasso.with(mActivity)
                    .load(R.drawable.ic_placeholder_image)
                    .fit().centerInside()
                    .into(ivImage);
        }
    }

    private String getUniqueImageFilename() {
        return "img_" + System.currentTimeMillis() + ".jpg";
    }

    private void showChooseImageSourceDialog() {

        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.dialog_title_select_image_source)
                .adapter(new SelectImageSourceAdapter(mActivity, R.array.image_sources))
                .build();

        ListView list = dialog.getListView();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    // Camera
                    Toast.makeText(mActivity, "Camera selected", Toast.LENGTH_SHORT).show();
                    captureImageFromCamera();

                } else {
                    // Gallery
                    Toast.makeText(mActivity, "Gallery selected", Toast.LENGTH_SHORT).show();
                    pickImageFromGallery();
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

//    private void openImageIntent() {
//
//// Determine Uri of camera image to save.
//        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
//        root.mkdirs();
//        final String fname = getUniqueImageFilename();
//        final File sdImageMainDirectory = new File(root, fname);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);
//
//        // Camera.
//        final List<Intent> cameraIntents = new ArrayList<Intent>();
//        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        final PackageManager packageManager = mActivity.getPackageManager();
//        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
//        for(ResolveInfo res : listCam) {
//            final String packageName = res.activityInfo.packageName;
//            final Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(packageName);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            cameraIntents.add(intent);
//        }
//
//        // Filesystem.
//        final Intent galleryIntent = new Intent();
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//        // Chooser of filesystem options.
//        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
//
//        // Add the camera options.
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
//
//        startActivityForResult(chooserIntent, REQ_CODE_PICK_IMAGE);
//    }

    public void pickImageFromGallery() {
        Log.d(TAG, "Button clicked to pick image");
//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        //noinspection HardCodedStringLiteral
        myIntent.setType("image/*");
        startActivityForResult(myIntent, REQ_CODE_PICK_IMAGE_FROM_GALLERY);
    }

    public void captureImageFromCamera() {
//        Log.d(TAG, "Button clicked to pick image");
////        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
//        //noinspection HardCodedStringLiteral
//        myIntent.setType("image/*");
//        startActivityForResult(myIntent, REQ_CODE_CAPTURE_FROM_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE_FROM_GALLERY:
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
                break;

            case REQ_CODE_CAPTURE_FROM_CAMERA:
                break;
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

    @Override
    public void onResume() {
        super.onResume();

        if (controller.getUriSelectedImg() != null) {
            Picasso.with(mActivity)
                    .load(controller.getUriSelectedImg())
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

}
