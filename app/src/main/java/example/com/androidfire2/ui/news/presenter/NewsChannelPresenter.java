package example.com.androidfire2.ui.news.presenter;

import com.jaydenxiao.common.baserx.RxSubscriber;

import java.util.ArrayList;
import java.util.List;

import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.NewsChannelTable;
import example.com.androidfire2.ui.news.contract.NewsChannelContract;
import rx.Subscriber;

/**
 * Created by asus-pc on 2017/9/25.
 */

public class NewsChannelPresenter extends NewsChannelContract.Presenter {
    @Override
    public void lodeChannelsRequest() {
        //我的频道
        //调用mRxManger.add()方法 将创建的Observable添加进去,subscribe建立连接,创建一个下游 Observer接收事件(除了 Observer 接口之外，RxJava 还内置了一个实现了 Observer 的抽象类：Subscriber。 Subscriber 对 Observer 接口进行了一些扩展，但他们的基本使用方式是完全一样的。)
        //当调用了subscribe方法 XXmodel中Observable.OnSubscribe的call方法就会被执行
        mRxManage.add(mModel.lodeMineNewsChannels().subscribe(new Subscriber<List<NewsChannelTable>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<NewsChannelTable> newsChannelTables) {
                mView.returnMineNewsChannels(newsChannelTables);

            }
        }));
        //更多频道
        mRxManage.add(mModel.lodeMoreNewsChannels().subscribe(new Subscriber<List<NewsChannelTable>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<NewsChannelTable> newsChannelTables) {
                mView.returnMoreNewsChannels(newsChannelTables);

            }
        }));

    }

    @Override
    public void onItemSwap(final ArrayList<NewsChannelTable> newsChannelTableList, int fromPosition, int toPosition) {
        mRxManage.add(mModel.swapDb(newsChannelTableList,fromPosition,toPosition).subscribe(new RxSubscriber<String>(mContext,false) {
            @Override
            protected void _onNext(String s) {
                mRxManage.post(AppConstant.NEWS_CHANNEL_CHANGED,newsChannelTableList);
            }

            @Override
            protected void _onError(String message) {

            }
        }));

    }

    @Override
    public void onItemAddOrRemove(final ArrayList<NewsChannelTable> mineChannelTableList, ArrayList<NewsChannelTable> moreChannelTableList) {
        mRxManage.add(mModel.updateDb(mineChannelTableList,moreChannelTableList).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                mRxManage.post(AppConstant.NEWS_CHANNEL_CHANGED,mineChannelTableList);

            }
        }));
    }
}
