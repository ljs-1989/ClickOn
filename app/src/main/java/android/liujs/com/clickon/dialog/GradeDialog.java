package android.liujs.com.clickon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.liujs.com.clickon.R;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by liujs on 2016/11/3.
 * 邮箱：725459481@qq.com
 */

public class GradeDialog extends Dialog {
    private Context mContext;
    private Button mButton;
    private ImageButton cancelBut;
    private TextView mContent;
    private boolean isRestart = false;

    public GradeDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public GradeDialog(Context context) {
        this(context, R.style.Dialog);
        init();
    }

private void init(){
    View contentView = LayoutInflater.from(mContext).inflate(R.layout.grade_dialog_layout,null);
    setContentView(contentView);
    mButton = (Button)this.findViewById(R.id.btn_confirm);
    cancelBut = (ImageButton)this.findViewById(R.id.btn_cancel);
    mContent = (TextView)this.findViewById(R.id.text_msg);
    setCanceledOnTouchOutside(false);
}

    public boolean isRestart() {
        return isRestart;
    }

    public void setRestart(boolean restart) {
        isRestart = restart;
    }

    public void setPositiveButtonClickListener(View.OnClickListener clickListener, String buttonText){
        mButton.setOnClickListener(clickListener);
        mButton.setText(buttonText);
    }

public void setCancleButtonClickListener(View.OnClickListener clickListener){
    cancelBut.setOnClickListener(clickListener);
}


    public void setContent(String msg){
        mContent.setText(msg);
    }
}
