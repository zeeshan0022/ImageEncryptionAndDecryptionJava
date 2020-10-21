package com.joinhub.imageencryption;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Cipher cipher;
    Button enrpt,decrpt,select;
    final String ALGORITHM = "blowfish";
    String keyString = "DesireSecretKey";
    Uri filePath;
    String filename,filePath1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
            }
        });
        decrpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File in= new File(filePath1+".enc");
                File encryptedFile = new
                        File(filePath1);
                try {
                    decrpty(Cipher.DECRYPT_MODE,in,encryptedFile);
                    in.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        enrpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                  //  encrypt(Environment.getExternalStorageDirectory()+"/ss.jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void encrypt(String file,String filename) throws Exception {

        File extStore = Environment.getExternalStorageDirectory();
        File inputFile = new File(file);
        File encryptedFile = new
                File(file+".enc");
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, encryptedFile);
       inputFile.delete();
    }

    public void decrpty(int cipherMode, File inputFile,
                        File outputFile) throws Exception {
        Key secretKey = new
                SecretKeySpec(keyString.getBytes(),ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new
                FileOutputStream(outputFile);
        outputStream.write(outputBytes);
        inputStream.close();
        outputStream.close();
    }
    private  void doCrypto(int cipherMode, File inputFile,
                           File outputFile) throws Exception {

        Key secretKey = new
                SecretKeySpec(keyString.getBytes(),ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(cipherMode, secretKey);

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new
                FileOutputStream(outputFile);
        outputStream.write(outputBytes);
        inputStream.close();
        outputStream.close();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            filePath= data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
                System.out.println("Dataa"+filePath);
                System.out.println("FilePath"+getRealPathFromURI(this,filePath));
                filename=getFileName(filePath);
                filePath1= getRealPathFromURI(this,filePath);
                encrypt(filePath1,filename);
            }

            catch (IOException e) {
                // Log the exception
                Log.e("En ",e.getMessage());
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("Ene ",e.getMessage());
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
        }else{

        }


    }

    private void init() {
        select= findViewById(R.id.imageButton);
        enrpt= findViewById(R.id.enrpttt);
        decrpt= findViewById(R.id.dcrpt);
        imageView= findViewById(R.id.imageView);

    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null
                , MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
    public String getFileName( Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            } finally {
                cursor.close();
            }
        }

        if (result == null) {
            result = uri.getPath();
            /*
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        */
        }
        return result;
    }

}