package com.anready.croissanttestjava.adapter;

public class DirectoryContents {
    public final String name;
    public final boolean isDirectory;
    public final boolean isHidden;

    public DirectoryContents(String s, boolean b, boolean b1) {
        this.name = s;
        this.isDirectory = b;
        this.isHidden = b1;
    }
}
