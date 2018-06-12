package example.com.androidfire2.ui.news.model;

import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.ACache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.com.androidfire2.R;
import example.com.androidfire2.api.ApiConstants;
import example.com.androidfire2.app.AppApplication;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.NewsChannelTable;
import example.com.androidfire2.db.NewsChannelTableManager;
import example.com.androidfire2.ui.news.contract.NewsChannelContract;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by asus-pc on 2017/9/25.
 */

public class NewsChannelModel implements NewsChannelContract.Model{
    @Override
    public Observable<List<NewsChannelTable>> lodeMineNewsChannels() {
        //创建一个上游 Observable(发送事件,当调用了presenter中的subscribe方法,Observable.OnSubscribe的call方法就会被执行，而Observable.OnSubscribe 说白了就是一个继承了Action1接口的接口，Observable.OnSubscribe <- Action1 <- Action <- Function,create()方法也就是个工厂方法)
        return Observable.create(new Observable.OnSubscribe<List<NewsChannelTable>>() {
            @Override
            public void call(Subscriber<? super List<NewsChannelTable>> subscriber) {
                ArrayList<NewsChannelTable> newsChannelTableList = (ArrayList<NewsChannelTable>) ACache.get(AppApplication.getAppContext()).getAsObject(AppConstant.CHANNEL_MINE);
                if(newsChannelTableList==null){
                    newsChannelTableList= (ArrayList<NewsChannelTable>) NewsChannelTableManager.loadNewsChannelsStatic();
                }
                subscriber.onNext(newsChannelTableList);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<NewsChannelTable>>io_main());
    }

    @Override
    public Observable<List<NewsChannelTable>> lodeMoreNewsChannels() {
        return Observable.create(new Observable.OnSubscribe<List<NewsChannelTable>>() {
            @Override
            public void call(Subscriber<? super List<NewsChannelTable>> subscriber) {
                ArrayList<NewsChannelTable> newsChannelTableList = (ArrayList<NewsChannelTable>) ACache.get(AppApplication.getAppContext()).getAsObject(AppConstant.CHANNEL_MORE);
                if(newsChannelTableList==null) {
                    List<String> channelName = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.news_channel_name));
                    List<String> channelId = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.news_channel_id));
                    newsChannelTableList = new ArrayList<>();
                    for (int i = 0; i < channelName.size(); i++) {
                        NewsChannelTable entity = new NewsChannelTable(channelName.get(i), channelId.get(i)
                                , ApiConstants.getType(channelId.get(i)), i <= 5, i, false);
                        newsChannelTableList.add(entity);
                    }
                }
                subscriber.onNext(newsChannelTableList);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<NewsChannelTable>>io_main());
    }

    @Override
    public Observable<String> swapDb(final ArrayList<NewsChannelTable> newsChannelTableList, int fromPosition, int toPosition) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //进行缓存
                ACache.get(AppApplication.getAppContext()).put(AppConstant.CHANNEL_MINE,newsChannelTableList);
                subscriber.onNext("");
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<String>io_main());
    }

    @Override
    public Observable<String> updateDb(final ArrayList<NewsChannelTable> mineChannelTableList, final ArrayList<NewsChannelTable> moreChannelTableList) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                ACache.get(AppApplication.getAppContext()).put(AppConstant.CHANNEL_MINE,mineChannelTableList);
                ACache.get(AppApplication.getAppContext()).put(AppConstant.CHANNEL_MORE,moreChannelTableList);
                subscriber.onNext("");
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<String>io_main());
    }
}
