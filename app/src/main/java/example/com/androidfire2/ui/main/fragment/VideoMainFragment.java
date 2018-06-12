package example.com.androidfire2.ui.main.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jaydenxiao.common.base.BaseFragment;
import com.jaydenxiao.common.base.BaseFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.VideosChannelTable;
import example.com.androidfire2.db.VideosChannelTableManager;
import example.com.androidfire2.ui.news.fragment.VideosListFragment;
import example.com.androidfire2.utils.MyUtils;


/**
 * Created by asus-pc on 2017/7/20.
 */

public class VideoMainFragment extends BaseFragment {
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.view_pager2)
    ViewPager viewPager;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    private BaseFragmentAdapter fragmentAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_vedio;
    }

    @Override
    public void initPresenter() {
    }

    @Override
    protected void initView() {
        List<String> channelNames = new ArrayList<>();
        List<VideosChannelTable> videosChannelTablesList = VideosChannelTableManager.loadVideosChannelsMine();
        List<Fragment> mVideosFragmentList = new ArrayList<>();
        for (int i = 0; i < videosChannelTablesList.size(); i++) {
            channelNames.add(videosChannelTablesList.get(i).getChannelName());
            mVideosFragmentList.add(createListFragments(videosChannelTablesList.get(i)));
        }
        fragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager(), mVideosFragmentList, channelNames);
        viewPager.setAdapter(fragmentAdapter);
        //建立关联
        tabs.setupWithViewPager(viewPager);
        MyUtils.dynamicSetTabLayoutMode(tabs);
        setPageChangeListener();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRxManager.post(AppConstant.NEWS_LIST_TO_TOP, "");
            }
        });

    }

    private void setPageChangeListener() {
    }

    private VideosListFragment createListFragments(VideosChannelTable videosChannelTable) {
        VideosListFragment fragment = new VideosListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.VIDEO_TYPE, videosChannelTable.getChannelId());
        fragment.setArguments(bundle);
        return fragment;
    }

}
