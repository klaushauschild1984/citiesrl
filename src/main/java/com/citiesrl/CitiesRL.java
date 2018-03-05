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
import com.rl4j.Backbuffer;
import com.rl4j.Dimension;
import com.rl4j.Game;
import com.rl4j.Roguelike;
import com.rl4j.event.Event;
import com.rl4j.event.MouseEvent.MouseButtonEvent;
import com.rl4j.event.MouseEvent.MouseMoveEvent;
import com.rl4j.ui.Box;

public class CitiesRL implements Game {

    private final Roguelike roguelike;
    private final Box gameBorder;
    private final Terrain terrain;

    private boolean highlightQuitX;

    public CitiesRL(final Roguelike roguelike) {
        this.roguelike = roguelike;
        final Dimension size = roguelike.getSize();
        gameBorder = new Box(0, 0, size.getWidth(), size.getHeight());
        gameBorder.setTitle("Cities RL");
        terrain = new Terrain(size);
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
    }

    @Override
    public void update(final float elapsed) {
        terrain.update(elapsed);
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

    }

}
