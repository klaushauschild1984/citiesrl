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

package com.citiesrl.terrain;

import java.util.Random;

import com.citiesrl.Palette;
import com.citiesrl.simulation.Tick;
import com.rl4j.BackBuffer;
import com.rl4j.Dimension;
import com.rl4j.Draw;
import com.rl4j.Update;
import com.rl4j.event.Event;
import com.rl4j.event.Handler;
import com.rl4j.event.KeyboardEvent;
import com.rl4j.event.KeyboardEvent.Key;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Terrain implements Update, Draw, Handler {

    private static final float RIVER_RATIO_THRESHOLD = 1 / 3f;

    private final Tile[][] tiles;
    private final Random random;
    private final int offsetMaxColumn;
    private final int offsetMaxRow;

    @Getter
    private int offsetColumn;
    @Getter
    private int offsetRow;
    private int moveIncrement = 3;

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

    public Dimension getSize() {
        return new Dimension(tiles.length, tiles[0].length);
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
    public void draw(final BackBuffer console) {
        final Dimension consoleSize = console.getSize();
        for (int column = 0; column < consoleSize.getWidth(); column++) {
            for (int row = 0; row < consoleSize.getHeight(); row++) {
                final Tile tile = tiles[offsetColumn + column][offsetRow + row];
                switch (tile.getGround()) {
                    case DIRT:
                        final char dirt = tile.isDecoration() ? '.' : ' ';
                        console.put(dirt, column, row, Palette.Terrain.ROCK, Palette.Terrain.DIRT);
                        break;
                    case TREE:
                        final char tree = tile.isDecoration() ? '.' : '*';
                        console.put(tree, column, row, Palette.Terrain.TREE, Palette.Terrain.DIRT);
                        break;
                    case RIVER:
                        final char river = tile.isDecoration() ? '~' : ' ';
                        console.put(river, column, row, Palette.Terrain.WAVE,
                                        Palette.Terrain.RIVER);
                        break;
                }

            }
        }
    }

    @Override
    public void update(final float elapsed) {}

    @Override
    public void handle(final Event event) {
        event.as(Tick.class) //
                        .ifPresent(tick -> redecorate());
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

    private void redecorate() {
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

    public Tile get(final int column, final int row) {
        try {
            return tiles[column + offsetColumn][row + offsetRow];
        } catch (final ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

}
