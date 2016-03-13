package com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.Recipe;
import com.cai.oldsiji.coredev.jiawei.emperor.peter.recipemate.model.RecipeMate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cover extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 20;
    public static final int SELECT_FILE = 21;

    private static final String TAG = "Cover.java";
    private Recipe recipe;
    private Bitmap coverPhoto;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_cover);

        Intent i = getIntent();
        recipe = (Recipe)i.getSerializableExtra("newRecipe");
    }

    public void selectImage(View view) {
        final CharSequence[] items = { "Take Photo", "Choose from Library" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Cover.this);
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

                photoFile = destination;

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
                photoFile = new File(selectedImageUri.getPath());
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
            ImageButton addImageButton = (ImageButton) findViewById(R.id.addImageButton);
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

            ImageView imageView = (ImageView)findViewById(R.id.coverImage);
            imageView.setImageBitmap(croppedBitmap);

            coverPhoto = croppedBitmap;
        }
    }

    public void finishRecipe(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to save the recipe?");

        alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                recipe.addCoverPhoto(coverPhoto);
                RecipeMate.recipes.add(recipe);

                new PostDataAsyncTask().execute();

                //Toast.makeText(Cover.this,"Recipe saved",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Cover.this, MainActivity.class);
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class PostDataAsyncTask extends AsyncTask<Void, Integer, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            // do stuff before posting data
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // 1 = post text data, 2 = post file

                    postText();


                // post a file

                    //postFile();


            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void lenghtOfFile) {
            // do stuff after posting data
            Toast.makeText(Cover.this,"Recipe saved",Toast.LENGTH_LONG).show();
        }
    }

    // this will post our text data
    private void postText(){
        try{
            // url where the data will be posted
            String postReceiverUrl = "http://ec2-52-90-0-205.compute-1.amazonaws.com/setup.php";
            String postReceiverUrl2 = "http://ec2-52-90-0-205.compute-1.amazonaws.com/ingredient.php";
            Log.v(TAG, "postURL: " + postReceiverUrl);

            // HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);

            // add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("name", recipe.getName()));
            nameValuePairs.add(new BasicNameValuePair("description", recipe.getDescription()));
            nameValuePairs.add(new BasicNameValuePair("tasty", String.valueOf(recipe.getTaste())));
            nameValuePairs.add(new BasicNameValuePair("difficulty", String.valueOf(recipe.getDifficulty())));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            for(int i = 0; i < recipe.getIngredientsList().size(); i++) {
                HttpPost httpPost2 = new HttpPost(postReceiverUrl2);

                List<NameValuePair> nameValuePairs2 = new ArrayList<>(3);
                nameValuePairs2.add(new BasicNameValuePair("name",recipe.getIngredientsList().get(i).getName()));
                nameValuePairs2.add(new BasicNameValuePair("amount", String.valueOf(recipe.getIngredientsList().get(i).getValue())));
                nameValuePairs2.add(new BasicNameValuePair("unit",recipe.getIngredientsList().get(i).getUnit()));

                HttpResponse response2 = httpClient.execute(httpPost2);
            }

            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity).trim();
                Log.v(TAG, "Response: " +  responseStr);

                // you can add an if statement here and do other actions based on the response
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // will post our text file
    private void postFile(){
        try{

            // the file to be posted
            //String textFile = Environment.getExternalStorageDirectory() + "/sample.txt";
            //Log.v(TAG, "textFile: " + textFile);

            // the URL where the file will be posted
            String postReceiverUrl = "http://ec2-52-90-0-205.compute-1.amazonaws.com/imageloader.php";
            Log.v(TAG, "postURL: " + postReceiverUrl);

            // new HttpClient
            HttpClient httpClient = new DefaultHttpClient();

            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);

            //File file = new File(textFile);
            FileBody fileBody = new FileBody(photoFile);

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("file", fileBody);
            httpPost.setEntity(reqEntity);

            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity).trim();
                Log.v(TAG, "Response: " +  responseStr);

                // you can add an if statement here and do other actions based on the response
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


