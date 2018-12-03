package example.com.androidfire2.ui.news.model;

import com.google.gson.Gson;
import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.TimeUtil;

import java.util.List;
import java.util.Map;

import example.com.androidfire2.api.Api;
import example.com.androidfire2.api.ApiConstants;
import example.com.androidfire2.api.HostType;
import example.com.androidfire2.bean.NewsSummary;
import example.com.androidfire2.ui.news.contract.NewsListContract;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by asus-pc on 2017/9/26.
 */

public class NewsListModel implements NewsListContract.Model {
    @Override
    public Observable<List<NewsSummary>> getNewsListData(String type, final String id, int startPage) {
        return Api.getDefault(HostType.NETEASE_NEWS_VIDEO)
                .getNewsList(Api.getCacheControl(), type, id, startPage)
                //flatMap和map操作符很相像，flatMap发送的是合并后的Observables，map操作符发送的是应用函数后返回的结果集(要求是一对多的转化，一个头条类下面有很多条的新闻)
                .flatMap(new Func1<Map<String, List<NewsSummary>>, Observable<NewsSummary>>() {
                    @Override
                    public Observable<NewsSummary> call(Map<String, List<NewsSummary>> map) {
                        LogUtils.loge(map.keySet().toString());//打印key即对应的id
                        LogUtils.loge(new Gson().toJson(map));
                        if (id.endsWith(ApiConstants.HOUSE_ID)) {
                            LogUtils.loge(new Gson().toJson(map));
                            // 房产实际上针对地区的 它的id与返回key不同
                            return Observable.from(map.get("北京"));//form依次发送 map.get("北京")  这里是房产的话返回的key是北京，所以一定要flatmap一下
                        }
                        return Observable.from(map.get(id));//form依次发送 map.get(id)
                    }
                })
                //转化时间(.map()事件 对象的直接变换。所谓变换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列。)（一对一）
//                .map(new Func1<NewsSummary, NewsSummary>() {
//                    @Override
//                    public NewsSummary call(NewsSummary newsSummary) {
//                        String ptime = TimeUtil.formatDate(newsSummary.getPtime());
//                        newsSummary.setPtime(ptime);
//                        return newsSummary;
//                    }
//                })
                .distinct()//去重(删除重复项)
                //toSortList 类似于toList ,不同的 他能实现 对 列表进行排序 , 默认是自然升序, 数据项必须实现Comparable接口   最后以多个NewsSummary即List<NewsSummary>
                .toSortedList(new Func2<NewsSummary, NewsSummary, Integer>() {
                    @Override
                    public Integer call(NewsSummary newsSummary, NewsSummary newsSummary2) {
                        //compareTo如果指定的数与参数相等返回0;如果指定的数小于参数返回 -1;如果指定的数大于参数返回 1;
                        return newsSummary2.getPtime().compareTo(newsSummary.getPtime());//>0 升序 ，<0 降序
                    }
                })
                //声明线程调度
                .compose(RxSchedulers.<List<NewsSummary>>io_main());
    }

}
