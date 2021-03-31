package com.bfong.governmentbf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {

    private List<Official> officialList;
    private MainActivity mainAct;

    public OfficialAdapter(List<Official> officialList, MainActivity mainActivity) {
        this.officialList = officialList;
        this.mainAct = mainActivity;
    }

    @NonNull
    @Override
    public OfficialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);

        holder.office.setText(official.getOffice());
        holder.name.setText(String.format("%s (%s)", official.getName(), official.getParty()));
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
