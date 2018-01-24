package io.weichao.stickynavigationbar.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.weichao.stickynavigationbar.R;
import io.weichao.stickynavigationbar.bean.DataResponseBean;
import io.weichao.stickynavigationbar.bean.NameBean;
import io.weichao.stickynavigationbar.model.MainModel;
import io.weichao.stickynavigationbar.widget.SectionDecoration;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MainModel mModel;

    private RecyclerView mRecyclerView;
    private ArrayList<NameBean> mNameBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);// 取消动画，否则 notify 会闪一下

        mModel = new MainModel(this);
        mModel.getData();
    }

    @Override
    protected void onDestroy() {
        mModel.onDestroy();
        super.onDestroy();
    }

    public void onSuccessGetData(DataResponseBean bean) {
        List<DataResponseBean.DataBean.ComingBean> comingBeanList = bean.getData().getComing();
        setPullAction(comingBeanList);
        mRecyclerView.addItemDecoration(new SectionDecoration(mNameBeanList, this, new SectionDecoration.DecorationCallback() {
            //返回标记id (即每一项对应的标志性的字符串)
            @Override
            public String getGroupId(int position) {
                if (mNameBeanList.get(position).getName() != null) {
                    return mNameBeanList.get(position).getName();
                }
                return "-1";
            }

            //获取同组中的第一个内容
            @Override
            public String getGroupFirstLine(int position) {
                if (mNameBeanList.get(position).getName() != null) {
                    return mNameBeanList.get(position).getName();
                }
                return "";
            }
        }));
        mRecyclerView.setAdapter(new MyRecyclerAdapter(this, comingBeanList));
    }

    public void onFailedGetData(String msg) {
        Log.d(TAG, msg);
    }

    private void setPullAction(List<DataResponseBean.DataBean.ComingBean> comingslist) {
        mNameBeanList = new ArrayList<>();
        for (int i = 0; i < comingslist.size(); i++) {
            NameBean nameBean = new NameBean();
            String name = comingslist.get(i).getComingTitle();
            nameBean.setName(name);
            mNameBeanList.add(nameBean);
        }
    }

    class MyRecyclerAdapter extends RecyclerView.Adapter {
        private final Context mContext;
        private final List<DataResponseBean.DataBean.ComingBean> mComingslist;
        private final LayoutInflater mLayoutInflater;

        private MyRecyclerAdapter(Context context, List<DataResponseBean.DataBean.ComingBean> comingslist) {
            mContext = context;
            mComingslist = comingslist;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(mLayoutInflater.inflate(R.layout.date_item, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder myholder = (MyViewHolder) holder;
            myholder.setData(position);
        }

        @Override
        public int getItemCount() {
            return mComingslist.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView mv_name;
            private TextView mv_dec;
            private TextView mv_date;
            private ImageView imageView;

            private MyViewHolder(View itemView) {
                super(itemView);
                mv_name = itemView.findViewById(R.id.mv_name);
                mv_dec = itemView.findViewById(R.id.mv_dec);
                mv_date = itemView.findViewById(R.id.mv_date);
                imageView = itemView.findViewById(R.id.image);
            }

            public void setData(int position) {
                DataResponseBean.DataBean.ComingBean coming = mComingslist.get(position);
                mv_name.setText(coming.getNm());
                mv_date.setText(coming.getShowInfo());
                mv_dec.setText(coming.getScm());
                //注：当你发下图片无法打开是，做个字符串替换即可
                String imagUrl = coming.getImg();
                String newImagUrl = imagUrl.replaceAll("w.h", "50.80");
                Glide.with(mContext)
                        .load(newImagUrl)
                        .into(imageView);
            }
        }
    }
}
