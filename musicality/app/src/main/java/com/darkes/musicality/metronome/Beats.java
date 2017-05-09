package com.darkes.musicality.metronome;

public enum Beats{
    one("1"),
    two("2"),
    three("3"),
    four("4"),
    five("5"),
    six("6"),
    seven("7"),
    eight("8"),
    nine("9"),
    ten("10"),
    eleven("11"),
    twelve("12"),
    thirteen("13"),
    fourteen("14"),
    fifteen("15"),
    sixteen("16");

    private String beat;

    Beats(String beat) {
        this.beat = beat;
    }

    @Override public String toString() {
        return beat;
    }

    public short getNum() {
        return Short.parseShort(beat);
    }
}