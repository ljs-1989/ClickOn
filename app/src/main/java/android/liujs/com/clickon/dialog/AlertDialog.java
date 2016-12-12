package android.liujs.com.clickon.dialog;


import android.app.Dialog;
import android.content.Context;
import android.liujs.com.clickon.R;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liujs.library.utils.AppUtil;
import com.liujs.library.utils.DipAndPxUtil;

/**
 * Created by liujs on 2016/11/4.
 * 邮箱：725459481@qq.com
 */

public class AlertDialog extends Dialog {
    private Context mContext;
    private TextView textView;

    public AlertDialog(Context context) {
        this(context, R.style.Dialog);
        initDialog();
    }
    public AlertDialog(Context context,String alertText) {
        this(context, R.style.Dialog);
        initDialog();
        textView.setText(alertText);
    }
    public AlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    private void initDialog(){
       View contentView =  LayoutInflater.from(mContext).inflate(R.layout.alert_dialog_layout,null);
       textView = (TextView)contentView.findViewById(R.id.alert_msg);
        ViewGroup.LayoutParams layoutParams =  new ViewGroup.LayoutParams((int)(AppUtil.getScreenWidth(mContext)*0.7f), DipAndPxUtil.dipToPx(mContext,120));
        setContentView(contentView,layoutParams);
    }

    /**
     * 设置提示信息
     * @param msg
     */
    public void setAlertText(@NonNull String msg){
        textView.setText(msg);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AlertDialog.this.cancel();
        }
    };

    @Override
    public void show() {
        super.show();
        AppUtil.setDialogDimAmount(0.3f,getWindow());
        mHandler.sendEmptyMessageDelayed(1,2000);
    }
}
