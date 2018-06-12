package example.com.androidfire2.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.com.androidfire2.R;
import example.com.androidfire2.app.AppApplication;
import example.com.androidfire2.bean.VideosChannelTable;

public class VideosChannelTableManager {

    /**
     * 加载视频类型
     * @return
     */
    public static List<VideosChannelTable> loadVideosChannelsMine() {
        List<String> channelName = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.video_channel_name));
        List<String> channelId = Arrays.asList(AppApplication.getAppContext().getResources().getStringArray(R.array.video_channel_id));
        ArrayList<VideosChannelTable>newsChannelTables=new ArrayList<>();
        for (int i = 0; i < channelName.size(); i++) {
            VideosChannelTable entity = new VideosChannelTable(channelId.get(i), channelName.get(i));
            newsChannelTables.add(entity);
        }
        return newsChannelTables;
    }

}
