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

import java.awt.Color;
import java.util.Random;

import com.rl4j.Backbuffer;
import com.rl4j.Dimension;
import com.rl4j.Game;
import com.rl4j.Roguelike;
import com.rl4j.event.Event;
import com.rl4j.event.MouseEvent.MouseButtonEvent;
import com.rl4j.event.MouseEvent.MouseMoveEvent;
import com.rl4j.ui.Box;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CitiesRL implements Game {

    private final Roguelike roguelike;
    private final Random random;
    private final Box gameBorder;
    private final Terrain terrain;
    private final Clock clock;

    private boolean highlightQuitX;

    public CitiesRL(final Roguelike roguelike, final Long randomSeed) {
        this.roguelike = roguelike;
        if (randomSeed == null) {
            final long currentTimeMillis = System.currentTimeMillis();
            log.info("Random seed: {}", currentTimeMillis);
            random = new Random(currentTimeMillis);
        } else {
            log.info("Given seed: {}", randomSeed);
            random = new Random(randomSeed);
        }
        final Dimension size = roguelike.getSize();
        gameBorder = new Box(0, 0, size.getWidth(), size.getHeight());
        gameBorder.setTitle("Cities RL");
        final Dimension terrainSize = new Dimension((int) (size.getWidth() * 1.5),
                        (int) (size.getHeight() * 1.5));
        terrain = new Terrain(terrainSize, size, random);
        clock = new Clock();
    }

    @Override
    public void draw(final Backbuffer console) {
        gameBorder.draw(console);
        Color quitXColor = Color.BLACK;
        if (highlightQuitX) {
            quitXColor = Color.RED;
        }
        console.put("[x]", roguelike.getSize().getWidth() - 4, 0, Color.WHITE, quitXColor);

        terrain.draw(console);
        clock.draw(console);
    }

    @Override
    public void update(final float elapsed) {
        terrain.update(elapsed);
        clock.update(elapsed);
    }

    @Override
    public void handle(final Event event) {
        final int quitXColumn = roguelike.getSize().getWidth() - 3;
        final int quitXRow = 0;

        event.as(MouseMoveEvent.class) //
                        .ifPresent(mouseMoveEvent -> {
                            highlightQuitX = mouseMoveEvent.getColumn() == quitXColumn
                                            && mouseMoveEvent.getRow() == quitXRow;
                        });

        event.as(MouseButtonEvent.class) //
                        .ifPresent(mouseButtonEvent -> {
                            if (mouseButtonEvent.isPressed() && //
                            mouseButtonEvent.getButton() == MouseButtonEvent.Button.LEFT && //
                            mouseButtonEvent.getColumn() == quitXColumn && //
                            mouseButtonEvent.getRow() == quitXRow) {
                                roguelike.stop();
                            }
                        });

        clock.handle(event);
        terrain.handle(event);
    }

}
