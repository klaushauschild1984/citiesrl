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
import com.rl4j.event.Event;
import com.rl4j.event.Handler;
import com.rl4j.event.KeyboardEvent;
import com.rl4j.event.KeyboardEvent.Key;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Terrain implements Update, Draw, Handler {

    private static final float RIVER_RATIO_THRESHOLD = 1 / 3f;

    private final Tile[][] tiles;
    private final Random random;
    private final int offsetMaxColumn;
    private final int offsetMaxRow;

    private int offsetColumn;
    private int offsetRow;
    private int moveIncrement = 3;

    private float time;

    public Terrain(final Dimension size, final Dimension consoleSize, final Random random) {
        tiles = new Tile[size.getWidth()][size.getHeight()];
        offsetMaxColumn = size.getWidth() - (consoleSize.getWidth() - 2);
        offsetMaxRow = size.getHeight() - (consoleSize.getHeight() - 2);
        this.random = random;
        boolean first = true;
        do {
            if (!first) {
                log.debug("River ratio ({}) is above  threshold ({}). Try again.", getRiverRatio(),
                                RIVER_RATIO_THRESHOLD);
            }
            final Noise noise = new Noise(random, 1.0f, size.getWidth(), size.getHeight());
            for (int column = 0; column < size.getWidth(); column++) {
                for (int row = 0; row < size.getHeight(); row++) {
                    final float value = noise.get(column, row);
                    if (value < 0.1) {
                        tiles[column][row] = new Tile(Ground.RIVER);
                    } else {
                        tiles[column][row] = new Tile(Ground.DIRT);
                        if (value > 0.8) {
                            tiles[column][row] = new Tile(Ground.TREE);
                        }
                        tiles[column][row].setDecoration(random.nextFloat() < 0.2f);
                    }
                }
            }
            first = false;
        } while (getRiverRatio() > RIVER_RATIO_THRESHOLD);
    }

    private float getRiverRatio() {
        int riverCount = 0;
        for (int column = 0; column < tiles.length - 1; column++) {
            for (int row = 0; row < tiles[column].length - 1; row++) {
                final Tile tile = tiles[column][row];
                if (tile.getGround() == Ground.RIVER) {
                    riverCount++;
                }
            }
        }
        final int tileCount = tiles.length * tiles[0].length;
        return riverCount / (float) tileCount;
    }

    @Override
    public void draw(final Backbuffer console) {
        final Dimension consoleSize = console.getSize();
        for (int column = 1; column < consoleSize.getWidth() - 1; column++) {
            for (int row = 1; row < consoleSize.getHeight() - 1; row++) {
                final Tile tile = tiles[offsetColumn + column - 1][offsetRow + row - 1];
                switch (tile.getGround()) {
                    case DIRT:
                        console.put(tile.isDecoration() ? '.' : ' ', column, row, Palette.ROCK,
                                        Palette.DIRT);
                        break;
                    case TREE:
                        console.put(tile.isDecoration() ? '.' : '*', column, row, Palette.TREE,
                                        Palette.DIRT);
                        break;
                    case RIVER:
                        console.put(tile.isDecoration() ? '~' : ' ', column, row, Palette.WAVE,
                                        Palette.RIVER);
                        break;
                }

            }
        }
    }

    @Override
    public void update(final float elapsed) {
        time += elapsed;
        if (time > 0.5f) {
            time = 0;
            for (int column = 0; column < tiles.length - 1; column++) {
                for (int row = 0; row < tiles[column].length - 1; row++) {
                    final Tile tile = tiles[column][row];
                    if (tile.getGround() != Ground.RIVER) {
                        continue;
                    }
                    tile.setDecoration(random.nextFloat() < 0.25);
                }
            }
        }
    }

    @Override
    public void handle(final Event event) {
        event.as(KeyboardEvent.class) //
                        .ifPresent(keyboardEvent -> {
                            if (keyboardEvent.getKey() == Key.CRTL) {
                                moveIncrement = keyboardEvent.isPressed() ? 9 : 3;
                            }
                            if (!keyboardEvent.isPressed()) {
                                return;
                            }
                            switch (keyboardEvent.getKey()) {
                                case A:
                                case LEFT:
                                    offsetColumn -= moveIncrement;
                                    if (offsetColumn < 0) {
                                        offsetColumn = 0;
                                    }
                                    break;
                                case W:
                                case UP:
                                    offsetRow -= moveIncrement;
                                    if (offsetRow < 0) {
                                        offsetRow = 0;
                                    }
                                    break;
                                case D:
                                case RIGHT:
                                    offsetColumn += moveIncrement;
                                    if (offsetColumn > offsetMaxColumn) {
                                        offsetColumn = offsetMaxColumn;
                                    }
                                    break;
                                case S:
                                case DOWN:
                                    offsetRow += moveIncrement;
                                    if (offsetRow > offsetMaxRow) {
                                        offsetRow = offsetMaxRow;
                                    }
                                    break;
                            }
                        });
    }

    enum Ground {

        DIRT,

        TREE,

        RIVER,;

    }

    @RequiredArgsConstructor
    @Getter
    class Tile {

        private final Ground ground;

        @Getter
        @Setter
        private boolean decoration;

    }

    class Noise {

        private final Random random;
        private final float roughness;
        private final float[][] data;

        /**
         * Generate a noise source based upon the midpoint displacement fractal.
         *
         * @param random The random number generator
         * @param roughness a roughness parameter
         * @param width the width of the grid
         * @param height the height of the grid
         */
        public Noise(final Random random, final float roughness, final int width,
                        final int height) {
            this.roughness = roughness / width;
            data = new float[width][height];
            this.random = (random == null) ? new Random() : random;
            initialise();
        }

        private void initialise() {
            int xh = data.length - 1;
            int yh = data[0].length - 1;

            // set the corner points
            data[0][0] = random.nextFloat();
            data[0][yh] = random.nextFloat();
            data[xh][0] = random.nextFloat();
            data[xh][yh] = random.nextFloat();

            // generate the fractal
            generate(0, 0, xh, yh);
        }

        // Add a suitable amount of random displacement to a point
        private float roughen(float v, int l, int h) {
            return v + roughness * (float) (random.nextGaussian() * (h - l));
        }

        // generate the fractal
        private void generate(int xl, int yl, int xh, int yh) {
            int xm = (xl + xh) / 2;
            int ym = (yl + yh) / 2;
            if ((xl == xm) && (yl == ym))
                return;

            data[xm][yl] = 0.5f * (data[xl][yl] + data[xh][yl]);
            data[xm][yh] = 0.5f * (data[xl][yh] + data[xh][yh]);
            data[xl][ym] = 0.5f * (data[xl][yl] + data[xl][yh]);
            data[xh][ym] = 0.5f * (data[xh][yl] + data[xh][yh]);

            float v = roughen(0.5f * (data[xm][yl] + data[xm][yh]), xl + yl, yh + xh);
            data[xm][ym] = v;
            data[xm][yl] = roughen(data[xm][yl], xl, xh);
            data[xm][yh] = roughen(data[xm][yh], xl, xh);
            data[xl][ym] = roughen(data[xl][ym], yl, yh);
            data[xh][ym] = roughen(data[xh][ym], yl, yh);

            generate(xl, yl, xm, ym);
            generate(xm, yl, xh, ym);
            generate(xl, ym, xm, yh);
            generate(xm, ym, xh, yh);
        }

        public float get(final int x, final int y) {
            return data[x][y];
        }

    }

}
