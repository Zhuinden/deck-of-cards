package com.taccardi.zak.library.pojo;

/**
 * The suit of a [Card]. ex: Hearts
 *
 * @property stringDef human readable string definition of this enum
 * @property intDef integer definition of this enum. Useful for storing in a database
 * @property symbol unicode symbol for suit shape
 */
public enum Suit {
    HEARTS("hearts", 1, "♥"),
    SPADES("spades", 2, "♠"),
    CLUBS("clubs", 3, "♣"),
    DIAMONDS("diamonds", 4, "♦");

    private final String stringDef;
    private final int intDef;
    private final String symbol;

    private Suit(String stringDef, int intDef, String symbol) {
        this.stringDef = stringDef;
        this.intDef = intDef;
        this.symbol = symbol;
    }

    public String getStringDef() {
        return stringDef;
    }

    public int getIntDef() {
        return intDef;
    }

    public String getSymbol() {
        return symbol;
    }

    public static int count() {
        return values().length;
    }

    @Override
    public String toString() {
        return "Suit{" + "stringDef='" + stringDef + '\'' + ", intDef=" + intDef + ", symbol='" + symbol + '\'' + '}';
    }
}