package example.com.androidfire2.ui.news.contract;

import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.ArrayList;
import java.util.List;

import example.com.androidfire2.bean.NewsChannelTable;
import rx.Observable;

/**
 * Created by asus-pc on 2017/9/25.
 */

public interface NewsChannelContract {

    //请求获取的数据
    interface Model extends BaseModel {
        Observable<List<NewsChannelTable>> lodeMineNewsChannels();

        Observable<List<NewsChannelTable>> lodeMoreNewsChannels();

        Observable<String> swapDb(ArrayList<NewsChannelTable> newsChannelTableList, int fromPosition, int toPosition);

        Observable<String> updateDb(ArrayList<NewsChannelTable> mineChannelTableList, ArrayList<NewsChannelTable> moreChannelTableList);
    }

    //返回获取的数据
    interface View extends BaseView {
        void returnMineNewsChannels(List<NewsChannelTable> newsChannelsMine);

        void returnMoreNewsChannels(List<NewsChannelTable> newsChannelsMore);
    }

    //发起数据请求
    abstract static class Presenter extends BasePresenter<View, Model> {
        public abstract void lodeChannelsRequest();

        public abstract void onItemSwap(ArrayList<NewsChannelTable> newsChannelTableList, int fromPosition, final int toPosition);

        //适配器设置点击事件监听，persenter中Rxbus监听处理returnMineNewsChannels
        public abstract void onItemAddOrRemove(ArrayList<NewsChannelTable> mineChannelTableList, ArrayList<NewsChannelTable> moreChannelTableList);

    }
}
