package example.com.androidfire2.ui.main.contract;

import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import example.com.androidfire2.bean.NewsChannelTable;
import rx.Observable;

/**
 * Created by asus-pc on 2017/9/22.
 */

public interface NewsMainContract {

    //请求获取的数据
    interface Model extends BaseModel {
        Observable<List<NewsChannelTable>> lodeMineNewsChannels();
    }

    //返回获取的数据
    interface View extends BaseView {
        void returnMineNewsChannels(List<NewsChannelTable> newsChannelsMine);
    }

    //发起数据请求
    abstract static class Presenter extends BasePresenter<View, Model> {
        public abstract void lodeMineChannelsRequest();
    }
}
