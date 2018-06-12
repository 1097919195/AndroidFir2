package example.com.androidfire2.db;
/**
 * db数据库包，新闻标签和视频标签都是用数组存储的
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.com.androidfire2.R;
import example.com.androidfire2.api.ApiConstants;
import example.com.androidfire2.app.AppApplication;
import example.com.androidfire2.bean.NewsChannelTable;

public class NewsChannelTableManager {
    /**
     * 加载新闻类型
     * @return
     */
    //当使用asList()方法时，数组就和列表链接在一起了.更新其中之一时，另一个将自动获得更新。注意:仅仅针对对象数组类型,基本数据类型数组不具备该特性 asList得到的数组是没有add和remove方法的
    public static List<NewsChannelTable> loadNewsChannelsMine() {
        List<String> channelName = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.news_channel_name));
        List<String> channelId = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.news_channel_id));
        ArrayList<NewsChannelTable> newsChannelTables = new ArrayList<>();
        for (int i = 0; i < channelName.size(); i++) {
            NewsChannelTable entity = new NewsChannelTable(channelName.get(i), channelId.get(i)
                    , ApiConstants.getType(channelId.get(i)), i <= 5, i, false);
            newsChannelTables.add(entity);
        }
        return newsChannelTables;
    }
    /**
     * 加载固定新闻类型
     * @return
     */
    public static List<NewsChannelTable> loadNewsChannelsStatic() {
        //Arrays.asList将数组转化成list对象
        List<String> channelName = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.news_channel_name_static));
        List<String> channelId = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.news_channel_id_static));
        ArrayList<NewsChannelTable> newsChannelTables = new ArrayList<>();
        for (int i = 0; i < channelName.size(); i++) {
            NewsChannelTable entity = new NewsChannelTable(channelName.get(i), channelId.get(i)
                    , ApiConstants.getType(channelId.get(i)), i <= 5, i, true);
            newsChannelTables.add(entity);
        }
        return newsChannelTables;
    }
}
