package example.com.androidfire2.ui.main.model;

import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.ACache;

import java.util.ArrayList;
import java.util.List;

import example.com.androidfire2.app.AppApplication;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.NewsChannelTable;
import example.com.androidfire2.db.NewsChannelTableManager;
import example.com.androidfire2.ui.main.contract.NewsMainContract;
import rx.Observable;
import rx.Subscriber;


/**
 * Created by asus-pc on 2017/9/22.
 */

public class NewsMainModel implements NewsMainContract.Model {
    @Override
    public Observable<List<NewsChannelTable>> lodeMineNewsChannels() {
        //创建一个上游 Observable(发送事件,当调用了presenter中的subscribe方法,Observable.OnSubscribe的call方法就会被执行，而Observable.OnSubscribe 说白了就是一个继承了Action1接口的接口，Observable.OnSubscribe <- Action1 <- Action <- Function,create()方法也就是个工厂方法)
        return Observable.create(new Observable.OnSubscribe<List<NewsChannelTable>>() {
            @Override
            public void call(Subscriber<? super List<NewsChannelTable>> subscriber) {
                //先从缓存中获取，没有就直接loadNewsChannelsStatic并缓存
                ArrayList<NewsChannelTable> newsChannelTablesList = (ArrayList<NewsChannelTable>) ACache.get(AppApplication.getAppContext()).getAsObject(AppConstant.CHANNEL_MINE);
                if (newsChannelTablesList == null) {
                    newsChannelTablesList = (ArrayList<NewsChannelTable>) NewsChannelTableManager.loadNewsChannelsStatic();
                    ACache.get(AppApplication.getAppContext()).put(AppConstant.CHANNEL_MINE,newsChannelTablesList);
                }
                subscriber.onNext(newsChannelTablesList);
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<List<NewsChannelTable>>io_main());
    }
}
