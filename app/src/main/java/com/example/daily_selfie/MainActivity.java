package com.example.daily_selfie;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "1";
    private ImageButton cameraBtn;
    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final int REQUEST_ID_VIDEO_CAPTURE = 101;
    private ImageView imageView;
    private ListView listView;
    private ArrayList<Image> arr = new ArrayList<Image>();
    private ArrayList<String> str = new ArrayList<String>();
    private String file;
    private Bitmap bp;
    private String currentDateandTime;
    public String state;
    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int REQUEST_PERMISSION = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        stopService(new Intent(MainActivity.this,MyService.class));
        listView = (ListView) findViewById(R.id.listView);
        cameraBtn = (ImageButton) findViewById(R.id.cameraButton);
        String root = Environment.getExternalStorageDirectory().toString();//get external storage

        File myDir = new File(root +"/files/PICTURES");

        myDir.mkdirs();

        if(readFromFile("nameImg.txt")!=""){
            String nameimg[] = readFromFile("nameImg.txt").trim().split(", ");

            for(String item : nameimg){
                Bitmap bitmap = null;
                str.add(item);

                    state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)){
                        file = "/sdcard/Android/data/com.example.daily_selfie/files/PICTURES/" + item + ".jpg";
                        bitmap = readFile(file);
                    }
                if(bitmap!=null){
                    arr.add(new Image(item,bitmap));
                }
            }
        };

        CustomAdapter adapter = new CustomAdapter(this,arr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Image img = arr.get(position);
                Bitmap bmp = img.getImg();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Intent intImg = new Intent(MainActivity.this,ImageActivity.class);
                intImg.putExtra("image",byteArray);
                startActivity(intImg);
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                captureImage();
            }

        });
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                bp = (Bitmap) data.getExtras().get("data");
                currentDateandTime =(String) new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                str.add(currentDateandTime);
                String tmp = str.toString();
                tmp = tmp.replace("["," ");
                tmp = tmp.replace("]"," ");
                tmp.trim();

                saveFile(bp,currentDateandTime);
                writeToFile(tmp,"nameImg.txt");
                arr.add(new Image(currentDateandTime,bp));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }}
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        CustomAdapter adapter = new CustomAdapter(this,arr);
        listView.setAdapter(adapter);

    }
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }


    private void saveFile(Bitmap finalBitmap,String imgName){
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        String fname = imgName +".jpg";
        File fileImg = new File (path, fname);
        if (fileImg.exists()){
            fileImg.delete();
        }
        else {
            try {
                FileOutputStream out = new FileOutputStream(fileImg);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap readFile(String file){
        Bitmap bit = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            bit = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bit;
    }
    private void writeToFile(String data,String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile(String name) {

        String ret = "";

        try {
            InputStream inputStream = this.openFileInput(name);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}