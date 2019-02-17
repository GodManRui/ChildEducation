package com.gerryrun.childeducation.parse;

public class Test {
    public static void main(String[] args) {
        /*String path = "D:\\AndroidStudio\\Project\\ChildEducation\\app\\src\\main\\res\\raw\\small_start.mid ";
        ReadMIDI readMIDI = new ReadMIDI();
        readMIDI.myRead(path, null);*/
        String ss = " <item\n" +
                "        android:drawable=\"@drawable/music_orange_%s\"\n" +
                "        android:duration=\"50\" />";
        for (int i = 0; i < 20; i++) {
            System.out.println(String.format(ss, i + 1 + ""));
        }
    }
}
