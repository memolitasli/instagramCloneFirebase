package com.example.instagramclonefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*Hiçbir şey yapmadan önce firebase bağlantisi oluşturmam lazım
* İlk başta yapmam gereken firebase projesini oluşturduktan sonra hangi platforma oluşturacağımı belirtmek
* androidi seçiyorum ben firebase sitesinde gösterilen işlemleri yaptıktan sonra firebase sdk eklemeye geliyor sıra
*
* */
public class girisEkraniActivity extends AppCompatActivity {
private FirebaseAuth firebaseAuth; // giriş ve kayıt yaptırmak için
EditText et_mail,et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.girisekrani);
        firebaseAuth = FirebaseAuth.getInstance();
        et_mail = (EditText)findViewById(R.id.et_Mail);
        et_password = (EditText)findViewById(R.id.et_password);

        //eğer daha önce giriş yapmış bir kullanıcı var mı diye kontrol ediyorum
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            Intent i = new Intent(girisEkraniActivity.this,FeedActivity.class);
            startActivity(i);
            finish();
        }

    }

    public void signinClick (View view){
        String mail = et_mail.getText().toString();
        String pass = et_password.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(mail,pass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(girisEkraniActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(girisEkraniActivity.this,"Giriş Başarılı",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(girisEkraniActivity.this,FeedActivity.class);
                startActivity(i);
                finish(); // finish koymamın sebebi kullanıcı giriş yaptıktan sonra hesabını kapatmak için kullanıcı çıkışı yapsın geriye basarak o ekrana geri dönmesin
                
            }
        });
    }
    public void signupClick (View view){
        // kullanıcı kayıt etmek için
        String mail = et_mail.getText().toString();
        String pass = et_password.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(mail,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
            // işlem başarılı ise
                Toast.makeText(girisEkraniActivity.this,"Kullanıcı Oluşturuldu",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(girisEkraniActivity.this,FeedActivity.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // işlem başarısız ise
                // işlem başarısız olması durumunda dönecek olan exception firebase den geliyor bu sayede kendi mesajlarımı yazmak yerine firebasenin yazılarını kullanabilirim
                Toast.makeText(girisEkraniActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}