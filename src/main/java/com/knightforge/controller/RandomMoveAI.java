package com.knightforge.controller;

import com.knightforge.model.Move;

import java.util.List;
import java.util.Random;

public class RandomMoveAI {
    private final Random random;

    public RandomMoveAI() {
        this(new Random());
    }

    public RandomMoveAI(Random random) {
        this.random = random;
    }

    public Move chooseMove(List<Move> legalMoves) {
        if (legalMoves.isEmpty()) {
            return null;
        }
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }
}
