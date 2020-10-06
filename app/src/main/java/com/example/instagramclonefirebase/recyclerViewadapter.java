package com.example.instagramclonefirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class recyclerViewadapter extends RecyclerView.Adapter<recyclerViewadapter.PostHolder> {
private ArrayList<String> MailList;
private ArrayList<String> CommentList;
private ArrayList<String> DownloadURLList;

    public recyclerViewadapter(ArrayList<String> mailList, ArrayList<String> commentList, ArrayList<String> downloadURLList) {
        MailList = mailList;
        CommentList = commentList;
        DownloadURLList = downloadURLList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewholder oluşturulunca ne yapacağım
        //döndureceğim postholder benim oluşturduğum xml ile bağlama işlemini yapacağım
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recyclar_row,parent,false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        // viewholdera bağlandığımda ne yapacağım
        //yani bağlandıktan sonra imageview içerisinde tv ler içerisinde neler olacak onları yazıyorum
        holder.tv_mail.setText(MailList.get(position));
        holder.tv_comment.setText(CommentList.get(position));

        // google da picasso gthub android aratarak gelen kütüphaneyi kullanacağım
        // implementation işlemini yaptıktan sonra
        Picasso.get().load(DownloadURLList.get(position)).into(holder.imageView);


    }

    @Override
    //RecyclerView da kaç eleman olacak onun sayısını istiyor benden
    public int getItemCount() {
        return MailList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        // oluşturduğum recyclar_row adlı xml dosyasının içerisindeki viewları burada tanımlayacağım
        ImageView imageView;
        TextView tv_mail;
        TextView tv_comment;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyclerView_row_imageView);
            tv_comment  = itemView.findViewById(R.id.recyclerView_row_comment_text);
            tv_mail = itemView.findViewById(R.id.recyclerView_row_mail_text);

        }
    }

}
