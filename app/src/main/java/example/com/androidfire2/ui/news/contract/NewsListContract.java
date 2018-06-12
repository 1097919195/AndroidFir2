package example.com.androidfire2.ui.news.contract;

import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import example.com.androidfire2.bean.NewsSummary;
import rx.Observable;

/**
 * Created by asus-pc on 2017/9/26.
 */

public interface NewsListContract {
    //请求获取的数据
    interface Model extends BaseModel {
        Observable<List<NewsSummary>> getNewsListData(String type, final String id, int startPage);
    }

    //返回获取的数据
    interface View extends BaseView {
        void returnNewsListData(List<NewsSummary> newsSummaries);

        void scrolltoTop();
    }

    //发起数据请求
    abstract static class Persenter extends BasePresenter<View, Model> {
        public abstract void getNewsListDataRequest(String type, final String id, int startPage);
    }

}
