package example.com.androidfire2.ui.news.activty;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.TimeUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.NewsDetail;
import example.com.androidfire2.ui.news.contract.NewsDetailContract;
import example.com.androidfire2.ui.news.model.NewsDetailModel;
import example.com.androidfire2.ui.news.presenter.NewsDetailPresenter;
import example.com.androidfire2.widget.URLImageGetter;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by asus-pc on 2017/9/29.
 */

public class NewsDetailActivity extends BaseActivity<NewsDetailPresenter,NewsDetailModel> implements NewsDetailContract.View{

    @Bind(R.id.news_detail_photo_iv)
    ImageView newsDetailPhotoIv;
    @Bind(R.id.mask_view)
    View maskView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.news_detail_from_tv)
    TextView newsDetailFromTv;
    @Bind(R.id.news_detail_body_tv)
    TextView newsDetailBodyTv;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private String postId;
    private URLImageGetter mUrlImageGetter;
    private String mNewsTitle;
    private String mShareLink;
    /**
     * 入口
     * @param mContext
     * @param view
     * @param postId
     * @param imgUrl
     */
    public static void startAction(Context mContext, View view, String postId, String imgUrl) {
        Intent intent = new Intent(mContext, NewsDetailActivity.class);
        intent.putExtra(AppConstant.NEWS_POST_ID, postId);
        intent.putExtra(AppConstant.NEWS_IMG_RES, imgUrl);

        //Build.VERSION.SDK_INT获取当前手机版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation((Activity) mContext,view, AppConstant.TRANSITION_ANIMATION_NEWS_PHOTOS);
            mContext.startActivity(intent, options.toBundle());
        } else {

            //让新的Activity从一个小的范围扩大到全屏
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.act_news_detail;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this, mModel);
    }

    @Override
    public void initView() {
        SetTranslanteBar();
        postId = getIntent().getStringExtra(AppConstant.NEWS_POST_ID);
        mPresenter.getOneNewsDataRequest(postId);

        toolbar.inflateMenu(R.menu.news_detail);//设置添加菜单选项
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });

        //分享
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShareLink==null) {
                    mShareLink = "";
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                //调用手机默认分享
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_contents, mNewsTitle, mShareLink));
                startActivity(Intent.createChooser(intent, getTitle()));
            }
        });

    }

    @Override
    public void returnOneNewsData(NewsDetail newsDetail) {
        mShareLink = newsDetail.getShareLink();
        mNewsTitle = newsDetail.getTitle();
        String newsSource = newsDetail.getSource();
        String newsTime = TimeUtil.formatDate(newsDetail.getPtime());
        String newsBody = newsDetail.getBody();
        String NewsImgSrc = getImgSrcs(newsDetail);

        setToolBarLayout(mNewsTitle);
        //mNewsDetailTitleTv.setText(newsTitle);
        newsDetailFromTv.setText(getString(R.string.news_from, newsSource, newsTime));
        setNewsDetailPhotoIv(NewsImgSrc);
        setNewsDetailBodyTv(newsDetail, newsBody);
    }

    private void setToolBarLayout(String newsTitle) {
        toolbarLayout.setTitle(newsTitle);
        toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_white));
    }

    private void setNewsDetailPhotoIv(String imgSrc) {
        Glide.with(this).load(imgSrc)
                .fitCenter()
                .error(com.jaydenxiao.common.R.drawable.ic_empty_picture)
                .crossFade().into(newsDetailPhotoIv);
    }

    private void setNewsDetailBodyTv(final NewsDetail newsDetail, final String newsBody) {
        mRxManager.add(Observable.timer(500, TimeUnit.MILLISECONDS)
                .compose(RxSchedulers.<Long>io_main())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        progressBar.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        setBody(newsDetail, newsBody);
                    }
                }));
    }

    private void setBody(NewsDetail newsDetail, String newsBody) {
        int imgTotal = newsDetail.getImg().size();
        if (isShowBody(newsBody, imgTotal)) {
//              mNewsDetailBodyTv.setMovementMethod(LinkMovementMethod.getInstance());//加这句才能让里面的超链接生效,实测经常卡机崩溃
            mUrlImageGetter = new URLImageGetter(newsDetailBodyTv, newsBody, imgTotal);
            newsDetailBodyTv.setText(Html.fromHtml(newsBody, mUrlImageGetter, null));
        } else {
            newsDetailBodyTv.setText(Html.fromHtml(newsBody));
        }
    }

    private boolean isShowBody(String newsBody, int imgTotal) {
        return imgTotal >= 2 && newsBody != null;
    }

    private String getImgSrcs(NewsDetail newsDetail) {
        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
        String imgSrc;
        if (imgSrcs != null && imgSrcs.size() > 0) {
            imgSrc = imgSrcs.get(0).getSrc();
        } else {
            imgSrc = getIntent().getStringExtra(AppConstant.NEWS_IMG_RES);
        }
        return imgSrc;
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }
}
