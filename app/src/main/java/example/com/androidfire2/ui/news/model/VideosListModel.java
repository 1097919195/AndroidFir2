package example.com.androidfire2.ui.news.model;

import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.TimeUtil;

import java.util.List;
import java.util.Map;

import example.com.androidfire2.api.Api;
import example.com.androidfire2.api.HostType;
import example.com.androidfire2.bean.VideoData;
import example.com.androidfire2.ui.news.contract.VideosListContract;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by asus-pc on 2017/10/23.
 */

public class VideosListModel implements VideosListContract.Model{
    @Override
    public Observable<List<VideoData>> getVideosListData(final String type, int startPage) {
        return Api.getDefault(HostType.NETEASE_NEWS_VIDEO)
                .getVideoList(Api.getCacheControl(),type,startPage)
                //flatMap和map操作符很相像，flatMap发送的是合并后的Observables，map操作符发送的是应用函数后返回的结果集
                .flatMap(new Func1<Map<String, List<VideoData>>, Observable<VideoData>>() {
                    @Override
                    public Observable<VideoData> call(Map<String, List<VideoData>> map) {
                        return Observable.from(map.get(type));
                    }
                })
                //事件对象的直接变换，转化时间
                .map(new Func1<VideoData, VideoData>() {
                    @Override
                    public VideoData call(VideoData videoData) {
                        String ptime = TimeUtil.formatDate(videoData.getPtime());
                        videoData.setPtime(ptime);
                        return videoData;
                    }
                })
                .distinct()//去重（去掉重复的项）
                //toSortList 类似于toList ,不同的 他能实现 对 列表进行排序 , 默认是自然升序, 数据项必须实现Comparable接口
                .toSortedList(new Func2<VideoData, VideoData, Integer>() {
                    @Override
                    public Integer call(VideoData videoData, VideoData videoData2) {
                        return videoData2.getPtime().compareTo(videoData.getPtime());
                    }
                })
                //声明线程调度
                .compose(RxSchedulers.<List<VideoData>>io_main());
    }
}
