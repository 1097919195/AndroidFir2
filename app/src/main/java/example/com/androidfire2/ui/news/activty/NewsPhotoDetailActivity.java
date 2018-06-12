package example.com.androidfire2.ui.news.activty;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonwidget.ViewPagerFixed;

import butterknife.Bind;
import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.NewsPhotoDetail;

/**
 * Created by asus-pc on 2017/9/30.
 */

public class NewsPhotoDetailActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewpager)
    ViewPagerFixed viewpager;
    @Bind(R.id.photo_detail_title_tv)
    TextView photoDetailTitleTv;

    /**
     * 入口
     * @param mContext
     * @param photoDetail
     */
    public static void startAction(Context mContext, NewsPhotoDetail photoDetail) {
        Intent intent = new Intent(mContext, NewsPhotoDetailActivity.class);
        intent.putExtra(AppConstant.PHOTO_DETAIL, photoDetail);
        mContext.startActivity(intent);

    }

    @Override
    public int getLayoutId() {
        return R.layout.act_news_photo_detail;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
