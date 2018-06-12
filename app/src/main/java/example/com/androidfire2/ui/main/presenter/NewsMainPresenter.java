package example.com.androidfire2.ui.main.presenter;

import java.util.List;

import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.NewsChannelTable;
import example.com.androidfire2.ui.main.contract.NewsMainContract;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by asus-pc on 2017/9/22.
 */

public class NewsMainPresenter extends NewsMainContract.Presenter {
    @Override
    public void onStart() {
        super.onStart();
        //监听新闻频道变化刷新频道on
        mRxManage.on(AppConstant.NEWS_CHANNEL_CHANGED, new Action1<List<NewsChannelTable>>() {

            @Override
            public void call(List<NewsChannelTable> newsChannelTables) {
                if(newsChannelTables!=null){
                    mView.returnMineNewsChannels(newsChannelTables);
                }
            }
        });
    }

    @Override
    public void lodeMineChannelsRequest() {
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

    }
}
