package com.gerryrun.childeducation.piano.bean;

import java.util.List;

public class SongList {

    /**
     * code : 1
     * data : [{"name":"律动","url":"http://dp.ink520.cn/Admin/upload/url/1555234201248.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155237365078418.png","gift_name":"155237365078418.png","url_name":"1555234201248.mid"},{"name":"小星星2","url":"http://dp.ink520.cn/Admin/upload/url/155524957451659.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155524957496019.png","gift_name":"155524957496019.png","url_name":"155524957451659.mid"},{"name":"小星星3","url":"http://dp.ink520.cn/Admin/upload/url/155525068933901.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/15552506893990.png","gift_name":"15552506893990.png","url_name":"155525068933901.mid"},{"name":"小星星4","url":"http://dp.ink520.cn/Admin/upload/url/155525070267630.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525070255164.png","gift_name":"155525070255164.png","url_name":"155525070267630.mid"},{"name":"小星星5","url":"http://dp.ink520.cn/Admin/upload/url/155525071478195.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525071464604.png","gift_name":"155525071464604.png","url_name":"155525071478195.mid"},{"name":"小星星6","url":"http://dp.ink520.cn/Admin/upload/url/155525072614875.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525072663731.png","gift_name":"155525072663731.png","url_name":"155525072614875.mid"},{"name":"小星星7","url":"http://dp.ink520.cn/Admin/upload/url/155525074715336.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525074777155.png","gift_name":"155525074777155.png","url_name":"155525074715336.mid"},{"name":"小星星8","url":"http://dp.ink520.cn/Admin/upload/url/155525081960700.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525081933210.png","gift_name":"155525081933210.png","url_name":"155525081960700.mid"},{"name":"小星星9","url":"http://dp.ink520.cn/Admin/upload/url/155525082974083.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525082910749.png","gift_name":"155525082910749.png","url_name":"155525082974083.mid"},{"name":"小星星11","url":"http://dp.ink520.cn/Admin/upload/url/155525083921682.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525083988624.png","gift_name":"155525083988624.png","url_name":"155525083921682.mid"},{"name":"小星星12","url":"http://dp.ink520.cn/Admin/upload/url/155525085077132.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525085034899.png","gift_name":"155525085034899.png","url_name":"155525085077132.mid"},{"name":"小星星13","url":"http://dp.ink520.cn/Admin/upload/url/155525086017278.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525086041475.png","gift_name":"155525086041475.png","url_name":"155525086017278.mid"},{"name":"小星星14","url":"http://dp.ink520.cn/Admin/upload/url/15552508708617.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525087088049.png","gift_name":"155525087088049.png","url_name":"15552508708617.mid"},{"name":"小星星15","url":"http://dp.ink520.cn/Admin/upload/url/155525088066460.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525088056272.png","gift_name":"155525088056272.png","url_name":"155525088066460.mid"},{"name":"小星星16","url":"http://dp.ink520.cn/Admin/upload/url/15552509217417.mid","gift":"http://dp.ink520.cn/Admin/upload/gift/155525092186740.png","gift_name":"155525092186740.png","url_name":"15552509217417.mid"}]
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

    public static class DataBean {
        /**
         * name : 律动
         * url : http://dp.ink520.cn/Admin/upload/url/1555234201248.mid
         * gift : http://dp.ink520.cn/Admin/upload/gift/155237365078418.png
         * gift_name : 155237365078418.png
         * url_name : 1555234201248.mid
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
