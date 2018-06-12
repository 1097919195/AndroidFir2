package example.com.androidfire2.bean;

/**
 * Created by asus-pc on 2017/10/23.
 */

public class VideosChannelTable {
    private String channelId;
    private String channelName;

    public VideosChannelTable(String channelId, String channelName) {
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
