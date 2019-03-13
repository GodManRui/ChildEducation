package com.gerryrun.childeducation.piano.parse;

public class Test {
    public static void main(String[] args) {
        String path = "D:\\AndroidStudio\\Project\\ChildEducation\\app\\src\\main\\res\\raw\\jiequ.mid";
        ReadMIDI readMIDI = new ReadMIDI();
        readMIDI.myRead(path, null);
        /*String ss = "<item>@drawable/music_9_%s</item>";
        for (int i = 0; i < 56; i++) {
            System.out.println(String.format(ss, i + 1 + ""));
        }*/
    }
}
