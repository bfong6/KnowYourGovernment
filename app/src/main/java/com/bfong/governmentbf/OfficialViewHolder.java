package com.bfong.governmentbf;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    TextView office;
    TextView name;

    public OfficialViewHolder(@NonNull View view) {
        super(view);
        office = view.findViewById(R.id.office);
        name = view.findViewById(R.id.name);
    }
}
