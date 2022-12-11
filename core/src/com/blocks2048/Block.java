package com.blocks2048;

import java.util.Random;

public class Block {

    private final int[] initNumbers = new int[]{2, 4, 8, 16, 32, 64, 128};
    int num;
    boolean finalPosition;
    int posX;
    int posY;
    boolean forRemove;

    public Block() {
        num = initNumbers[new Random().nextInt(initNumbers.length)];
        posX = 161;
        posY = 520;
        finalPosition = false;
        forRemove = false;
    }

    public String getRegionName() {
        return String.valueOf(num);
    }

    public void add(int factor) {
        for (int i = 0; i < factor; i++) {
            num *= 2;
        }
    }
}
