package android.liujs.com.clickon;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.liujs.com.clickon.dialog.AlertDialog;
import android.liujs.com.clickon.dialog.GradeDialog;
import android.liujs.com.clickon.dialog.SetDialog;
import android.liujs.com.clickon.utils.GameUtil;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.liujs.library.data.PreferenceOperator;
import com.liujs.library.utils.AppUtil;
import com.liujs.library.utils.BitmapUtil;
import com.liujs.library.utils.TextUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements IGame{
    private RecyclerView mRecyclerView;
    private Button mControl,stopBut;
    private  static RecyclerViewAdapter recyclerViewAdapter;
    private TextView runTimeView ;
    private SeekBar seekBar;
    private CountDownTimer mCountDownTimer ,showHasRunDownTimer;

    /**
     * 每个级别所需时间
     * 120秒
     */
    private final int OneLevelMillionTime = 1000*60;
    /**
     * 游戏在当前级别已经运行的时间
     */
    private int HasRunMillionTime ;
    private final int GAME_PRESTART = 0X12,GAME_STOP = 0X13,GAME_PAUSE = 0X14,GAME_RUNNING = 0X15;
    private int GAME_STATE = GAME_PRESTART ;
    /**
     * 统计误点次数，大于1不能升级
     */
    private int UNUSE_CLICK_COUNT,CLICK_COUNT;
    private PreferenceOperator mOperator;
    private int MODE = 1;
    private float switch_speed = 1f;
    //切换的图片
    private Bitmap switchBitmap;
    private TextView speedTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOperator = new PreferenceOperator(this,getString(R.string.mode));
        mRecyclerView = (RecyclerView)this.findViewById(R.id.recycle_view_id);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,4));
        speedTextView = (TextView)this.findViewById(R.id.speed_tv);
        speedTextView.setText(String.format( "%s次/s",switch_speed+""));
        recyclerViewAdapter = new RecyclerViewAdapter(this,mOperator);
        mRecyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setItemOnClikcListener(itemOnClikcListener);
        GameUtil.SWITCH_NUM = mOperator.getInt(R.string.switch_number,1);

        seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        int maxLevel = mOperator.getInt(R.string.my_max_level,0);
        if(maxLevel>0){
            seekBar.setSecondaryProgress(maxLevel);
        }
        MODE = mOperator.getInt(R.string.mode,1);
        String imgCachePath = mOperator.getString(R.string.switch_img_path,null);
        if(!TextUtil.isEmpty(imgCachePath)){
            switchBitmap = BitmapUtil.readBitmap(imgCachePath);
        }
        runTimeView = (TextView)this.findViewById(R.id.show_time_tv);
        mControl = (Button)this.findViewById(R.id.start_pause_btn);
        stopBut = (Button)this.findViewById(R.id.stop_btn);
        mControl.setOnClickListener(clickListener);
        stopBut.setOnClickListener(clickListener);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        private boolean fromUser;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.fromUser = fromUser;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(fromUser){
                if(seekBar.getProgress()>=seekBar.getSecondaryProgress()){
                    seekBar.setProgress(seekBar.getSecondaryProgress());
                }
                    switch_speed = seekBar.getProgress()*0.5f+1;
                    speedTextView.setText(String.format( "%s次/s",switch_speed+""));
            }

        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.start_pause_btn:
                    if(GAME_STATE==GAME_PRESTART||GAME_STATE==GAME_STOP){//游戏开始
                          gameStart();
                    }else if(GAME_STATE== GAME_PAUSE){//游戏重新开始
                         restart();
                    }else if(GAME_STATE == GAME_RUNNING){//游戏切换到暂停
                        gamePause();
                    }
                    break;
                case R.id.stop_btn:
                    gameStop();
                    break;
                case R.id.btn_cancel:
                    if(mGradeDialog!=null)mGradeDialog.cancel();
                    reSetState();
                    break;
                case R.id.btn_confirm:
                    if(mGradeDialog!=null)mGradeDialog.cancel();
                    reSetState();
                   if(mGradeDialog.isRestart()==true) mControl.performClick();
                    break;
            }
        }
    };

    /**
     * 回复游戏初始状态
     */
    private void reSetState(){
        //游戏结束
        mControl.setText("开始");
        GAME_STATE = GAME_STOP;
        HasRunMillionTime = 0;
        UNUSE_CLICK_COUNT = 0;
        CLICK_COUNT = 0;
        if(mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }
        if(showHasRunDownTimer !=null) {
            showHasRunDownTimer.onFinish();
            showHasRunDownTimer.cancel();
        }
        for(int i=0;i < GameUtil.clickItemList.size();i++){
            View itemView = mRecyclerView.getChildAt(GameUtil.clickItemList.get(i));
            ImageView mImageView = (ImageView) itemView.findViewById(R.id.grid_item);
            mImageView.setImageResource(0);
        }
        GameUtil.reInitItem();
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private ItemOnClikcListener itemOnClikcListener = new ItemOnClikcListener() {
        @Override
        public void itemOnClick(View view, int position) {
                ++CLICK_COUNT ;
              //判断该item是否可切换
             if(GameUtil.clickItemList.contains(position) && GAME_STATE==GAME_RUNNING){
                 if(RecyclerViewAdapter.colorRsid==0){
                     ((ImageView)view).setImageResource(R.mipmap.ic_launcher);
                 }else{
                     ((ImageView)view).setImageResource(0);
                 }

                // recyclerViewAdapter.notifyItemChanged(position);
                 GameUtil.removeClickItem(position);
             }else if(GAME_STATE==GAME_RUNNING){
                  UNUSE_CLICK_COUNT += 1;
                 Toast.makeText(MainActivity.this,"警告：您已误点了"+UNUSE_CLICK_COUNT+"次",Toast.LENGTH_LONG).show();
             }

        }
    };
     private void showCountDownTimer(){
         final int lessTime = (OneLevelMillionTime - HasRunMillionTime)/1000;
         if(runTimeView.getVisibility()==View.INVISIBLE) runTimeView.setVisibility(View.VISIBLE);
         runTimeView.setText(lessTime+" s");
         showHasRunDownTimer = new CountDownTimer(OneLevelMillionTime - HasRunMillionTime,1000) {
             int lessMillionTime = lessTime;
             @Override
             public void onTick(long millisUntilFinished) {
                 runTimeView.setText(--lessMillionTime+" s");
             }
             @Override
             public void onFinish() {
                 runTimeView.setVisibility(View.INVISIBLE);
             }
         };
         showHasRunDownTimer.start();
     }


    private  GradeDialog mGradeDialog;
    private void gameRun(final int intervalTime){
        mCountDownTimer = new CountDownTimer(OneLevelMillionTime - HasRunMillionTime,intervalTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                for(int i = 0; i <  GameUtil.SWITCH_NUM; i++){
                    int clickItemPosition = GameUtil.getClickItem();
                    if(clickItemPosition != -1){
                        HasRunMillionTime += intervalTime;
                        View view =  mRecyclerView.getChildAt(clickItemPosition);
                        ImageView item = (ImageView) view.findViewById(R.id.grid_item);
                        if(switchBitmap!=null){
                            item.setImageBitmap(switchBitmap);
                        }else{
                            item.setImageResource(R.mipmap.switch_pic);
                        }
                        // recyclerViewAdapter.notifyItemChanged(clickItemPosition);
                    }else{
                        //玩家失败，游戏结束
                        if(mCountDownTimer!=null){
                            mCountDownTimer.cancel();
                        }
                        if(showHasRunDownTimer !=null) {
                            showHasRunDownTimer.onFinish();
                            showHasRunDownTimer.cancel();
                        }
                        AlertDialog alertDialog = new AlertDialog(MainActivity.this,"失败告终！");
                        alertDialog.show();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                reSetState();
                            }
                        });
                        break;
                    }

                }
            }

            @Override
            public void onFinish() {
                //统计有效点击率，判断是否进入下一级
                if(UNUSE_CLICK_COUNT == 0){
                    //顺利进入下一级
                    if(seekBar.getProgress() < 9){
                        seekBar.setProgress(seekBar.getProgress()+1);
                        if(seekBar.getProgress()>mOperator.getInt(R.string.my_max_level,0)){
                            seekBar.setSecondaryProgress(seekBar.getProgress());
                            mOperator.putInt(R.string.my_max_level,seekBar.getSecondaryProgress());
                        }
                        switch_speed = switch_speed+0.5f;
                        speedTextView.setText(String.format( "%s次/s",switch_speed+""));
                        HasRunMillionTime = 0;
                        UNUSE_CLICK_COUNT = 0;
                        final int intervalTime = (int)((1f/((0.5f*seekBar.getProgress())+1))*1000);
                        gameRun(intervalTime);
                        mCountDownTimer.start();
                        showHasRunDownTimer.cancel();
                        showCountDownTimer();
                        //将所在级别缓存起来,方便下次打开app时进行初始化
                        mOperator.putInt(R.string.current_level,seekBar.getProgress());
                    }else{//单指模式下已满级,弹出成绩单，询问是否进入双显模式.

                    }

                }else{//弹出成绩单，提示是否要从新开始游戏
                    showGradeDialog("重新开始");
                    mGradeDialog.setRestart(true);
                }
            }
        };
    }

    private void showGradeDialog(String buttonText){
        if(mGradeDialog==null){
            mGradeDialog = new GradeDialog(MainActivity.this);
            mGradeDialog.setCancleButtonClickListener(clickListener);
        }
        mGradeDialog.setPositiveButtonClickListener(clickListener,buttonText);
        float unSuccessRate = new BigDecimal(CLICK_COUNT-UNUSE_CLICK_COUNT).divide(new BigDecimal(CLICK_COUNT),3,BigDecimal.ROUND_DOWN).floatValue();
        mGradeDialog.setContent(String.format("所用时间：%ds;%n所处级别：%.1f次/秒;%n无效点击次数：%d次；%n点击成功率：%.1f%%,继续努力哦！",
                HasRunMillionTime/1000,switch_speed,UNUSE_CLICK_COUNT,unSuccessRate*100));

        mGradeDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private SetDialog setDialog;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings){
             setDialog = new SetDialog(this,new ReflashHandler());
            setDialog.show();
            StatService.onPageStart(this, "设置页");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
       if(keyCode==KeyEvent.KEYCODE_BACK){//调用双击退出函数
           AppUtil.exitBy2Click(MainActivity.this,"别再按了，再按可要退出了！");

       }
        return false;
    }

    @Override
    public void gameStart() {
        //每翻一个卡片用的时间间隔
        final int intervalTime = (int)((1f/((0.5f*seekBar.getProgress())+1))*1000);
        HasRunMillionTime = 0;
        UNUSE_CLICK_COUNT = 0;
        gameRun(intervalTime);
        showCountDownTimer();
        mCountDownTimer.start();
        mControl.setText("pause");
        GAME_STATE = GAME_RUNNING;
    }

    @Override
    public void restart() {
        GAME_STATE = GAME_RUNNING;
        mControl.setText("pause");
        mCountDownTimer.start();
        showCountDownTimer();
    }

    @Override
    public void gamePause() {
        mControl.setText("继续");
        if(mCountDownTimer!=null){
            mCountDownTimer.cancel();
            GAME_STATE = GAME_PAUSE;
        }
        if(showHasRunDownTimer !=null) showHasRunDownTimer.cancel();
    }

    @Override
    public void gameStop() {
        if(CLICK_COUNT>0){
            if(GAME_STATE==GAME_RUNNING) gamePause();
            showGradeDialog("确定");
            mGradeDialog.setRestart(false);
        }else{
            reSetState();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
  private static int REFLASH_BG = 1;
    private static class ReflashHandler extends Handler{
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if(msg.what==REFLASH_BG){
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode== ImagePicker.RESULT_CODE_ITEMS){
           ArrayList<ImageItem> imageItems = (ArrayList)data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if(imageItems!=null&&imageItems.size()>0){
                String imgPath  = imageItems.get(0).path;
                if(!TextUtil.isEmpty(imgPath)){
                   Bitmap bitmap =  BitmapUtil.readBitmap(imgPath);
                    if(bitmap!=null&&setDialog!=null){
                        switchBitmap = bitmap;
                        setDialog.showSelectImg(bitmap);
                        mOperator.putString(R.string.switch_img_path,imgPath);
                        for(int i=0;i < GameUtil.clickItemList.size();i++){
                            View itemView = mRecyclerView.getChildAt(GameUtil.clickItemList.get(i));
                            ImageView mImageView = (ImageView) itemView.findViewById(R.id.grid_item);
                            mImageView.setImageBitmap(switchBitmap);
                        }
                    }
                }
            }
        }
    }
}
