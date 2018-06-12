package example.com.androidfire2.ui.news.contract;

import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import example.com.androidfire2.bean.VideoData;
import rx.Observable;

/**
 * Created by asus-pc on 2017/10/23.
 */

public interface VideosListContract {
    //请求获取的数据
    interface Model extends BaseModel {
        Observable<List<VideoData>> getVideosListData(String type, int startPage);
    }

    //返回获取的数据
    interface View extends BaseView {
        //返回获取的视频
        void returnVideosListData(List<VideoData> newsSummaries);
        //返回顶部
        void scrolltoTop();
    }

    //发起数据请求
    abstract static class Presenter extends BasePresenter<View, Model> {
        public abstract void getVideosListDataRequest(String type, int startPage);
    }

}
