package example.com.androidfire2.ui.news.presenter;

import com.jaydenxiao.common.baserx.RxSubscriber;

import java.util.List;

import example.com.androidfire2.R;
import example.com.androidfire2.bean.PhotoGirl;
import example.com.androidfire2.ui.news.contract.PhotosListContract;

/**
 * Created by asus-pc on 2017/10/17.
 */

public class PhotosListPresenter extends PhotosListContract.Presenter{
    @Override
    public void getPhotosListDataRequest(int size, int page) {
        mRxManage.add(mModel.getPhotosListData(size,page).subscribe(new RxSubscriber<List<PhotoGirl>>(mContext,false) {
            @Override
            public void onStart() {
                super.onStart();
                mView.showLoading(mContext.getString(R.string.loading));
            }
            @Override
            protected void _onNext(List<PhotoGirl> photoGirls) {
                mView.returnPhotosListData(photoGirls);
                mView.stopLoading();
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
