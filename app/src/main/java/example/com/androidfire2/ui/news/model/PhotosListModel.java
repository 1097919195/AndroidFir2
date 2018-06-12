package example.com.androidfire2.ui.news.model;

import com.jaydenxiao.common.baserx.RxSchedulers;

import java.util.List;

import example.com.androidfire2.api.Api;
import example.com.androidfire2.api.HostType;
import example.com.androidfire2.bean.GirlData;
import example.com.androidfire2.bean.PhotoGirl;
import example.com.androidfire2.ui.news.contract.PhotosListContract;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by asus-pc on 2017/10/17.
 */

public class PhotosListModel implements PhotosListContract.Model{
    @Override
    public Observable <List<PhotoGirl>> getPhotosListData(int size, int page) {
        return Api.getDefault(HostType.GANK_GIRL_PHOTO)
                .getPhotoList(Api.getCacheControl(),size,page)
                .map(new Func1<GirlData, List<PhotoGirl>>() {
                    @Override
                    public List<PhotoGirl> call(GirlData girlData) {
                        return girlData.getResults();
                    }
                })
                .compose(RxSchedulers.<List<PhotoGirl>>io_main());
    }
}
