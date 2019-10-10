package com.maruf.banglaarabicocrdictionary;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
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
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Spinner dropdown1,dropdown2;

    EditText editText;

    ImageView imageView;
    Button translateButton;

    TextView translationTV;


    int sourceLanguage = FirebaseTranslateLanguage.AR;
    int targetLanguage = FirebaseTranslateLanguage.BN;

    CropImageView cropImageView;


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
        translateButton = findViewById(R.id.translate_Button);
        translationTV = findViewById(R.id.translation_tv);



        String[] items = new String[]{ "Arabic","Bangla",};

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown1.setAdapter(adapter);
        dropdown2.setAdapter(adapter);

        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0){
                    sourceLanguage = FirebaseTranslateLanguage.AR;
                    targetLanguage = FirebaseTranslateLanguage.BN;
                    dropdown2.setSelection(1);


                } else {


                    sourceLanguage = FirebaseTranslateLanguage.BN;
                    targetLanguage = FirebaseTranslateLanguage.AR;
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
                    sourceLanguage = FirebaseTranslateLanguage.BN;
                    targetLanguage = FirebaseTranslateLanguage.AR;
                    dropdown1.setSelection(1);


                } else {

                    sourceLanguage = FirebaseTranslateLanguage.AR;
                    targetLanguage = FirebaseTranslateLanguage.BN;
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


       translateButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {



               FirebaseTranslatorOptions options =
                       new FirebaseTranslatorOptions.Builder()
                               .setSourceLanguage(sourceLanguage)
                               .setTargetLanguage(targetLanguage)
                               .build();

               final FirebaseTranslator banglaArabicTranslator =
                       FirebaseNaturalLanguage.getInstance().getTranslator(options);

               FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                       .build();
               banglaArabicTranslator.downloadModelIfNeeded(conditions)
                       .addOnSuccessListener(
                               new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void v) {
                                       // Model downloaded successfully. Okay to start translating.
                                       // (Set a flag, unhide the translation UI, etc.)



                                       banglaArabicTranslator.translate(editText.getText().toString())
                                               .addOnSuccessListener(
                                                       new OnSuccessListener<String>() {
                                                           @Override
                                                           public void onSuccess(@NonNull String translatedText) {
                                                               translationTV.setText(translatedText);
                                                           }
                                                       })
                                               .addOnFailureListener(
                                                       new OnFailureListener() {
                                                           @Override
                                                           public void onFailure(@NonNull Exception e) {
                                                               // Error.
                                                               // ...
                                                           }
                                                       });

                                   }
                               })
                       .addOnFailureListener(
                               new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       // Model couldn’t be downloaded or other internal error.
                                       // ...
                                   }
                               });



           }
       });




        FirebaseTranslateModelManager modelManager = FirebaseTranslateModelManager.getInstance();

        // Download the French model.
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();

        FirebaseTranslateRemoteModel bnModel =
                new FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.BN)
                        .setDownloadConditions(conditions)
                        .build();
        modelManager.downloadRemoteModelIfNeeded(bnModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error.
                    }
                });

        FirebaseTranslateRemoteModel arModel =
                new FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.AR)
                        .setDownloadConditions(conditions)
                        .build();
        modelManager.downloadRemoteModelIfNeeded(arModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error.
                    }
                });











    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");


            final Dialog dialog = new Dialog(this, android.R.style.Theme_Light);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.crop_layout);

            cropImageView = dialog.findViewById(R.id.cropImageView);
            cropImageView.setImageBitmap(bitmap);
            dialog.setCancelable(false);

            dialog.findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bitmap cropped = cropImageView.getCroppedImage();

                    image = FirebaseVisionImage.fromBitmap(cropped);


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





                    dialog.dismiss();
                }
            });

            dialog.show();



        }


    }

}