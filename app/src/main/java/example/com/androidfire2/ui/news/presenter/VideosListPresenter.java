package example.com.androidfire2.ui.news.presenter;

import com.jaydenxiao.common.baserx.RxSubscriber;

import java.util.List;

import example.com.androidfire2.R;
import example.com.androidfire2.app.AppConstant;
import example.com.androidfire2.bean.VideoData;
import example.com.androidfire2.ui.news.contract.VideosListContract;
import rx.functions.Action1;

/**
 * Created by asus-pc on 2017/10/23.
 */

public class VideosListPresenter extends VideosListContract.Presenter{
    @Override
    public void onStart() {
        super.onStart();
        //监听返回顶部动作
        mRxManage.on(AppConstant.NEWS_LIST_TO_TOP, new Action1<Object>() {
            @Override
            public void call(Object o) {
                mView.scrolltoTop();
            }
        });
    }

    @Override
    public void getVideosListDataRequest(String type, int startPage) {
        //调用mRxManger.add()方法 将创建的Observable添加进去,subscribe建立连接,创建一个下游 Observer接收事件
        mRxManage.add(mModel.getVideosListData(type,startPage).subscribe(new RxSubscriber<List<VideoData>>(mContext,false) {
            @Override
            public void onStart() {
                super.onStart();
                mView.showLoading(mContext.getString(R.string.loading));
            }

            @Override
            protected void _onNext(List<VideoData> videoDatas) {
                mView.returnVideosListData(videoDatas);
                mView.stopLoading();
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);

            }
        }));

    }
}
