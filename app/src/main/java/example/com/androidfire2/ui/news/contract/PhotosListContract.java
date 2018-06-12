package example.com.androidfire2.ui.news.contract;

import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import example.com.androidfire2.bean.PhotoGirl;
import rx.Observable;

/**
 * Created by asus-pc on 2017/10/17.
 */

public interface PhotosListContract {
    //请求获取的数据
    interface Model extends BaseModel {
        Observable <List<PhotoGirl>> getPhotosListData(int size, int page);
    }

    //返回的数据
    interface View extends BaseView {
        void returnPhotosListData(List<PhotoGirl> photoGirls);
    }

    //发起获取数据请求
    abstract static class Presenter extends BasePresenter<View, Model> {
        public abstract void getPhotosListDataRequest(int size, int page);
    }

}
