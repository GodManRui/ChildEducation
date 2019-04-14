package com.gerryrun.childeducation.piano.bean;

import java.io.Serializable;
import java.util.List;

public class SelectSong {

    /**
     * code : 1
     * data : [{"name":"粉刷匠","url":"http://dp.ink520.cn/Admin/upload/url/155445735265016.mid",
     * "gift":"http://dp.ink520.cn/Admin/upload/gift/155445735228253.mp4","gift_name":"155445735228253.mp4",
     * "url_name":"155445735265016.mid"},{"name":"摇滚","url":"http://dp.ink520.cn/Admin/upload/url/155445745142992.mid",
     * "gift":"http://dp.ink520.cn/Admin/upload/gift/155445745183614.mp4","gift_name":"155445745183614.mp4",
     * "url_name":"155445745142992.mid"}]
     */

    private int code;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * name : 粉刷匠
         * url : http://dp.ink520.cn/Admin/upload/url/155445735265016.mid
         * gift : http://dp.ink520.cn/Admin/upload/gift/155445735228253.mp4
         * gift_name : 155445735228253.mp4
         * url_name : 155445735265016.mid
         */

        private String name;
        private String url;
        private String gift;
        private String gift_name;
        private String url_name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getGift() {
            return gift;
        }

        public void setGift(String gift) {
            this.gift = gift;
        }

        public String getGift_name() {
            return gift_name;
        }

        public void setGift_name(String gift_name) {
            this.gift_name = gift_name;
        }

        public String getUrl_name() {
            return url_name;
        }

        public void setUrl_name(String url_name) {
            this.url_name = url_name;
        }
    }
}
