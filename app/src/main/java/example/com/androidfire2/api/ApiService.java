package example.com.androidfire2.api;

import java.util.List;
import java.util.Map;

import example.com.androidfire2.bean.GirlData;
import example.com.androidfire2.bean.NewsDetail;
import example.com.androidfire2.bean.NewsSummary;
import example.com.androidfire2.bean.VideoData;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by asus-pc on 2017/9/22.
 */

public interface ApiService {

    //新闻列表
    //http://c.m.163.com/nc/article/headline/T1348647909107/0-20.html
    @GET("nc/article/{type}/{id}/{startPage}-20.html")
    Observable<Map<String, List<NewsSummary>>> getNewsList(
            @Header("Cache-Control") String cacheControl,
            @Path("type") String type,
            @Path("id") String id,
            @Path("startPage") int startPage
    );

    //新闻详情
    @GET("nc/article/{postId}/full.html")
    Observable<Map<String, NewsDetail>> getNewDetail(
            @Header("Cache-Control") String cacheControl,//添加响应头（缓存的方式）
            @Path("postId") String postId
    );

    //图片
    @GET("data/福利/{size}/{page}")
    Observable<GirlData> getPhotoList(
            @Header("Cache-Control") String cacheControl,
            @Path("size") int size,
            @Path("page") int page);

    //视频
    @GET("nc/video/list/{type}/n/{startPage}-10.html")
    Observable<Map<String,List<VideoData>>> getVideoList(
            @Header("Cache-Control") String cacheControl,
            @Path("type") String type,
            @Path("startPage") int startPage);

    //显示新闻详情时的图片加载
    @GET
    Observable<ResponseBody> getNewsBodyHtmlPhoto(
            @Header("Cache-Control") String cacheControl,
            @Url String photoPath);
    //@Url，它允许我们直接传入一个请求的URL。这样以来我们可以将上一个请求的获得的url直接传入进来，baseUrl将被无视
    //baseUrl 需要符合标准，为空、""、或不合法将会报错
}
