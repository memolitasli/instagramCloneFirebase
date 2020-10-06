package com.example.instagramclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

public class uploadActivity extends AppCompatActivity {
Button btn_upload;
Bitmap selectedimage;
ImageView img;
EditText et_yorum;
Uri imageuri;
private FirebaseStorage firebaseStorage;
private StorageReference storageReference;
private FirebaseFirestore firebaseFirestore;
private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        btn_upload = (Button)findViewById(R.id.btn_upload);
        img = (ImageView)findViewById(R.id.imageView);
        et_yorum = (EditText)findViewById(R.id.et_yorum);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    public void upload(View view){
        // şimdi 2 aşamalı bir işlem yapacağım
        //ilk önce görselin kendisini upload edeceğim bunu için sqlite gibi veri tabanını değil firebase içerisindeki storage kullanacağım
        // görsel video gibi verileri kaydetmek için storage kullanmak zorundayım  ve oraya upload ettikten sonra bir link alabiliyrum nereye kaydettim diye

        //storereferance kullanarak ben bir görsel video vb nesneyi kaydedebilirim ve nereye kaydedeceğimi söyleyebilirim

        if(imageuri != null){
            //eğer klasör var ise o klasör içerisine yolladığım nesneyi kaydediyor eğer klasmr yok ise o klasörü oluşturuyor
            // ilk koyduğum child klasorun ismi ikinci child görsel ismi
            //klasor içerine bir klasor daha açmak istersem eğer storagereferance.child(image).child(image2).child().... yapmam lazım
            //ancak her görsellerin ismi aynı olursa eğer bu sefer en son üklediğim görseli diğerinin üzerine yazacak bunun olmasını istemediğim için
            // universal unique id adlı bir yapıyı kullanacağım

            UUID uuid = UUID.randomUUID();
            final String imageName = "images/" + uuid + ".jpg";
            storageReference.child(imageName).putFile(imageuri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(uploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(uploadActivity.this,"Görsel Yüklek başarı ile gerçekleti",Toast.LENGTH_SHORT).show();
                    // görseli başarılı bir şekilde storage ye yükledikten sonra storage içerisindeki görselde bir adet downloadlink oluşuyor bu linki ben
                    //alıp veri tabanıma kaydedeceğim daha sonra veri tabanından görseli çağırırken bu şekilde çağıracağım artık storage ile işim bitti

                    //artık download url yi almam lazım
                    StorageReference newreferance = FirebaseStorage.getInstance().getReference(imageName); // yüklediğim görselin nere kaydedildiğini bul
                    newreferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadurl = uri.toString(); // artık indirilebilir url linkini aldım

                            // şimdi sıra bu verileri veri firestore veri tabanına eklemeye geldi
                            FirebaseUser user = firebaseAuth.getCurrentUser(); // hangi kullanıcının aktif olduğunu buldum
                            String usermail = user.getEmail();
                            String yorum = et_yorum.getText().toString();

                                // firebasestore a ekleyeceğim verileri bir hashmap olarak ekleyeceğim
                                HashMap<String, Object> postdata = new HashMap<>();
                                postdata.put("userMail",usermail);
                                postdata.put("downloadURl",downloadurl);
                                postdata.put("comment",yorum);
                                postdata.put("date", FieldValue.serverTimestamp());


                                firebaseFirestore.collection("Posts").add(postdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        // başarılı olduysa feed activity e geri yollayacağım
                                        Intent i = new Intent(uploadActivity.this,FeedActivity.class);
                                        startActivity(i);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(uploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });


                        }
                    });

                }
            });
        }

    }
    public void selectImage(View view){
            //API 23 ve öncesi için izin var mı yokmu kontrolü yapıyorum eğer yok ise
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        //izin verildi veya daha önceden verilmiş ise
        else{
            Intent intenttoGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intenttoGallery,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == 1 ){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intenttoGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intenttoGallery,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //izin alındı resim seçildi ne yapılacak

         /*izin alındıktan ve görsel seçildikten sonra request code olarak 2 döndürmüştük onu kontrol ediyorum
         * işlem düzgün bir şekilde sonuçlandı mı onu kontrol ediyorum ve seçilen görsel null değil ise diye kontrol ediyorum*/
        if(requestCode == 2  && resultCode == RESULT_OK && data != null){
            // gelen datayı urı a çevirmem gerekiyor
           imageuri =  data.getData();

            try {
                //eğer sdk versiyonu 28 den yukarıda ise
               if(Build.VERSION.SDK_INT >= 28){
                   ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageuri);
                   selectedimage = ImageDecoder.decodeBitmap(source);
                   img.setImageBitmap(selectedimage);
               }
               // 28den önce ise
               else{
                   selectedimage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageuri);
                   img.setImageBitmap(selectedimage);
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}