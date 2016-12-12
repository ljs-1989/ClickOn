package android.liujs.com.clickon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.liujs.com.clickon.R;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by liujs on 2016/11/23.
 * 邮箱：725459481@qq.com
 */

public class UpdateDialog extends Dialog {

    private Context mContext;
    private TextView mUpdateMsg;
    private Button updateButton;
    private ImageButton deleteButton;

    public UpdateDialog(Context context) {
        this(context, R.style.Dialog);
    }

    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        init();
    }
 private void init(){
     setContentView(LayoutInflater.from(mContext).inflate(R.layout.update_dialog,null));
     setCanceledOnTouchOutside(false);
     mUpdateMsg = (TextView)this.findViewById(R.id.update_msg);
     updateButton = (Button)this.findViewById(R.id.update_btn);
     deleteButton = (ImageButton)this.findViewById(R.id.btn_cancel);
 }

    /**
     * 版本更新信息
     * @param msg
     */
    public void setUpdateMsg(String msg){
        updateButton.setText(msg);
    }

    /**
     * 更新按钮点击事件
     * @param clickListener
     */
    public void setUpdateClickListener(View.OnClickListener clickListener){
        updateButton.setOnClickListener(clickListener);
    }

    /**
     * 窗口关闭事件
     */
    public void setDialogCancleButClickListener(View.OnClickListener clickListener){
        deleteButton.setOnClickListener(clickListener);
    }
}
