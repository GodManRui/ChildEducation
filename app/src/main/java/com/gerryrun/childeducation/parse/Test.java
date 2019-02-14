package com.gerryrun.childeducation.parse;

public class Test {
    public static void main(String[] args) {
        String path = "D:\\AndroidStudio\\Project\\ChildEducation\\app\\src\\main\\res\\raw\\small_start.mid ";
        ReadMIDI readMIDI = new ReadMIDI();
        readMIDI.myRead(path, null);
        String ss = " <item>@drawable/learn_the_background_of_nursery_rhy1_%s</item>";
        for (int i = 0; i < 164; i++) {
            System.out.println(String.format(ss, i + 1 + ""));
        }
    }
}
