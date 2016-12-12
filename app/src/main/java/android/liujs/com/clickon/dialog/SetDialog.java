package android.liujs.com.clickon.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.liujs.com.clickon.MainActivity;
import android.liujs.com.clickon.R;
import android.liujs.com.clickon.RecyclerViewAdapter;
import android.liujs.com.clickon.utils.GameUtil;
import android.liujs.com.clickon.utils.GlideImageLoader;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.baidu.appx.BDBannerAd;
import com.baidu.appx.BaiduAppX;
import com.baidu.mobstat.StatService;
import com.liujs.library.data.PreferenceOperator;
import com.liujs.library.utils.AppUtil;
import com.liujs.library.utils.BitmapUtil;
import com.liujs.library.utils.TextUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.ui.ImageGridActivity;

/**
 * Created by liujs on 2016/11/7.
 * 邮箱：725459481@qq.com
 */

public class SetDialog extends Dialog {
    private Context mContext;
    private ImageView switchImg;
    private RadioGroup mRadioGroup1,mRadioGroupNumber;
    private PreferenceOperator preferenceOperator;
    private static int REFLASH_BG = 1;
    private Handler mHandler;

    public SetDialog(Context context, Handler handler) {

        super(context, R.style.Dialog);
        this.mHandler = handler;
        this.mContext = context;
        init();
    }

    public SetDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        init();
    }

    private void init(){


        View view =  LayoutInflater.from(mContext).inflate(R.layout.set_dialog_layout,null);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.btn_cancel);
        imageButton.setOnClickListener(clickListener);
        mRadioGroup1 = ((RadioGroup)view.findViewById(R.id.switch_color_group1));
        mRadioGroup1.setOnCheckedChangeListener(checkedChangeListener);
        mRadioGroupNumber = (RadioGroup)view.findViewById(R.id.switch_number_group) ;
        mRadioGroupNumber.setOnCheckedChangeListener(checkedChangeListener);

        view.findViewById(R.id.animal_btn).setOnClickListener(clickListener);
        view.findViewById(R.id.girl_btn).setOnClickListener(clickListener);
        view.findViewById(R.id.other_img_btn).setOnClickListener(clickListener);

        switchImg = (ImageView) view.findViewById(R.id.switch_img);
        preferenceOperator = new  PreferenceOperator(mContext,mContext.getString(R.string.mode));
       int itemColorCaChe =  preferenceOperator.getInt(R.string.item_color,0);
        if(itemColorCaChe!= 0){
            checkSelectColor(itemColorCaChe);
        }
        int switchNum = preferenceOperator.getInt(R.string.switch_number,0);
        if(switchNum!=0){
            ((RadioButton) mRadioGroupNumber.getChildAt(switchNum-1)).setChecked(true);
        }
        String imgPath =  preferenceOperator.getString(R.string.switch_img_path,null);
        if(!TextUtil.isEmpty(imgPath)) {
            Bitmap bitmap = BitmapUtil.readBitmap(imgPath);
            if (bitmap != null) {
                switchImg.setImageBitmap(bitmap);
            }
        }
        //实例化百度广告展示
        initAdver(view);
        ViewGroup.LayoutParams layoutParams =  new ViewGroup.LayoutParams((AppUtil.getScreenWidth(mContext)), ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view,layoutParams);
        setCanceledOnTouchOutside(false);
    }
private void initAdver(View view){
    BaiduAppX.version();//百度广告获取版本号
    RelativeLayout bannerContainerLayout = (RelativeLayout) view.findViewById(R.id.advertisement_banner);
    //创建并展示横幅广告
    BDBannerAd bannerview = new BDBannerAd((MainActivity)mContext, GameUtil.BaiDu_App_Key, GameUtil.SDK_BANNER_AD_ID);
    bannerview.setAdSize(BDBannerAd.SIZE_FLEXIBLE); //选择模式
 /*   bannerview.setAdListener(new BDBannerAd.BannerAdListener(){
        @Override
        public void onAdvertisementDataDidLoadSuccess() {

        }

        @Override
        public void onAdvertisementDataDidLoadFailure() {

        }

        @Override
        public void onAdvertisementViewDidShow() {

        }

        @Override
        public void onAdvertisementViewDidClick() {

        }

        @Override
        public void onAdvertisementViewWillStartNewIntent() {

        }
    }); */
    // 设置监听回调
    bannerContainerLayout.addView(bannerview);
}
  private void checkSelectColor(int selectColor){
      switch(selectColor){
        case R.color.pink:
            mRadioGroup1.check(R.id.pink_color_button);
            break;
        case R.color.azure:
            mRadioGroup1.check(R.id.azure_color_button);
            break;
        case R.color.grey:
            mRadioGroup1.check(R.id.grey_color_button);
            break;
        case R.color.grey_red:
            mRadioGroup1.check(R.id.grey_red_color_button);
            break;
        case R.color.orange:
            mRadioGroup1.check(R.id.orange_color_button);
            break;
        case android.R.color.white:
            mRadioGroup1.check(R.id.white_color_button);
            break;
    }

}

private View.OnClickListener clickListener = new View.OnClickListener(){
    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.btn_cancel:
              SetDialog.this.cancel();
              StatService.onPageEnd(mContext, "设置页");
              break;
          case R.id.animal_btn://动物

              break;
          case R.id.girl_btn://美女

              break;
          case R.id.other_img_btn://自定义
              ImagePicker imagePicker = ImagePicker.getInstance();
              imagePicker.setImageLoader(new GlideImageLoader());
              imagePicker.setMultiMode(false);   //单选（多选不能剪裁）
              imagePicker.setShowCamera(true);  //显示拍照按钮
              imagePicker.setSelectLimit(9);    //最多选择9张
              imagePicker.setCrop(true);       //进行裁剪
              imagePicker.setOutPutX(RecyclerViewAdapter.imgWidth);//图片裁剪的宽度
              imagePicker.setOutPutY(RecyclerViewAdapter.imgWidth);//图片裁剪的高度
              Intent intent = new Intent(mContext, ImageGridActivity.class);
              ((Activity)mContext).startActivityForResult(intent, 100);
              break;
      }
    }
};

    public void showSelectImg(Bitmap mBitmap){
        switchImg.setImageBitmap(mBitmap);
    }
    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.radioButton://同时切换一个
                    preferenceOperator.putInt(R.string.switch_number,1);
                    GameUtil.SWITCH_NUM = 1;
                    break;
                case R.id.radioButton2://同时切换两个
                    preferenceOperator.putInt(R.string.switch_number,2);
                    GameUtil.SWITCH_NUM = 2;
                    break;
                case R.id.pink_color_button:
                     preferenceOperator.putInt(R.string.item_color,R.color.pink);
                     RecyclerViewAdapter.colorRsid = R.color.pink;
                    mHandler.sendEmptyMessage(REFLASH_BG);
                    break;
                case R.id.azure_color_button:
                    preferenceOperator.putInt(R.string.item_color,R.color.azure);
                    RecyclerViewAdapter.colorRsid = R.color.azure;
                    mHandler.sendEmptyMessage(REFLASH_BG);
                    break;
                case R.id.grey_color_button:
                    preferenceOperator.putInt(R.string.item_color,R.color.grey);
                    RecyclerViewAdapter.colorRsid = R.color.grey;
                    mHandler.sendEmptyMessage(REFLASH_BG);
                    break;
                case R.id.grey_red_color_button:
                    preferenceOperator.putInt(R.string.item_color,R.color.grey_red);
                    RecyclerViewAdapter.colorRsid = R.color.grey_red;
                    mHandler.sendEmptyMessage(REFLASH_BG);
                    break;
                case R.id.orange_color_button:
                    preferenceOperator.putInt(R.string.item_color,R.color.orange);
                    RecyclerViewAdapter.colorRsid = R.color.orange;
                    mHandler.sendEmptyMessage(REFLASH_BG);
                    break;
                case R.id.white_color_button:
                    preferenceOperator.putInt(R.string.item_color,android.R.color.white);
                    RecyclerViewAdapter.colorRsid = android.R.color.white;
                    mHandler.sendEmptyMessage(REFLASH_BG);
                    break;
            }
        }
    };
}
