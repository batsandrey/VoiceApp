package com.batsandrey.voiceapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {


    //переменная для проверки возможности
    //распознавания голоса в телефоне
    private static final int VR_REQUEST = 999;

    //ListView для отображения распознанных слов
    private ListView wordList;
    private Button speechBtn;
    private Button flashOffButton;

    //Log для вывода вспомогательной информации
    private final String LOG_TAG = "SpeechRepeatActivity";
    //***здесь можно использовать собственный тег***

    //переменные для работы TTS

    //переменная для проверки данных для TTS
    private int MY_DATA_CHECK_CODE = 0;

    //Text To Speech интерфейс
    private TextToSpeech repeatTTS;

    private static final String TAG = MainActivity.class.getSimpleName();
    private Camera mCamera;
    private Camera.Parameters parameters;
    private CameraManager camManager;
    private Context context;

    String cameraId = null; // Usually front camera is at 0 position.

    private boolean hasFlash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
        voiceIsSupport();

//        getCamera();

//        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        turnFlashlightOff();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
//        turnFlashlightOff();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (hasFlash)
//            turnFlashlightOn();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //проверяем результат распознавания речи
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
            //Добавляем распознанные слова в список результатов
            ArrayList<String> suggestedWords =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //Передаем список возможных слов через ArrayAdapter компоненту ListView

            checkValidWords(suggestedWords);

            wordList.setAdapter(new ArrayAdapter<String>(this, R.layout.word, suggestedWords));

        }


//tss код здесь

//вызываем метод родительского класса
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void getCamera() {
//
//        if (mCamera == null) {
//            try {
//                mCamera = Camera.open();
//                parameters = mCamera.getParameters();
//            }catch (Exception e) {
//
//            }
//        }
//
//    }

    private void checkValidWords(ArrayList<String> suggestedWords) {
        for (String word : suggestedWords) {
            if (word.contains("andrey")) {
//                turnFlashlightOn();
//                turnScreenLightness();

                Toast.makeText(getBaseContext(), "Right word",
                        Toast.LENGTH_SHORT).show();
            }
//            else {
//                Toast.makeText(getBaseContext(), "Exist word",
//                        Toast.LENGTH_SHORT).show();
//            }
        }
    }

//    private void turnFlashlightOn() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            try {
//                camManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
//                if (camManager != null) {
//                    cameraId = camManager.getCameraIdList()[0];
//                    camManager.setTorchMode(cameraId, true);
//                }
//            } catch (CameraAccessException e) {
//                Log.e(TAG, e.toString());
//            }
//        } else {
//            mCamera = Camera.open();
//            parameters = mCamera.getParameters();
//            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//            mCamera.setParameters(parameters);
//            mCamera.startPreview();
//        }
//    }
//
//    public void turnFlashlightOff() {
//        try {
//            if (getPackageManager().hasSystemFeature(
//                    PackageManager.FEATURE_CAMERA_FLASH)) {
//                mCamera.stopPreview();
//                mCamera.release();
//                mCamera = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getBaseContext(), "Exception flashLightOff",
//                    Toast.LENGTH_SHORT).show();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            try {
//                camManager.setTorchMode(cameraId, false);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//                Toast.makeText(getBaseContext(), "Exception flashLightOff",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }

    private void initializeViews() {
        speechBtn = findViewById(R.id.speech_btn);
        flashOffButton = findViewById(R.id.flash_off_btn);
        flashOffButton.setOnClickListener(this);
        wordList = findViewById(R.id.word_list);
    }

    private void voiceIsSupport() {
        //проверяем, поддерживается ли распознование речи
        PackageManager packManager = getPackageManager();
        List<ResolveInfo> intActivities = packManager.queryIntentActivities(new
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0) {
// распознавание поддерживается, будем отслеживать событие щелчка по кнопке
            speechBtn.setOnClickListener(this);
        } else {
// распознавание не работает. Заблокируем
// кнопку и выведем соответствующее
// предупреждение.
            speechBtn.setEnabled(false);
            Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void listenToSpeech() {

//запускаем интент, распознающий речь и передаем ему требуемые данные
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//указываем пакет
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getClass().getPackage().getName());
//В процессе распознования выводим сообщение
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
//устанавливаем модель речи
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//указываем число результатов, которые могут быть получены
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

//начинаем прослушивание
        startActivityForResult(listenIntent, VR_REQUEST);
    }

    @Override
    public void onInit(int status) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speech_btn:
                listenToSpeech();
                Toast.makeText(getBaseContext(), "Speech on",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.flash_off_btn:
//                turnFlashlightOff();
                Intent intent = new Intent(this, FlashlightActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(getBaseContext(), "On light",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
