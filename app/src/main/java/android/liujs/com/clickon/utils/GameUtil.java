package android.liujs.com.clickon.utils;

import com.liujs.library.utils.LogUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by liujs on 2016/11/2.
 * 邮箱：725459481@qq.com
 */

public class GameUtil {
    private static final int IMAGE_COUNT = 16;
    public static final String BaiDu_App_ID = "8949432";
    public static final String BaiDu_App_Key = "jSjON9qhl2SHe3DchWSvOveu7ZjtOOAr";
    public static final String BaiDu_Secret_Key = "T8eOhEbsDpy8zmBiZlYD6ZsKTxvNTmuX";
    public static final String SDK_BANNER_AD_ID = "";
    public static  int SWITCH_NUM = 1;
    public static ArrayList<Integer> clickItemList = new ArrayList<Integer>();
    public static ArrayList<Integer> unClickList = new ArrayList<Integer>(){{add(0); add(1);add(2); add(3);add(4); add(5);add(6); add(7);add(8); add(9);
        add(10); add(11); add(12);add(13); add(14);add(15); }};

    /**
     * 随机获取一个item进行点击
     * @return
     */
    public static   int  getClickItem(){
        LogUtil.d("unClickitem size",unClickList.size()+"");
        if(unClickList.size() > 0){
            Random random=new Random();
            int  randomItem =   random.nextInt(unClickList.size());
            int selectPosition = unClickList.get(randomItem);
            clickItemList.add(selectPosition);
            unClickList.remove(new Integer(selectPosition));
            return selectPosition;
        }else{
            return  -1;
        }
    }

    /**
     * 成功点击了一个item后调用
     * @param position
     */
    public static   void removeClickItem(int position){
        clickItemList.remove(new Integer(position));
        unClickList.add(position);
    }

    public static void reInitItem(){
        GameUtil.unClickList.addAll(GameUtil.clickItemList);
        GameUtil.clickItemList.clear();
    }
}
