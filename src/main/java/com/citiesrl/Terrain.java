/*
 * CitiesRL Copyright (C) 2018 Klaus Hauschild
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.citiesrl;

import java.util.Random;
import com.rl4j.Backbuffer;
import com.rl4j.Dimension;
import com.rl4j.Draw;
import com.rl4j.Update;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Terrain implements Update, Draw {

    private final Tile[][] tiles;

    private int offsetColumn;
    private int offsetRow;

    public Terrain(final Dimension size) {
        tiles = new Tile[size.getWidth()][size.getHeight()];
        for (int column = 0; column < size.getWidth(); column++) {
            for (int row = 0; row < size.getHeight(); row++) {
                tiles[column][row] = new Tile(Ground.DIRT);
            }
        }
        final Random random = new Random();

        int x = 0;
        int y = 0;
        int dx = 0;
        int dy = 0;
        if (random.nextBoolean()) {
            y = random.nextInt(size.getHeight());
            dx = 1;
        } else {
            x = random.nextInt(size.getWidth());
            dy = 1;
        }
        while (x < size.getWidth() && y < size.getHeight()) {
            tiles[x][y] = new Tile(Ground.RIVER);
            x += dx;
            y += dy;
            if (random.nextInt(4) == 0) {
                if (dx == 0) {
                    x += 1 - random.nextInt(2);
                }
                if (dy == 0) {
                    y += 1 - random.nextInt(2);
                }
            }
        }
    }

    @Override
    public void draw(final Backbuffer console) {
        final Dimension consoleSize = console.getSize();
        for (int column = 1; column < consoleSize.getWidth() - 1; column++) {
            for (int row = 1; row < consoleSize.getHeight() - 1; row++) {
                final Tile tile = tiles[column][row];
                switch (tile.getGround()) {
                    case DIRT:
                        console.put(' ', column, row, null, Palette.DIRT);
                        break;
                    case TREE:
                        console.put('*', column, row, Palette.TREE, Palette.DIRT);
                        break;
                    case RIVER:
                        console.put(' ', column, row, null, Palette.RIVER);
                        break;
                }

            }
        }
    }

    @Override
    public void update(final float elapsed) {

    }

    enum Ground {

        DIRT,

        TREE,

        RIVER,

        ;

    }

    @RequiredArgsConstructor
    @Getter
    class Tile {

        private final Ground ground;

    }

}
