package example.com.androidfire2.ui.main.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baserx.RxBus;
import com.jaydenxiao.common.commonutils.ToastUitl;
import com.jaydenxiao.common.daynightmodeutils.ChangeModeController;
import com.nineoldandroids.view.ViewHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.Bind;
import cn.hugeterry.updatefun.UpdateFunGO;
import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.TabEntity;
import example.com.androidfire2.ui.main.fragment.CareMainFragment;
import example.com.androidfire2.ui.main.fragment.NewsMainFragment;
import example.com.androidfire2.ui.main.fragment.PhotosMainFragment;
import example.com.androidfire2.ui.main.fragment.VideoMainFragment;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import rx.functions.Action1;

import static com.jaydenxiao.common.commonutils.LogUtils.loge;

public class MainActivity extends BaseActivity {
    @Bind(R.id.tab_layout)
    CommonTabLayout tabLayout;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private String[] mTitles = {"首页","美女","视频","关注"};
    private int[] mIconUnselectIds = {
            R.mipmap.ic_home_normal,R.mipmap.ic_girl_normal,R.mipmap.ic_video_normal,R.mipmap.ic_care_normal};
    private int[] mIconSelectIds = {
            R.mipmap.ic_home_selected,R.mipmap.ic_girl_selected, R.mipmap.ic_video_selected,R.mipmap.ic_care_selected};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private NewsMainFragment newsMainFragment;
    private PhotosMainFragment photosMainFragment;
    private VideoMainFragment videoMainFragment;
    private CareMainFragment careMainFragment;
    private static int tabLayoutHeight;

    float scale=1;//侧滑滑动的最大幅度
    private ViewPager viewPager1,viewPager2;
    private Rect mChangeImageBackgroundRect = null;
    private boolean have = false;

    float DownX = 0;
    float DownY = 0;
    float MoveX = 0;
    float MoveY = 0;


    @Override
    public int getLayoutId() {
//        return R.layout.act_main;
        return R.layout.drawer_side_slip_menu;//抽屉侧滑
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        //初始化菜单
        initTab();
        //SetTranslanteBar();沉浸 还需改布局

        //侧滑监听
        drawerLayoutListener();
        //设置抽屉滑动边距
        setDrawerLeftEdgeSize(this, mDrawerLayout, 0.8f);

    }

    public void openDrawer(View drawerView) {
        ViewHelper.setTranslationX(drawerView, DownX - MoveX);
    }

    //dispatchTouchEvent方法负责touch事件的分发
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){

//        viewPager = (ViewPager) getSupportFragmentManager().findFragmentById(R.id.fl_body).getView().findViewById(R.id.view_pager);
        viewPager1 = (ViewPager) this.findViewById(R.id.view_pager1);
        viewPager2 = (ViewPager) this.findViewById(R.id.view_pager2);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            DownX = event.getX();
            DownY = event.getY();

            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            MoveX = event.getX();
            MoveY = event.getY();

            // 监听屏幕滑动状态-viewpager控件是否可见
            Point p = new Point();
            getWindowManager().getDefaultDisplay().getSize(p);
            int screenWidth = p.x;
            int screenHeight = p.y;
            Rect rect = new Rect(0, 0, screenWidth, screenHeight);
            int[] location = new int[2];
            viewPager1.getLocationInWindow(location);
            viewPager2.getLocationInWindow(location);
            // 控件在屏幕可见区域
            if (viewPager1.getLocalVisibleRect(rect) && !newsMainFragment.isHidden() || viewPager2.getLocalVisibleRect(rect) && !videoMainFragment.isHidden()) {
                displayXY(DownX, DownY);// 获取到手指坐标，进行判断手否在该view中，返回布尔值have

                //当手指在viewpager上的时候，不进行drawerlayout监听(左边80px开始算起)
                if (have==true && DownX > 95) {
                    ToastUitl.showShort("DownX===="+DownX);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//禁止手势滑动
                }else{

                    //当滑动一定幅度时
                    if (MoveX - DownX > 20 && Math.abs(MoveY-DownY) < 7) {
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//打开
                    }

                }

            } else {// 非指定控件区域
                //当滑动一定幅度时
                if (MoveX - DownX > 20 && Math.abs(MoveY-DownY) < 7) {
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//打开
//                    openDrawer(findViewById(R.id.left_drawer));
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }
    // 获取到手指坐标，进行判断
    private void displayXY(float x, float y) {
        if (!newsMainFragment.isHidden()) {
            isInChangeImageZone(viewPager1, x, y);
        } else if(!videoMainFragment.isHidden()){
            isInChangeImageZone(viewPager2, x, y);
        }
    }
    //判断touch事件点是否在view范围内
    private boolean isInChangeImageZone(View view, float x, float y) {
        if (null == mChangeImageBackgroundRect) {
            mChangeImageBackgroundRect = new Rect();
        }
        view.getDrawingRect(mChangeImageBackgroundRect);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mChangeImageBackgroundRect.left = location[0];
        mChangeImageBackgroundRect.top = location[1];
        mChangeImageBackgroundRect.right = mChangeImageBackgroundRect.right + location[0];
        mChangeImageBackgroundRect.bottom = mChangeImageBackgroundRect.bottom + location[1];
        have = mChangeImageBackgroundRect.contains((int) Math.ceil(x), (int) Math.ceil(y));
        Log.e("werwer","123123");
        loge("werwer"+have);
        return have;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //切换daynight模式要立即变色的页面
        ChangeModeController.getInstance().init(this,R.attr.class);
        super.onCreate(savedInstanceState);
        //初始化frament
        initFragment(savedInstanceState);
        tabLayout.measure(0,0);
        tabLayoutHeight=tabLayout.getMeasuredHeight();
        //监听菜单显示或隐藏
        mRxManager.on(AppConstant.MENU_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                startAnimation(hideOrShow);
            }
        });
    }

    //初始化tab
    private void initTab() {
        for (int i=0;i<mTitles.length;i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        tabLayout.setTabData(mTabEntities);
        //点击监听
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                SwitchTo(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    //初始化碎片
    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;//改变值可以改变刚进入的fragment页面
        if (savedInstanceState != null) {
            newsMainFragment = (NewsMainFragment) getSupportFragmentManager().findFragmentByTag("newsMainFragment");
            photosMainFragment = (PhotosMainFragment) getSupportFragmentManager().findFragmentByTag("photosMainFragment");
            videoMainFragment = (VideoMainFragment) getSupportFragmentManager().findFragmentByTag("videoMainFragment");
            careMainFragment = (CareMainFragment) getSupportFragmentManager().findFragmentByTag("careMainFragment");
            //读取最近存储时的那个值
            currentTabPosition = savedInstanceState.getInt(AppConstant.HOME_CURRENT_TAB_POSITION);
        }else {
            newsMainFragment = new NewsMainFragment();
            photosMainFragment = new PhotosMainFragment();
            videoMainFragment = new VideoMainFragment();
            careMainFragment = new CareMainFragment();

            transaction.add(R.id.fl_body, newsMainFragment, "newsMainFragment");
            transaction.add(R.id.fl_body, photosMainFragment, "photosMainFragment");
            transaction.add(R.id.fl_body, videoMainFragment, "videoMainFragment");
            transaction.add(R.id.fl_body, careMainFragment, "careMainFragment");
        }
        transaction.commit();
        SwitchTo(currentTabPosition);
        //设置当前tab指向对应的Tab
        tabLayout.setCurrentTab(currentTabPosition);
    }
    /**
     * 在onSaveInstanceState()方法之后去调用commit()，就会抛出我们遇到的这个异常，这是因为在
     * onSaveInstanceState()之后调用commit()方法，这些变化就不会被activity存储，即这些状态会被丢失,
     * 但我们可以去用commitAllowingStateLoss()这个方法去代替commit()来解决这个为题
     */

    //切换
    private void SwitchTo(int position) {
//        RxBus.getInstance().post(AppConstant.MENU_SHOW_HIDE,true);//发送显示菜单栏事件 为了切换页面后显示菜单栏(防止recycleview滑动时立马切换界面导致菜单栏消失)

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            //首页
            case 0:
                transaction.hide(photosMainFragment);
                transaction.hide(videoMainFragment);
                transaction.hide(careMainFragment);
                transaction.show(newsMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //美女
            case 1:
                transaction.hide(newsMainFragment);
                transaction.hide(videoMainFragment);
                transaction.hide(careMainFragment);
                transaction.show(photosMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //视频
            case 2:
                transaction.hide(newsMainFragment);
                transaction.hide(photosMainFragment);
                transaction.hide(careMainFragment);
                transaction.show(videoMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            //关注
            case 3:
                transaction.hide(newsMainFragment);
                transaction.hide(photosMainFragment);
                transaction.hide(videoMainFragment);
                transaction.show(careMainFragment);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    /**
     * 菜单显示隐藏动画
     * @param showOrHide
     */
    private void startAnimation(boolean showOrHide){
        final ViewGroup.LayoutParams layoutParams = tabLayout.getLayoutParams();
        ValueAnimator valueAnimator;
        ObjectAnimator alpha;
        if(!showOrHide){
            valueAnimator = ValueAnimator.ofInt(tabLayoutHeight, 0);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 1, 0);
            RxBus.getInstance().post(AppConstant.FAB_SHOW_HIDE,false);
        }else{
            valueAnimator = ValueAnimator.ofInt(0, tabLayoutHeight);
            alpha = ObjectAnimator.ofFloat(tabLayout, "alpha", 0, 1);
            RxBus.getInstance().post(AppConstant.FAB_SHOW_HIDE,true);
        }
        //调用addUpdateListener(AnimatorUpdateListener mListener)方法为ValueAnimator对象设置属性变化的监听器
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height= (int) valueAnimator.getAnimatedValue();
                tabLayout.setLayoutParams(layoutParams);
            }
        });
        //AnimatorSet混合动画类，可以并联或者串联地播放动画。一般的AnimatorSet会和ObjectAnimator一起使用用于切实的改变视图的属性(普通的Animation不会改变视图的属性，动画播放完毕后视图又恢复原来的属性)
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator,alpha);
        animatorSet.start();
    }

    //侧滑监听
    private void drawerLayoutListener() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            /**
             * 当抽屉滑动状态改变的时候被调用
             * 状态值是STATE_IDLE（闲置--0）, STATE_DRAGGING（拖拽的--1）, STATE_SETTLING（固定--2）中之一。
             * 抽屉打开的时候，点击抽屉，drawer的状态就会变成STATE_DRAGGING，然后变成STATE_IDLE
             */
            @Override
            public void onDrawerStateChanged(int newState) {
                Log.i("drawer", "drawer的状态：" + newState);
            }
            /**
             * 当抽屉被滑动的时候调用此方法
             * slideOffset 表示 滑动的幅度（0-1）
             */
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Log.i("drawer", slideOffset + "");
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;//内容区域的缩放比例计算

                //左边菜单滑动效果
                float leftScale = 1 - 0.3f * scale;//菜单的缩放比例计算
                ViewHelper.setScaleX(mMenu, leftScale);
                ViewHelper.setScaleY(mMenu, leftScale);
                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));//内容的x方向偏移量
                ViewHelper.setPivotX(mContent, 0);
                ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                ViewHelper.setScaleX(mContent, rightScale);
                ViewHelper.setScaleY(mContent, rightScale);


//                //右边菜单滑动效果
//                ViewHelper.setTranslationX(mContent,
//                        -mMenu.getMeasuredWidth() * slideOffset);
//                ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
//                ViewHelper.setPivotY(mContent,
//                        mContent.getMeasuredHeight() / 2);
//                mContent.invalidate();
//                ViewHelper.setScaleX(mContent, rightScale);
//                ViewHelper.setScaleY(mContent, rightScale);

            }
            /**
             * 当一个抽屉被完全打开的时候被调用
             */
            @Override
            public void onDrawerOpened(View drawerView) {
                Log.i("drawer", "抽屉被完全打开了！");
            }
            /**
             * 当一个抽屉完全关闭的时候调用此方法
             */
            @Override
            public void onDrawerClosed(View drawerView) {
                Log.i("drawer", "抽屉被完全关闭了！");
            }
        });

    }
    //设置抽屉滑动边距(http://blog.csdn.net/a598068693/article/details/52084612 长按的问题)
    private void setDrawerLeftEdgeSize (Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) return;
        try {
            // 找到 ViewDragHelper 并设置 Accessible 为true
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");//Right
            leftDraggerField.setAccessible(true);//java代码中，常常将一个类的成员变量置为private,在类的外面获取此类的私有成员变量的value时,故必须进行此操作
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);

            // 找到 edgeSizeField 并设置 Accessible 为true
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);

            // 现获取屏幕大小，然后设置新的边缘大小
            DisplayMetrics dm = new DisplayMetrics ();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (Exception e) {
        }
    }

    /**
     * 监听全屏视频时返回键
     */
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);//可将activity 退到后台，注意不是finish()退出。参数为false——代表只有当前activity是task根，指应用启动的第一个activity时，才有效;参数为true——则忽略这个限制，任何activity都可以有效。

            //如果有抽屉菜单栏滑出了，返回键监听关闭菜单栏事件
            if (scale!=1) {
                mDrawerLayout.closeDrawers();
            }else {
                moveTaskToBack(true);
            }


            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //onSaveInstanceState在需要空出内存给当前Activity的时候执行
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //奔溃前保存位置
        loge("onSaveInstanceState进来了1");
        if (tabLayout != null) {
            loge("onSaveInstanceState进来了2");
            outState.putInt(AppConstant.HOME_CURRENT_TAB_POSITION, tabLayout.getCurrentTab());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChangeModeController.onDestory();
    }
}
