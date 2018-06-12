package example.com.androidfire2.ui.news.model;

import com.jaydenxiao.common.baserx.RxSchedulers;

import java.util.List;
import java.util.Map;

import example.com.androidfire2.api.Api;
import example.com.androidfire2.api.HostType;
import example.com.androidfire2.bean.NewsDetail;
import example.com.androidfire2.ui.news.contract.NewsDetailContract;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by asus-pc on 2017/9/29.
 */

public class NewsDetailModel implements NewsDetailContract.Model {
    @Override
    public Observable<NewsDetail> getOneNewsData(final String postId) {
        return Api.getDefault(HostType.NETEASE_NEWS_VIDEO)
                .getNewDetail(Api.getCacheControl(), postId)
                .map(new Func1<Map<String, NewsDetail>, NewsDetail>() {
                    @Override
                    public NewsDetail call(Map<String, NewsDetail> stringNewsDetailMap) {
                        NewsDetail newsDetail = stringNewsDetailMap.get(postId);
                        changeNewsDetail(newsDetail);
                        return newsDetail;
                    }
                })
                .compose(RxSchedulers.<NewsDetail>io_main());
    }
    private void changeNewsDetail(NewsDetail newsDetail) {
        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
        //当图片为2张以上时，内容改变
        if (isChange(imgSrcs)) {
            String newsBody = newsDetail.getBody();
            newsBody = changeNewsBody(imgSrcs, newsBody);
            newsDetail.setBody(newsBody);
        }
    }

    private boolean isChange(List<NewsDetail.ImgBean> imgSrcs) {
        return imgSrcs != null && imgSrcs.size() >= 2;
    }

    private String changeNewsBody(List<NewsDetail.ImgBean> imgSrcs, String newsBody) {
        for (int i = 0; i < imgSrcs.size(); i++) {
            String oldChars = "<!--IMG#" + i + "-->";
            String newChars;
            if (i == 0) {
                newChars = "";
            } else {
                newChars = "<img src=\"" + imgSrcs.get(i).getSrc() + "\" />";
            }
            newsBody = newsBody.replace(oldChars, newChars);

        }
        return newsBody;
    }
}
