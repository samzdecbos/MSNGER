package com.example.messangerapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderUser extends RecyclerView.ViewHolder {
    TextView name;
    TextView mail;
    public ViewHolderUser(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        mail = itemView.findViewById(R.id.mail);
    }
}
