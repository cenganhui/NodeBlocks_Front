package geishaproject.demonote.module.audio;

import android.Manifest;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import geishaproject.demonote.activity.NewNote;
import geishaproject.demonote.module.audio.manager.AudioRecordButton;
import geishaproject.demonote.module.permission.PermissionHelper;
import geishaproject.demonote.ui.Components;
import geishaproject.demonote.utils.PublicContext;

public class Audio {

    /**
     * 初始化录音模块
     */
    public static void initAudio() {
        Components.player = new MediaPlayer();   //实例化录音控件
    }
    /**
     * 播放录音
     */
    public static void PlayR () {
        if(!Components.data.getAudioPath().equals("")) {
            if (Components.player != null) {
                Components.player.reset();
                try {
                    Components.data.cutAudioPathArr();
                    Toast.makeText(PublicContext.getContext(), Components.data.getAudioPath(), Toast.LENGTH_SHORT).show();
                    int i = Components.data.getAudioPathArr().size();
                    Log.d("***size","*/*/*/*size"+i);
                    Components.player.setDataSource(Components.data.getAudioPathArr().get(i-1)); //获取录音文件
                    Components.player.prepare();
                    Components.player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        else{
            Toast.makeText(PublicContext.getContext(), "还未曾录音", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 添加录音点击事件监听
     */
    public  static  void addAudioListener() {
        Components.PlayRecord.setOnClickListener(new View.OnClickListener() {        //播放录音点击事件
            @Override
            public void onClick(View v) {
                Audio.PlayR();
            }
        });

        Components.mEmTvBtn.setHasRecordPromission(false);
//        //授权处理
//        Components.mHelper = new PermissionHelper(this);

        Components.mHelper.requestPermissions("请授予[录音]、[读写]权限，否则无法录音",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {
                        Components.mEmTvBtn.setHasRecordPromission(true);

                        Components.mEmTvBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
                            @Override
                            public void onFinished(float seconds, String filePath) {
                                Record recordModel = new Record();
                                recordModel.setSecond((int) seconds <= 0 ? 1 : (int) seconds);
                                recordModel.setPath(filePath);
                                recordModel.setPlayed(false);
                                Components.mRecords = recordModel;
                                String newAudioPath =Components.data.getAudioPath()+Components.mRecords.getPath()+"?";
                                //拼接的路径重新存入data中
                                Components.data.setAudioPath(newAudioPath);
                                Components.data.cutAudioPathArr();
                                Toast.makeText(PublicContext.getContext(), newAudioPath, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(NewNote.this, "录音保存成功！时长："+mRecords.getSecond()+"s", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void doAfterDenied(String... permission) {
                        Components.mEmTvBtn.setHasRecordPromission(false);
                        Toast.makeText(PublicContext.getContext(), "请授权,否则无法录音", Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

}