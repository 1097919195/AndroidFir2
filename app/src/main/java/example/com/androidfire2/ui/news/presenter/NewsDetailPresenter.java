package example.com.androidfire2.ui.news.presenter;

import com.jaydenxiao.common.baserx.RxSubscriber;
import com.jaydenxiao.common.commonutils.ToastUitl;

import example.com.androidfire2.R;
import example.com.androidfire2.bean.NewsDetail;
import example.com.androidfire2.ui.news.contract.NewsDetailContract;

/**
 * 新闻详情
 * Created by asus-pc on 2017/9/29.
 */

public class NewsDetailPresenter extends NewsDetailContract.Presenter {
    @Override
    public void getOneNewsDataRequest(String postId) {
        mRxManage.add(mModel.getOneNewsData(postId).subscribe(new RxSubscriber<NewsDetail>(mContext) {
            @Override
            protected void _onNext(NewsDetail newsDetail) {
                mView.returnOneNewsData(newsDetail);
            }

            @Override
            protected void _onError(String message) {
                ToastUitl.showToastWithImg(message, R.drawable.ic_wrong);
            }
        }));

    }
}
