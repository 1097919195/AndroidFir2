package example.com.androidfire2.ui.news.activty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jaydenxiao.common.commonwidget.StatusBarCompat;

import butterknife.Bind;
import butterknife.ButterKnife;
import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import uk.co.senab.photoview.PhotoView;


/**
 * Created by asus-pc on 2017/10/18.
 */

public class PhotosDetailActivity extends AppCompatActivity {

    @Bind(R.id.photo_touch_iv)
    PhotoView photoTouchIv;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.background)
    RelativeLayout background;

    public static void startAction(Context context, String url) {
        Intent intent = new Intent(context, PhotosDetailActivity.class);
        intent.putExtra(AppConstant.PHOTO_DETAIL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.translucentStatusBar(this);
        setContentView(R.layout.act_photos_detail);
        ButterKnife.bind(this);//绑定activity

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initView() {
        initToolbar();
        loadPhotoIv();
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.girl));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadPhotoIv() {
        String url = getIntent().getStringExtra(AppConstant.PHOTO_DETAIL);
        Glide.with(this).load(url)
                //磁盘缓存策略
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //当加载出错，或者加载不出来的时候
                .error(com.jaydenxiao.common.R.drawable.ic_empty_picture)
                //标准的淡入淡出动画
                .crossFade()
                .into(photoTouchIv);
    }
}
