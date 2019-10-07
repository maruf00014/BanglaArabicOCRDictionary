package com.maruf.banglaarabicocrdictionary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Spinner dropdown1,dropdown2;

    EditText editText;

    ImageView imageView;


    int SelectedLanguage = 0;


    FirebaseVisionImage image;
    FirebaseVisionTextRecognizer textRecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dropdown1 = findViewById(R.id.spinner1);
        dropdown2 = findViewById(R.id.spinner2);
        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.camera);


        String[] items = new String[]{ "Arabic","Bangla",};

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);

        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0){
                    SelectedLanguage = 0;
                    dropdown2.setSelection(1);


                } else {

                    SelectedLanguage = 1;
                    dropdown2.setSelection(0);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0){
                    SelectedLanguage = 1;
                    dropdown1.setSelection(1);


                } else {

                    SelectedLanguage = 0;
                    dropdown1.setSelection(0);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        FirebaseVisionCloudTextRecognizerOptions options =
                new FirebaseVisionCloudTextRecognizerOptions.Builder()
                        .setLanguageHints(Arrays.asList("bn"))
                        .build();


       textRecognizer = FirebaseVision.getInstance()
                .getCloudTextRecognizer(options);



       imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                   startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
               }

           }
       });






            }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {



            Bitmap bitmap = (Bitmap) data.getExtras().get("data");


            image = FirebaseVisionImage.fromBitmap(bitmap);


            textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText result) {
                            // Task completed successfully
                            // ...

                            String resultText = result.getText();

                            editText.setText(resultText);
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    Toast.makeText(MainActivity.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
                                }
                            });






        }


    }

}