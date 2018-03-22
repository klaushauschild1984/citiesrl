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
import com.citiesrl.terrain.Terrain;
import com.rl4j.BackBuffer;
import com.rl4j.Dimension;
import com.rl4j.GameObject;
import com.rl4j.Roguelike;
import com.rl4j.event.Event;
import com.rl4j.ui.Box;
import com.rl4j.ui.sprite.Sprite;

public class CitiesRL2 implements GameObject {

    private static final int CONTROLS_WIDTH = 7;
    private static final int STATUS_HEIGHT = 7;

    private final Canvas controlsCanvas;
    private final Canvas statusCanvas;
    private final Canvas mainCanvas;
    private final Terrain terrain;

    public CitiesRL2(final Roguelike roguelike, final Random random) {
        roguelike.getCursor().setBlinkInterval(0);
        final Dimension size = roguelike.getSize();
        controlsCanvas = new Canvas(0, 0, CONTROLS_WIDTH, size.getHeight() - STATUS_HEIGHT);
        statusCanvas = new Canvas(0, size.getHeight() - STATUS_HEIGHT, size.getWidth(),
                        STATUS_HEIGHT);
        mainCanvas = new Canvas(CONTROLS_WIDTH, 0, size.getWidth() - CONTROLS_WIDTH,
                        size.getHeight() - STATUS_HEIGHT);

        final Dimension terrainSize = new Dimension((int) (size.getWidth() * 1.5),
                        (int) (size.getHeight() * 1.5));
        terrain = new Terrain(terrainSize, size, random);
    }

    @Override
    public void draw(final BackBuffer console) {
        terrain.draw(mainCanvas.console);

        controlsCanvas.draw(console);
        statusCanvas.draw(console);
        mainCanvas.draw(console);
    }

    @Override
    public void update(final float elapsed) {

    }

    @Override
    public void handle(final Event event) {
        terrain.handle(event);
    }

    private static class Canvas extends Box {

        private final Sprite console;

        Canvas(final int column, final int row, final int width, final int height) {
            super(column, row, width, height);
            console = new Sprite(row + 1, column + 1, new Dimension(width - 2, height - 2), false);
        }

        @Override
        public void draw(final BackBuffer console) {
            super.draw(console);
            this.console.draw(console);
        }

    }

}
