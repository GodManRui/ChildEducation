package com.gerryrun.childeducation.piano.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class QuestionLife implements Serializable {

    /**
     * code : 1
     * data : [{"title":"这是什么声音","voice":"http://dp.ink520.cn/Admin/upload/voice/155132954782765.wav","right":"41","right_pic":"http://dp.ink520.cn/Admin/upload/right_pic/155124425243149.png","right_file":"155124425243149.png","voice_file":"155132954782765.wav","choose":{"43":"http://dp.ink520.cn/Admin/upload/2019-02-28/15513292312896089.png","41":"http://dp.ink520.cn/Admin/upload/2019-02-28/15513292316199456.png","42":"http://dp.ink520.cn/Admin/upload/2019-02-28/15513292311126184.png"},"choose_file":{"43":"15513292312896089.png","41":"15513292316199456.png","42":"15513292311126184.png"}}]
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
         * title : 这是什么声音
         * voice : http://dp.ink520.cn/Admin/upload/voice/155132954782765.wav
         * right : 41
         * right_pic : http://dp.ink520.cn/Admin/upload/right_pic/155124425243149.png
         * right_file : 155124425243149.png
         * voice_file : 155132954782765.wav
         * choose : {"43":"http://dp.ink520.cn/Admin/upload/2019-02-28/15513292312896089.png","41":"http://dp.ink520.cn/Admin/upload/2019-02-28/15513292316199456.png","42":"http://dp.ink520.cn/Admin/upload/2019-02-28/15513292311126184.png"}
         * choose_file : {"43":"15513292312896089.png","41":"15513292316199456.png","42":"15513292311126184.png"}
         */

        private String title;
        private String voice;
        private String right;
        private String right_pic;
        private String right_file;
        private String voice_file;
        private HashMap<String, String> choose;
        private HashMap<String, String> choose_file;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVoice() {
            return voice;
        }

        public void setVoice(String voice) {
            this.voice = voice;
        }

        public String getRight() {
            return right;
        }

        public void setRight(String right) {
            this.right = right;
        }

        public String getRight_pic() {
            return right_pic;
        }

        public void setRight_pic(String right_pic) {
            this.right_pic = right_pic;
        }

        public String getRight_file() {
            return right_file;
        }

        public void setRight_file(String right_file) {
            this.right_file = right_file;
        }

        public String getVoice_file() {
            return voice_file;
        }

        public void setVoice_file(String voice_file) {
            this.voice_file = voice_file;
        }

        public HashMap<String, String> getChoose() {
            return choose;
        }

        public void setChoose(HashMap<String, String> choose) {
            this.choose = choose;
        }

        public HashMap<String, String> getChoose_file() {
            return choose_file;
        }

        public void setChoose_file(HashMap<String, String> choose_file) {
            this.choose_file = choose_file;
        }
    }

}
