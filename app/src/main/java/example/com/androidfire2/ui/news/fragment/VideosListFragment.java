package example.com.androidfire2.ui.news.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;
import com.aspsine.irecyclerview.widget.LoadMoreFooterView;
import com.jaydenxiao.common.base.BaseFragment;
import com.jaydenxiao.common.commonwidget.LoadingTip;

import java.util.List;

import butterknife.Bind;
import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.VideoData;
import example.com.androidfire2.ui.news.contract.VideosListContract;
import example.com.androidfire2.ui.news.model.VideosListModel;
import example.com.androidfire2.ui.news.presenter.VideosListPresenter;

/**
 * 视频fragment
 */
public class VideosListFragment extends BaseFragment<VideosListPresenter,VideosListModel> implements VideosListContract.View, OnRefreshListener, OnLoadMoreListener{

    @Bind(R.id.irc)
    IRecyclerView irc;
    @Bind(R.id.loadedTip)
    LoadingTip loadedTip;

    private CommonRecycleViewAdapter<VideoData> videoListAdapter;

    private String mVideoType;
    private int mStartPage=0;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_news_list;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            mVideoType = getArguments().getString(AppConstant.VIDEO_TYPE);
        }
        //设置布局管理器
        irc.setLayoutManager(new LinearLayoutManager(getContext()));
        videoListAdapter = new CommonRecycleViewAdapter<VideoData>(getContext(),R.layout.item_video_list) {
            @Override
            public void convert(ViewHolderHelper helper, VideoData videoData) {

                helper.setText(R.id.tv, videoData.getTopicName());
            }
        };
        irc.setAdapter(videoListAdapter);
        irc.setOnRefreshListener(this);
        irc.setOnLoadMoreListener(this);
        //视频监听
        irc.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {

            }
        });

        //数据为空发起请求
        if (videoListAdapter.getSize()<=0) {
            mStartPage=0;
            mPresenter.getVideosListDataRequest(mVideoType,mStartPage);
        }

    }

    @Override
    public void returnVideosListData(List<VideoData> videoDatas) {
        if (videoDatas != null) {
            mStartPage += 1;
            if (videoListAdapter.getPageBean().isRefresh()) {
                irc.setRefreshing(false);
                videoListAdapter.replaceAll(videoDatas);
            } else {
                if (videoDatas.size() > 0) {
                    irc.setLoadMoreStatus(LoadMoreFooterView.Status.GONE);
                    videoListAdapter.addAll(videoDatas);
                } else {
                    irc.setLoadMoreStatus(LoadMoreFooterView.Status.THE_END);
                }
            }
        }

    }

    /**
     * 返回顶部
     */
    @Override
    public void scrolltoTop() {
        irc.smoothScrollToPosition(0);
    }

    @Override
    public void onRefresh() {
        videoListAdapter.getPageBean().setRefresh(true);
        mStartPage = 0;
        //发起请求
        irc.setRefreshing(true);
        mPresenter.getVideosListDataRequest(mVideoType, mStartPage);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        //数据不为空才能刷新加载
        if (videoListAdapter.getSize() > 0) {
            videoListAdapter.getPageBean().setRefresh(false);
            //发起请求
            irc.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING);
            mPresenter.getVideosListDataRequest(mVideoType, mStartPage);
        }

    }

    @Override
    public void showLoading(String title) {
        //无数据时的加载效果
        if( videoListAdapter.getPageBean().isRefresh()) {
            if(videoListAdapter.getSize()<=0) {
                loadedTip.setLoadingTip(LoadingTip.LoadStatus.loading);
            }
        }
    }

    @Override
    public void stopLoading() {
        loadedTip.setLoadingTip(LoadingTip.LoadStatus.finish);
    }

    @Override
    public void showErrorTip(String msg) {
        if( videoListAdapter.getPageBean().isRefresh()) {
            if(videoListAdapter.getSize()<=0) {
                loadedTip.setLoadingTip(LoadingTip.LoadStatus.error);
                loadedTip.setTips(msg);
                irc.setRefreshing(false);
            }
        }else{
            irc.setLoadMoreStatus(LoadMoreFooterView.Status.ERROR);
        }
    }

}
