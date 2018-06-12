package example.com.androidfire2.ui.news.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.aspsine.irecyclerview.animation.ScaleInAnimation;
import com.aspsine.irecyclerview.widget.LoadMoreFooterView;
import com.jaydenxiao.common.base.BaseFragment;
import com.jaydenxiao.common.commonwidget.LoadingTip;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.NewsSummary;
import example.com.androidfire2.ui.news.adapter.NewsListAdapter;
import example.com.androidfire2.ui.news.contract.NewsListContract;
import example.com.androidfire2.ui.news.model.NewsListModel;
import example.com.androidfire2.ui.news.presenter.NewsListPresenter;

/**
 * Created by asus-pc on 2017/9/26.
 */

public class NewsListFragment extends BaseFragment<NewsListPresenter, NewsListModel> implements NewsListContract.View, OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.irc)
    IRecyclerView irc;
    @Bind(R.id.loadedTip)
    LoadingTip loadedTip;

    private NewsListAdapter newsListAdapter;
    private List<NewsSummary> datas = new ArrayList<>();

    private String mNewsId;
    private String mNewsType;
    private int mStartPage=0;//数据条数

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_news_list;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this, mModel);
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            mNewsId = getArguments().getString(AppConstant.NEWS_ID);
            mNewsType = getArguments().getString(AppConstant.NEWS_TYPE);
        }
        //设置布局管理器
        irc.setLayoutManager(new LinearLayoutManager(getContext()));
        datas.clear();
        newsListAdapter = new NewsListAdapter(getContext(), datas);
        newsListAdapter.openLoadAnimation(new ScaleInAnimation());
        //设置adapter
        irc.setAdapter(newsListAdapter);
        irc.setOnRefreshListener(this);
        irc.setOnLoadMoreListener(this);
        //数据为空才重新发起请求
        if(newsListAdapter.getSize()<=0) {
            mStartPage = 0;
            mPresenter.getNewsListDataRequest(mNewsType, mNewsId, mStartPage);
        }
    }


    @Override
    public void returnNewsListData(List<NewsSummary> newsSummaries) {
        if (newsSummaries != null) {
            mStartPage += 20;
            //一次请求数据加载完
            if (newsListAdapter.getPageBean().isRefresh()) {
                irc.setRefreshing(false);//停止刷新
                newsListAdapter.replaceAll(newsSummaries);
            } else {
                if (newsSummaries.size() > 0) {
                    irc.setLoadMoreStatus(LoadMoreFooterView.Status.GONE);
                    newsListAdapter.addAll(newsSummaries);
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
        newsListAdapter.getPageBean().setRefresh(true);
        mStartPage = 0;
        //发起请求
        irc.setRefreshing(true);
        mPresenter.getNewsListDataRequest(mNewsType, mNewsId, mStartPage);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        //数据不为空才能刷新加载
        if (mStartPage > 0) {
            newsListAdapter.getPageBean().setRefresh(false);
            //发起请求
            irc.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING);
            mPresenter.getNewsListDataRequest(mNewsType, mNewsId, mStartPage);

        }

    }

    @Override
    public void showLoading(String title) {
        //无数据时的加载效果
        if( newsListAdapter.getPageBean().isRefresh()) {
            if(newsListAdapter.getSize()<=0) {
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
        if( newsListAdapter.getPageBean().isRefresh()) {
            if(newsListAdapter.getSize()<=0) {
                loadedTip.setLoadingTip(LoadingTip.LoadStatus.error);
                loadedTip.setTips(msg);
            }
            irc.setRefreshing(false);
        }else{
            irc.setLoadMoreStatus(LoadMoreFooterView.Status.ERROR);
        }
    }
}
