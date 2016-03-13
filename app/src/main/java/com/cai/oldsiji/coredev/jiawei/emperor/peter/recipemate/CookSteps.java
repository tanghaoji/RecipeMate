package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate;

import java.util.ArrayList;

import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.ListView;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.Recipe;
import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.Step;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CookSteps extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 22;
    public static final int SELECT_FILE = 23;

    private int currStep = 0;
    public ArrayList<Step> steps= new ArrayList();

    public Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_steps);
        if(currStep == 0) {
            ImageButton prevButton = (ImageButton) findViewById(R.id.prevButton);
            prevButton.setVisibility(View.GONE);
        }

        Intent i = getIntent();
        recipe = (Recipe)i.getSerializableExtra("newRecipe");
    }

    public void addImageButton(View view) {
        final CharSequence[] items = { "Take Photo", "Choose from Library" };
        AlertDialog.Builder builder = new AlertDialog.Builder(CookSteps.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bm;
            if (requestCode == REQUEST_CAMERA) {
                bm = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
            }

            ImageButton addImageButton = (ImageButton) findViewById(R.id.stepAddImage);
            addImageButton.setVisibility(View.GONE);

            final int IMAGE_SIZE = 900;
            boolean landscape = bm.getWidth() > bm.getHeight();

            float scale_factor;
            if (landscape) scale_factor = (float)IMAGE_SIZE / bm.getHeight();
            else scale_factor = (float)IMAGE_SIZE / bm.getWidth();
            Matrix matrix = new Matrix();

            matrix.postScale(scale_factor, scale_factor);

            Bitmap croppedBitmap;
            if (landscape){
                int start = (bm.getWidth() - bm.getHeight()) / 2;
                croppedBitmap = Bitmap.createBitmap(bm, start, 0, bm.getHeight(), bm.getHeight(), matrix, true);
            } else {
                int start = (bm.getHeight() - bm.getWidth()) / 2;
                croppedBitmap = Bitmap.createBitmap(bm, 0, start, bm.getWidth(), bm.getWidth(), matrix, true);
            }

            ImageView imageView = (ImageView)findViewById(R.id.stepImageView);
            imageView.setImageBitmap(croppedBitmap);
        }
    }

    public void nextStep(View view) {
        EditText stepEditText = (EditText)findViewById(R.id.stepEditText);
        String stepDetail = stepEditText.getText().toString();

        ImageView imageView = (ImageView)findViewById(R.id.stepImageView);
        Drawable drawable= imageView.getDrawable();

        if (drawable == null) {
            Toast.makeText(CookSteps.this,"Please attach an image",Toast.LENGTH_LONG).show();
            return;
        }

        if (steps.size() - 1 > currStep) {
            Step nextStep = steps.get(currStep + 1);
            stepEditText.setText(nextStep.getStepDetail());
            imageView.setImageBitmap(nextStep.getThumbnail());
        } else {
            stepEditText.setText("");
            Bitmap thumbnail = ((BitmapDrawable)drawable).getBitmap();
            imageView.setImageDrawable(null);

            ImageButton addImageButton = (ImageButton) findViewById(R.id.stepAddImage);
            addImageButton.setVisibility(View.VISIBLE);

            steps.add(new Step(currStep,stepDetail,thumbnail));
        }

        currStep++;

        if(currStep > 0) {
            ImageButton prevButton = (ImageButton) findViewById(R.id.prevButton);
            prevButton.setVisibility(View.VISIBLE);
        }

    }

    public void prevStep(View view) {
        if (currStep - 1 < 0) return;

        Step previosStep = steps.get(currStep - 1);

        EditText stepEditText = (EditText)findViewById(R.id.stepEditText);
        String stepDetail = stepEditText.getText().toString();

        ImageView imageView = (ImageView)findViewById(R.id.stepImageView);
        Drawable drawable= imageView.getDrawable();

        if (drawable == null && stepDetail != "") {
            Toast.makeText(CookSteps.this,"Please attach an image",Toast.LENGTH_LONG).show();
            return;
        } else if (drawable == null && stepDetail == "") {
            if (steps.size() > currStep) steps.remove(currStep);
        } else {
            // save the last step
            Bitmap thumbnail = ((BitmapDrawable)drawable).getBitmap();

            if (steps.size() > currStep) {
                Step currentStep = steps.get(currStep);
                currentStep.setStepDetail(stepDetail);
                currentStep.setThumbnail(thumbnail);
            } else {
                steps.add(new Step(currStep,stepDetail,thumbnail));
            }
        }

        ImageButton addImageButton = (ImageButton) findViewById(R.id.stepAddImage);
        addImageButton.setVisibility(View.GONE);

        stepEditText.setText(previosStep.getStepDetail());
        imageView.setImageBitmap(previosStep.getThumbnail());

        currStep--;

        if(currStep == 0) {
            ImageButton prevButton = (ImageButton) findViewById(R.id.prevButton);
            prevButton.setVisibility(View.GONE);
        }
    }
    public void removeStepOnClickHandler(View v) {
        EditText stepEditText = (EditText)findViewById(R.id.stepEditText);
        ImageView imageView = (ImageView)findViewById(R.id.stepImageView);
        if (currStep < steps.size()) {
            steps.remove(currStep);
        }
        if (currStep > 0) {
            currStep--;
        }

        if (steps.size() == 0) {
            stepEditText.setText("");
            imageView.setImageDrawable(null);
            ImageButton prevButton = (ImageButton) findViewById(R.id.prevButton);
            prevButton.setVisibility(View.VISIBLE);
        } else {
            Step previosStep = steps.get(currStep);
            stepEditText.setText(previosStep.getStepDetail());
            imageView.setImageBitmap(previosStep.getThumbnail());
        }

        if (currStep == 0) {
            ImageButton prevButton = (ImageButton) findViewById(R.id.prevButton);
            prevButton.setVisibility(View.GONE);
        }
    }

    public void goToCover(View view) {
        ImageView imageView = (ImageView)findViewById(R.id.stepImageView);
        Drawable drawable= imageView.getDrawable();

        EditText stepEditText = (EditText)findViewById(R.id.stepEditText);
        String stepDetail = stepEditText.getText().toString();

        Bitmap thumbnail = ((BitmapDrawable)drawable).getBitmap();

        if (drawable == null) {
            Toast.makeText(CookSteps.this,"Please attach an image",Toast.LENGTH_LONG).show();
            return;
        }
        else {
            steps.add(new Step(currStep,stepDetail,thumbnail));
        }

        //recipe.addSteps(steps);

        Intent intent = new Intent(CookSteps.this, Cover.class);
        intent.putExtra("newRecipe", recipe);
        startActivity(intent);
    }
}
