package example.com.androidfire2.ui.main.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.jaydenxiao.common.base.BaseFragment;
import com.jaydenxiao.common.base.BaseFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.NewsChannelTable;
import example.com.androidfire2.ui.main.contract.NewsMainContract;
import example.com.androidfire2.ui.main.model.NewsMainModel;
import example.com.androidfire2.ui.main.presenter.NewsMainPresenter;
import example.com.androidfire2.ui.news.activty.NewsChannelActivity;
import example.com.androidfire2.ui.news.fragment.NewsListFragment;
import example.com.androidfire2.utils.MyUtils;


/**
 * Created by asus-pc on 2017/7/20.
 */

public class NewsMainFragment extends BaseFragment<NewsMainPresenter, NewsMainModel> implements NewsMainContract.View {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.add_channel_iv)
    ImageView addChannelIv;
    @Bind(R.id.view_pager1)
    ViewPager viewPager;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private BaseFragmentAdapter fragmentAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_news;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);
    }

    @Override
    protected void initView() {
        //显示我的频道
        mPresenter.lodeMineChannelsRequest();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送事件
                mRxManager.post(AppConstant.NEWS_LIST_TO_TOP, "");
            }
        });
    }

    @OnClick(R.id.add_channel_iv)
    public void clickAdd() {
        NewsChannelActivity.startAction(getContext());
    }

    //显示新闻频道
    @Override
    public void returnMineNewsChannels(List<NewsChannelTable> newsChannelsMine) {
        if (newsChannelsMine != null) {
            List<String> channelNames = new ArrayList<>();
            List<Fragment> mNewsFragmentList = new ArrayList<>();
            for (int i=0;i<newsChannelsMine.size();i++) {
                channelNames.add(newsChannelsMine.get(i).getNewsChannelName());
                mNewsFragmentList.add(createListFragments(newsChannelsMine.get(i)));
            }
            if(fragmentAdapter==null) {
                fragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager(), mNewsFragmentList, channelNames);
            }else{
                //刷新fragment
                fragmentAdapter.setFragments(getChildFragmentManager(),mNewsFragmentList,channelNames);
            }
            viewPager.setAdapter(fragmentAdapter);
            tabs.setupWithViewPager(viewPager);//必须在setadapter之后
            //设置tabs根据容器大小的排列样式
            MyUtils.dynamicSetTabLayoutMode(tabs);
            setPageChangeListener();
        }

    }

    private void setPageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //当页面在滑动的时候会调用此方法
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面跳转完后得到调用
            @Override
            public void onPageSelected(int position) {
            }

            //此方法是在状态改变的时候调用
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //根据频道创建的listfragment
    private NewsListFragment createListFragments(NewsChannelTable newsChannel) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.NEWS_ID, newsChannel.getNewsChannelId());
        bundle.putString(AppConstant.NEWS_TYPE, newsChannel.getNewsChannelType());
        bundle.putInt(AppConstant.CHANNEL_POSITION, newsChannel.getNewsChannelIndex());
        fragment.setArguments(bundle);
        return fragment;
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
