package com.taccardi.zak.library.pojo;

/**
 * The rank of a [Card]. ex: Queen
 */
public enum Rank {
    TWO("two", 2),
    THREE("three", 3),
    FOUR("four", 4),
    FIVE("five", 5),
    SIX("six", 6),
    SEVEN("seven", 7),
    EIGHT("eight", 8),
    NINE("nine", 9),
    TEN("ten", 10),
    JACK("jack", 11),
    QUEEN("queen", 12),
    KING("king", 13),
    ACE("ace", 14);

    private final String stringDef;
    private final int intDef;

    public String getStringDef() {
        return stringDef;
    }

    public int getIntDef() {
        return intDef;
    }

    private Rank(String stringDef, int intDef) {
        this.stringDef = stringDef;
        this.intDef = intDef;
    }

    @Override
    public String toString() {
        return stringDef;
    }

    public static int count() {
        return values().length;
    }
}