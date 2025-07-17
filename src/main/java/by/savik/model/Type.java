package by.savik.model;

import java.util.Random;

public enum Type {
    CHAIR, ARMCHAIR, COUCH;

    private static final Random RND = new Random();

    public static Type randomType()  {
        Type[] types = values();
        return types[RND.nextInt(types.length)];
    }
}


