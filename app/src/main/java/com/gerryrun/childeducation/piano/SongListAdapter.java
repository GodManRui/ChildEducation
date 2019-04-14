package com.gerryrun.childeducation.piano;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gerryrun.childeducation.piano.SongListAdapter.VY;
import com.gerryrun.childeducation.piano.bean.SongList;
import com.gerryrun.childeducation.piano.bean.SongList.DataBean;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<VY> {
    private final List<SongList.DataBean> data;

    public SongListAdapter(List<SongList.DataBean> data) {
        this.data = data;
    }


    static class VY extends RecyclerView.ViewHolder {

        final TextView songName;

        VY(@NonNull View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.tv_list_song_name);
//            songName = (View) itemView.findViewById(R.id.tv_item_song_name);
        }
    }

    @NonNull
    @Override
    public VY onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_list, viewGroup, false);
        return new VY(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VY holder, int i) {
        DataBean dataBean = data.get(i);
        holder.songName.setText(dataBean.getName());
        holder.itemView.setOnClickListener(v -> {
            //item 点击事件

        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
