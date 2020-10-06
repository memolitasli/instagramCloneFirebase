package com.example.instagramclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
/*eklenen postlrı listelemek iin recyclerView kullanacağım bunun nedeni ListViewden daha verimli çalışması*/
public class FeedActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<String>userEmailFromFirebse;
    ArrayList<String>downloadURLformFirebase;
    ArrayList<String>userCommentFromFirebase;
    recyclerViewadapter adapter;
// şimdi bir menü eklemem lazım
    // res klasörüne gidiyorum ve menu dosyamı yapıyorum

    // oncreateoptionsmenu bir menu bağlamak için ve onoptionsitemselected ise seçiilen menu elamanını beirlemek için kullanılan metodlar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.instaoptionsmenu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post) {
            Intent i = new Intent(FeedActivity.this, uploadActivity.class);
            startActivity(i);
            finish();
        }
        if (item.getItemId() == R.id.signout) {
            // öncelikle firebase den signout yapabilceğimden emin olmam gerekiyor
            firebaseAuth.signOut();

            Intent i = new Intent(FeedActivity.this,girisEkraniActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getDataFromFireStrore(){
        //
        CollectionReference collectionReference = firebaseFirestore.collection("Posts");
        //bütün veriler bu snapshot üzerinden çekilecek
        // normalde  collectionReference.addSnapshotListener
        //ancak ben filtreleme yapacağım yüklenme tarihleri en son olan en üstte çıkmasını istiyoum , eğer ben sadece isim mehmet olanları almak istersem where... kullanacağım
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                //eğer bir hata geldiyse
                if(error != null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                else{
                    //velue değeri bana bir dizi veriyor value.getDocuments dersem bana snapshotları dizi olarak veriyor
                    if(value != null){
                        for(DocumentSnapshot snapshot : value.getDocuments()){
                            //snapshot bana sisteme yüklerken kaydettiğim hashmapi geri veriyor
                          Map<String, Object> data =   snapshot.getData();
                          // artık kaydettiğim dataya ulaştım
                            String comment = (String)data.get("comment");
                            String usermail = (String)data.get("userMail");
                            String downloadUrl = (String)data.get("downloadURL");

                            //firebaseden çektiğim verileri arraylistlere aktardım
                            userCommentFromFirebase.add(comment);
                            userEmailFromFirebse.add(usermail);
                            downloadURLformFirebase.add(downloadUrl);

                            //recyclerView ın adaptörünü program çalışırken bir yükleme gerçekleşir ise uyarıyorum tekrar kendini düzenlesin diye
                            adapter.notifyDataSetChanged();

                        }
                    }
                }



            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        userCommentFromFirebase =new ArrayList<>();
        downloadURLformFirebase = new ArrayList<>();
        userEmailFromFirebse = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getDataFromFireStrore();


        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new recyclerViewadapter(userEmailFromFirebse,userCommentFromFirebase,downloadURLformFirebase);

        recyclerView.setAdapter(adapter);
    }


}