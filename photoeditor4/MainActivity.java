package com.example.photoeditor4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.photoeditor4.Interface.EditImageFragmentListener;
import com.example.photoeditor4.Interface.FiltersListFragmentListener;
import com.example.photoeditor4.Utilites.BitMapUtilities;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {
    public static final int PERMISSION_PICK_IMAGE = 1000;
    public static final int CAMERA_REQUEST = 1001;
    public static Uri resultUri, image_selected_uri;

    public static PhotoEditorView photoEditorView;
    private PhotoEditor photoEditor;
    private CoordinatorLayout coordinatorLayout;

    public static Bitmap originalBitmap, filteredBitmap, finalBitmap;

    private CardView btn_filters_list,btn_effect, btn_edit;

    private FiltersListFragment filtersListFragment;
    private EditImageFragment editImageFragment;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;


    static{
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Photo Editor");

        //View
        photoEditorView = (PhotoEditorView) findViewById(R.id.image_preview);
        photoEditor = new PhotoEditor.Builder(this,photoEditorView).setPinchTextScalable(true).build();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

        btn_effect = (CardView) findViewById(R.id.btn_effects);
        btn_filters_list = (CardView) findViewById(R.id.btn_filters_list);
        btn_edit = (CardView) findViewById(R.id.btn_edits) ;

        btn_filters_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filtersListFragment != null){
                    filtersListFragment.show(getSupportFragmentManager(),filtersListFragment.getTag());
                }
                else{
                    FiltersListFragment filtersListFragment = FiltersListFragment.getInstance(null);
                    filtersListFragment.setListener(MainActivity.this);
                    filtersListFragment.show(getSupportFragmentManager(),filtersListFragment.getTag());
                }
            }
        });

        btn_effect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(editImageFragment != null){
                   editImageFragment.show(getSupportFragmentManager(),editImageFragment.getTag());
                }
                else {
                    EditImageFragment editImageFragment = EditImageFragment.getInstance();
                    editImageFragment.setListener(MainActivity.this);
                    editImageFragment.show(getSupportFragmentManager(), editImageFragment.getTag());
                }
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image_selected_uri != null)
                    startCrop(image_selected_uri);
                else if (resultUri != null)
                    startCrop((resultUri));
            }
        });

        openImageFromGallery();
    }

    private void startCrop(Uri uri) {
        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append("jpg").toString();
        UCrop ucrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
        ucrop.start(MainActivity.this);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalBitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
        EditImageFragment.instance = null;
    }

    private void resetControl() {
        if(editImageFragment != null)
            editImageFragment.resetControls();
        brightnessFinal = 0;
        saturationFinal = 1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_open){
            openImageFromGallery();
            return true;
        }
        else if(id == R.id.action_save){
            saveImageToGallery();
            return true;
        }
        else if(id == R.id.action_camera){
            openCamera();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {
                                   try {
                                        photoEditorView.getSource().setImageBitmap(saveBitmap);
                                       final String path = BitMapUtilities.insertImage(getContentResolver(),
                                                saveBitmap, System.currentTimeMillis() +".jpg",
                                                null);
                                        if(!TextUtils.isEmpty(path)){
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Image saved to Gallery!",Snackbar.LENGTH_LONG)
                                                    .setAction("Open", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            openImage(path);
                                                        }
                                                    });
                                            snackbar.show();
                                        }
                                        else{
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Unable to save to Gallery!",Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                        }
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Permission Not Granted!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Permission Not Granted!",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void openCamera() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE,"New Photo");
                            values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
                            image_selected_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_selected_uri);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);

                        }
                        else{
                            Toast.makeText(MainActivity.this, "Permission Not Granted!",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == PERMISSION_PICK_IMAGE){
            Bitmap bitmap = BitMapUtilities.getBitmapFromGallery(this, data.getData(), 800,800);
            image_selected_uri = data.getData();

            //Refresh bitmap memory
            if(originalBitmap != null | finalBitmap != null | filteredBitmap != null) {
                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();
            }

            //Setting bitmap memory
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            photoEditorView.getSource() .setImageBitmap((originalBitmap));
            bitmap.recycle();

            //Thumbnail Rendering for image
            filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
            filtersListFragment.setListener(this);
        }
        else if(resultCode == RESULT_OK && requestCode == CAMERA_REQUEST){
            if(data == null)
                return;

            Bitmap bitmap = BitMapUtilities.getBitmapFromGallery(this,image_selected_uri,800,800);

            if(originalBitmap != null | finalBitmap != null | filteredBitmap != null) {
                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();
            }

            //Setting bitmap memory
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            photoEditorView.getSource().setImageBitmap((originalBitmap));
            bitmap.recycle();

            //Thumbnail Rendering for image
            filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
            filtersListFragment.setListener(this);
        }
        else if (requestCode == UCrop.REQUEST_CROP)
            handleCropResult(data);
        else if(requestCode == UCrop.RESULT_ERROR)
            handleCropError(data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleCropError(Intent data) {
        final Throwable cropError = UCrop.getError(data);
        if(cropError != null){
            Toast.makeText(this,""+cropError.getMessage(), Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,"Unexpected Error", Toast.LENGTH_LONG).show();
        }
    }

    private void handleCropResult(Intent data) {
        if (data != null) {
            resultUri = UCrop.getOutput(data);
            image_selected_uri = UCrop.getOutput(data);

            if(image_selected_uri != null){
                photoEditorView.getSource().setImageURI(image_selected_uri);
                originalBitmap = BitMapUtilities.drawableToBitmap(photoEditorView.getSource().getDrawable());
                filteredBitmap = originalBitmap;
                finalBitmap = originalBitmap;
            }
           else if(resultUri != null) {
                photoEditorView.getSource().setImageURI(resultUri);
                originalBitmap = BitMapUtilities.drawableToBitmap(photoEditorView.getSource().getDrawable());
                filteredBitmap = originalBitmap;
                finalBitmap = originalBitmap;
            }
            else
                Toast.makeText(this, "Cannot retrieve crop image!", Toast.LENGTH_LONG).show();
        }
    }
}