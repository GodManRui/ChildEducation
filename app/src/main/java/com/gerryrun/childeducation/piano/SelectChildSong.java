package com.gerryrun.childeducation.piano;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gerryrun.childeducation.piano.bean.SelectSong;
import com.gerryrun.childeducation.piano.bean.SelectSong.DataBean;
import com.gerryrun.childeducation.piano.util.NetUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gerryrun.childeducation.piano.bean.Constont.SELECT_CHILD_SONG;

/**
 * 选儿哥
 */
public class SelectChildSong extends BaseActivity {

    private ProgressDialog progressDialog;
    private SelectSong mDataSelectSong;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_child_song);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("获取歌单...");
        progressDialog.setCancelable(false);
        recyclerView = findViewById(R.id.song_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NetUtil.getQuestion(SELECT_CHILD_SONG, new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    runOnUiThread(() -> Toast.makeText(SelectChildSong.this,
                            "服务器响应失败: " + response.code(), Toast.LENGTH_LONG).show());
                    return;
                }
                Gson gson = new Gson();
                String responseStr = response.body().string();
                Log.w("JerryZhu", "onResponse: " + responseStr);
                mDataSelectSong = gson.fromJson(responseStr, SelectSong.class);
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    initData();
                });
            }
        });

        findViewById(R.id.im_go_back).setOnClickListener(v -> {
            finish();
        });
    }

    private void initData() {
        if (mDataSelectSong == null || mDataSelectSong.getData() == null
                || mDataSelectSong.getData().size() <= 0) {
            Toast.makeText(this, "服务器没有数据", Toast.LENGTH_SHORT).show();
            finish();
        }
        int space = 3;
        int space1 = recyclerView.getHeight() / mDataSelectSong.getData().size();
//        recyclerView.addItemDecoration(new SpacesItemDecoration(100));
        MyAdapter myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
    }

    static class VY extends RecyclerView.ViewHolder {

        final TextView songName;

        VY(@NonNull View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.tv_item_song_name);
//            songName = (View) itemView.findViewById(R.id.tv_item_song_name);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<VY> {

        private final List<DataBean> data;

        MyAdapter() {
            data = mDataSelectSong.getData();
        }

        @NonNull
        @Override
        public VY onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_learn_song, viewGroup, false);
            return new VY(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VY holder, int i) {
            DataBean dataBean = data.get(i);
            holder.songName.setText(dataBean.getName());
            holder.itemView.setOnClickListener(v -> {
                //item 点击事件
                Intent intent = new Intent(SelectChildSong.this, StartLearnSong2.class);
                intent.putExtra("data", dataBean);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
