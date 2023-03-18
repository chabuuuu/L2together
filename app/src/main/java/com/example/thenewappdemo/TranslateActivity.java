package com.example.thenewappdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TranslateActivity extends AppCompatActivity {
    private EditText sourceLanguageEt;
    private TextView destinationLanguageTv;
    private MaterialButton sourceLanguageChooseBtn;
    private MaterialButton destinationLanguageChooseBtn;
    private MaterialButton translateBtn;
    private TranslatorOptions translatorOptions;
    private Translator translator;
    private ProgressDialog progressDialog;
    private ArrayList<ModelLanguage> languageArrayList;
    private static final String TAG = "MAIN_TAG";


    private String sourceLanguageCode = "en";
    private String sourceLanguageTitle = "English";
    private String destinationLanguageCode = "vi";
    private String destinationLanguageTitle = "Vietnamese";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        sourceLanguageEt = findViewById(R.id.sourceLanguageEt);
        destinationLanguageTv = findViewById(R.id.destinationLanguageTv);
        sourceLanguageChooseBtn = findViewById(R.id.sourceLanguageChooseBtn);
        destinationLanguageChooseBtn = findViewById(R.id.destinationLanguageChooseBtn);
        translateBtn = findViewById(R.id.translateBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Hãy chờ một chút nhé...");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAvailableLanguages();


        sourceLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SourceLanguageChoose();

            }
        });

        destinationLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationLanguageChoose();

            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData ();

            }
        });


    }

    private String sourceLanguageText = "";

    private void validateData() {

        sourceLanguageText =  sourceLanguageEt.getText().toString().trim();
        Log.d(TAG, "valiateData: sourceLanguageText: " +sourceLanguageText);
        if (sourceLanguageText.isEmpty()){
            Toast.makeText(this, "Nhập văn bản cần dịch", Toast.LENGTH_SHORT).show();
        }
        else{
            startTranslations();
        }


    }

    private void startTranslations() {
        progressDialog.setMessage("Đang chạy Language model... ");
        progressDialog.show();
        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();
        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: model ready, starting translate...");

                        progressDialog.setMessage("Đang dịch...");


                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {

                                       Log.d(TAG, "onSuccess: translatedText: " +translatedText);
                                       progressDialog.dismiss();

                                       destinationLanguageTv.setText(translatedText);



                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        progressDialog.dismiss();
                                        Log.e(TAG, "onFailure ", e);
                                        Toast.makeText(TranslateActivity.this, "Failed to translated due to" + e.getMessage(), Toast.LENGTH_SHORT).show();





                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onFailure ", e);
                        Toast.makeText(TranslateActivity.this, "Chạy model gạp lỗi vì: " + e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });
    }

    private void  SourceLanguageChoose(){
        PopupMenu popupMenu = new PopupMenu(this, sourceLanguageChooseBtn);
        for (int i = 0 ; i< languageArrayList.size();i++){
            popupMenu.getMenu().add(Menu.NONE,i,i,languageArrayList.get(i).languageTitle);


        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int position = item.getItemId();
                sourceLanguageCode  = languageArrayList.get(position).languageCode;
                sourceLanguageTitle= languageArrayList.get(position).languageTitle;

                sourceLanguageChooseBtn.setText(sourceLanguageTitle);
                sourceLanguageEt.setHint("Nhập "+sourceLanguageTitle);

                Log.d(TAG, "onMenuItemClick: sourceLanguageCode: "+ sourceLanguageCode);
                Log.d(TAG, "onMenuItemClick: sourceLanguageTitle: "+ sourceLanguageTitle);

                return false;
            }
        });
    }

    private void destinationLanguageChoose(){
        PopupMenu popupMenu = new PopupMenu(this, destinationLanguageChooseBtn);

        for (int i =0; i< languageArrayList.size(); i++){
            popupMenu.getMenu().add(Menu.NONE,i, i, languageArrayList.get(i).getLanguageTitle());


        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int position = item.getItemId();

                destinationLanguageCode = languageArrayList.get(position).languageCode;
                destinationLanguageTitle = languageArrayList.get(position).languageTitle;

                destinationLanguageChooseBtn.setText(destinationLanguageTitle);
                Log.d(TAG, "onMenuItemClick: destinationLanguageCode: "+ destinationLanguageCode);
                Log.d(TAG, "onMenuItemClick: destinationLanguageTitle: "+ destinationLanguageTitle);

                return false;


            }
        });


    }

    private void loadAvailableLanguages() {
        languageArrayList = new ArrayList<>();
        List<String> languageCodeList = TranslateLanguage.getAllLanguages();

        for (String languageCode: languageCodeList){
            String languageTitle = new Locale(languageCode).getDisplayLanguage();

            Log.d(TAG, "loadAvailableLanguages: languageCode: "+languageCode);
            Log.d(TAG, "loadAvailableLanguages: languageTitle: "+languageTitle);
            ModelLanguage modelLanguage = new ModelLanguage(languageCode,languageTitle);
            languageArrayList.add(modelLanguage);


        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TranslateActivity.this, DashboardActivity.class));

        super.onBackPressed();
    }
}