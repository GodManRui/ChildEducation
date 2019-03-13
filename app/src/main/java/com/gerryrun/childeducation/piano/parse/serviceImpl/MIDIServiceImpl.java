package com.gerryrun.childeducation.piano.parse.serviceImpl;


import com.gerryrun.childeducation.piano.parse.service.MIDIService;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MIDIServiceImpl implements MIDIService {

    private String MThd = "4d546864";           //头块标记
    private String MTrk = "4d54726b";           //轨道块标记
    private final ArrayList<String> musicalName;

    public MIDIServiceImpl() {
        musicalName = new ArrayList<String>();
        musicalName.add("C");
        musicalName.add("#C");
        musicalName.add("D");
        musicalName.add("#D");
        musicalName.add("E");
        musicalName.add("F");
        musicalName.add("#F");
        musicalName.add("G");
        musicalName.add("#G");
        musicalName.add("A");
        musicalName.add("#A");
        musicalName.add("B");
    }

    public ArrayList<String> getSequence(String path, InputStream inputStreamRaw) {
        ArrayList<String> store = new ArrayList<String>();
        try {
            InputStream inputStream = null;
            if (path != null)
                inputStream = new FileInputStream(path);
            else if (inputStreamRaw != null) inputStream = inputStreamRaw;
            if (inputStream == null) return null;
            int res;
            while ((res = inputStream.read()) != -1) {
                String tmp = Integer.toHexString(res);
                if (tmp.length() == 1)
                    tmp = "0" + tmp;
                store.add(tmp);
            }
            System.out.print("文件内容: ");
            for (String str : store) {
                System.out.print(str + " ");
            }
            System.out.println();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //String fileType = store.get(8) + store.get(9);
        //int orbitNum = Integer.parseInt(store.get(10)+store.get(11),16);
        //int deltaTime = Integer.parseInt(store.get(12)+store.get(13),16);
        return store;
    }

    public ArrayList<ArrayList<String>> getTracks(ArrayList<String> store) {
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        for (int i = 14; i < store.size() - 4; i++) {
            if ((store.get(i) + store.get(i + 1) + store.get(i + 2) + store.get(i + 3)).equals(MTrk)) {
                int chunkLen = Integer.parseInt(store.get(i + 4) + store.get(i + 5) + store.get(i + 6) + store.get(i + 7), 16);
                i += 8;
                ArrayList<String> oneChunk = new ArrayList<String>();
                int count = 0;
                while (i < store.size() && count < chunkLen) {
                    oneChunk.add(store.get(i));
                    i++;
                    count++;
                }
                i--;
                res.add(oneChunk);
            }
        }
        return res;
    }

    public ArrayList<Integer> getDeltaTime(ArrayList<String> event) {
        ArrayList<String> store = new ArrayList<String>();

        for (int i = 0; i < event.size(); i++) {
            if (Integer.valueOf(event.get(i).charAt(0) + "", 16) < 8) {
                store.add(event.get(i));
                break;
            } else
                store.add(event.get(i));
        }
        int len = store.size();
        ArrayList<Integer> res = new ArrayList<Integer>();
        res.add(len);
        int res2 = 0;
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                int coefficient = Integer.valueOf(store.get(i), 16) - 128;
                if (coefficient < 0)
                    coefficient += 128;
                res2 += coefficient * Math.pow(128, len - i - 1);
            }
        }
        res.add(res2);
        return res;
    }

    public String getMusicalNote(String note) {
        int num = Integer.valueOf(note, 16);

        int pos = num % 12;
        int musicalScale = num / 12 - 1;
        String res = musicalName.get(pos);
        res += musicalScale;
        return res;
    }

    public ArrayList<String> getEventLen(String command, String lastCommand, int offset, ArrayList<String> leftEvents) {
        char leftNybble = command.charAt(0);
        ArrayList<String> res = new ArrayList<String>();
        if (leftNybble == '8') {
            res.add("2");
            String str = "音符关闭: " + getMusicalNote(leftEvents.get(offset + 1)) + "; 力度: " + leftEvents.get(offset + 2);
            res.add(str);
            System.out.println(str);
        } else if (leftNybble == '9') {
            res.add("2");
            String str;
            int vv = Integer.valueOf(leftEvents.get(offset + 2), 16);
            if (vv == 0)
                str = "音符关闭: " + getMusicalNote(leftEvents.get(offset + 1)) + "; 力度: " + leftEvents.get(offset + 2);
            else
                str = "音符打开: " + getMusicalNote(leftEvents.get(offset + 1)) + "; 力度: " + getPressStrength(leftEvents.get(offset + 2));
            System.out.println(str);
            res.add(str);
        } else if (leftNybble == 'a') {
            res.add("2");
            String str = "触后音符: " + getMusicalNote(leftEvents.get(offset + 1)) + "; 力度: " + getPressStrength(leftEvents.get(offset + 2));
            System.out.println(str);
            res.add(str);
        } else if (leftNybble == 'b') {
            res.add("2");
            String str = "调换控制, 控制号: " + leftEvents.get(offset + 1) + "; 新值: " + leftEvents.get(offset + 2);
            System.out.println(str);
        } else if (leftNybble == 'c') {
            res.add("1");
            System.out.println("改变程序, 新的程序号: " + leftEvents.get(offset + 1));
        } else if (leftNybble == 'd') {
            res.add("1");
            System.out.println("在通道后接触, 管道号: " + leftEvents.get(offset + 1));
        } else if (leftNybble == 'e') {
            res.add("2");
            String str = "滑音, 音高低位: " + leftEvents.get(offset + 1) + "; 音高高位: " + leftEvents.get(offset + 2);
            System.out.println(str);
        } else if (command.equals("ff")) {
            System.out.println("Meta事件的类型: " + leftEvents.get(offset + 1));
            int metaDataLen = Integer.valueOf(leftEvents.get(offset + 2), 16);
            res.add((2 + metaDataLen) + "");
        } else if (command.equals("f0")) {
            int count = 1;
            while (offset + count < leftEvents.size() && !leftEvents.get(offset + count).equals("f7"))
                count++;
            res.add(count + "");
            System.out.println("系统码事件");
        } else if (Integer.valueOf(command, 16) >= 0 && Integer.valueOf(command, 16) <= 127 && !lastCommand.equals("")) {
            res = getEventLen(lastCommand, lastCommand, offset - 1, leftEvents);
        } else {
            res.add("0");
            System.out.println(command + " not found!");
        }
        return res;
    }

    private String getPressStrength(String strength) {
        int num = Integer.valueOf(strength, 16);
        String res = num + "(";
        if (num == 0)
            res = "无(松开音符)";
        else if (num >= 1 && num <= 15)
            res += "极弱";
        else if (num >= 16 && num <= 31)
            res += "很弱";
        else if (num >= 32 && num <= 47)
            res += "弱";
        else if (num >= 48 && num <= 63)
            res += "中等偏弱";
        else if (num >= 64 && num <= 79)
            res += "中等偏强";
        else if (num >= 80 && num <= 96)
            res += "强";
        else if (num >= 97 && num <= 111)
            res += "很强";
        else if (num >= 112 && num <= 127)
            res += "极强";
        res += ")";
        return res;
    }
}
