package android.liujs.com.clickon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.liujs.com.clickon.dialog.UpdateDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.baidu.mobstat.StatService;
import com.liujs.library.utils.LogUtil;

public class BaseActivity extends AppCompatActivity {

    private UpdateDialog mUpdateDialog;
    private NotificationManager mNotificationManager;
    private static final int NOTIFY_ID = 1;
    private Notification mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //百度更新
//        BDAutoUpdateSDK.cpUpdateCheck(this, new MyCPCheckUpdateCallback());
    }
    
    private class MyCPCheckUpdateCallback implements CPCheckUpdateCallback {

        @Override
        public void onCheckUpdateCallback(AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
            if (infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
                //直接安装
               BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), infoForInstall.getInstallPath());
            } else if (info != null) {
                //弹框提示是否下载新版本
                mUpdateDialog = new UpdateDialog(BaseActivity.this);
                MyClickListener clickListener = new MyClickListener(info);
                mUpdateDialog.setDialogCancleButClickListener(clickListener);
                mUpdateDialog.setUpdateClickListener(clickListener);
                String updateMsg = "V "+info.getAppVersionName() +" "+info.getAppSize()+"\n"+ info.describeContents();
                mUpdateDialog.setUpdateMsg(updateMsg);
                mUpdateDialog.show();
            } else {
              LogUtil.d("update", "no update.");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);//百度移动统计页面使用时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private class MyClickListener implements View.OnClickListener{
        private AppUpdateInfo info;

        public MyClickListener(AppUpdateInfo info){
            this.info = info;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.update_btn:
                    BDAutoUpdateSDK.cpUpdateDownload(BaseActivity.this, info, new UpdateDownloadCallback());
                    break;
                case R.id.btn_cancel:
                  if(mUpdateDialog!=null)mUpdateDialog.cancel();
                    break;
            }
        }
    };

    private class UpdateDownloadCallback implements CPUpdateDownloadCallback {

        @Override
        public void onDownloadComplete(String apkPath) {
            BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), apkPath);
            mNotificationManager.cancel(NOTIFY_ID);
        }

        @Override
        public void onStart() {
            //下载进度通知栏开始显示
            buildNotification();
        }

        @Override
        public void onPercent(int percent, long rcvLen, long fileSize) {
            //更新下载进度
            updateNotification(percent);
        }

        @Override
        public void onFail(Throwable error, String content) {
            Toast.makeText(BaseActivity.this,"下载失败！",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStop() {

        }

    }

    /**
     * 更新通知栏的进度
     * @param progress
     */
    private void updateNotification(int progress) {
        mNotification.contentView.setTextViewText(R.id.tv_progress, progress + "%");
        mNotification.contentView.setProgressBar(R.id.progressbar, 100, progress, false);
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    /**
     * 实例化通知栏
     */
    private void buildNotification() {
        RemoteViews contentView = new RemoteViews(BuildConfig.APPLICATION_ID, R.layout.notification_download);
        contentView.setTextViewText(R.id.name, "正在下载...");

        mNotification = new NotificationCompat.Builder(this)
                .setTicker("开始下载")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setContent(contentView)
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        // LED灯闪烁，如
        mNotification.defaults |= Notification.DEFAULT_LIGHTS;
        mNotification.ledARGB = 0xff00ff00;
        mNotification.ledOnMS = 300;
        mNotification.ledOffMS = 1000;
        mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
    }
}
