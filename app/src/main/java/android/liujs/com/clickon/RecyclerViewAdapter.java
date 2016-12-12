package android.liujs.com.clickon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.liujs.library.data.PreferenceOperator;

/**
 * Created by liujs on 2016/10/30.
 * 邮箱：725459481@qq.com
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter {
    private final int  ITEM_COUNT = 16;
    private Context mContext;
    public static int imgWidth;
    public static int colorRsid;
    private PreferenceOperator preferenceOperator;
    private ItemOnClikcListener itemOnClikcListener;

    public RecyclerViewAdapter(Context mContext,PreferenceOperator preferenceOperator){
        this.mContext = mContext;
        this.preferenceOperator =  preferenceOperator;
    }

    public void setItemOnClikcListener(ItemOnClikcListener itemOnClikcListener) {
        this.itemOnClikcListener = itemOnClikcListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.gridview_item,null);

        return new Holder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(colorRsid==0) colorRsid = preferenceOperator.getInt(R.string.item_color,0);
        if(colorRsid != 0){
            ((Holder)holder).mImageView.setBackgroundResource(colorRsid);

        }else{
            ((Holder)holder).mImageView.setBackgroundResource(0);
        }
        ((Holder)holder).mImageView.setOnClickListener(new ClickListener(position));
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
   private class ClickListener implements View.OnClickListener{
       private int position;

       public ClickListener(int position){
           this.position = position;
       }
       @Override
       public void onClick(View v) {
           if(itemOnClikcListener!=null){
               itemOnClikcListener.itemOnClick(v,this.position);
           }
       }
   };

    class Holder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public Holder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.grid_item);
            mImageView.post(new Runnable() {
                @Override
                public void run() {
                   if(imgWidth==0) imgWidth = mImageView.getMeasuredWidth();
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imgWidth,imgWidth);
                    mImageView.setLayoutParams(layoutParams);
                }
            });
        }

    }
}
 interface  ItemOnClikcListener {
    void itemOnClick(View view,int position);
}