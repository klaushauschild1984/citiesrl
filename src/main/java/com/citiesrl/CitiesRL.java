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

import com.citiesrl.simulation.City;
import com.citiesrl.simulation.Entity;
import com.citiesrl.simulation.PowerPlant;
import com.citiesrl.simulation.Powered;
import com.citiesrl.simulation.Road;
import com.citiesrl.simulation.Zone;
import com.citiesrl.terrain.Terrain;
import com.citiesrl.terrain.Tile;
import com.rl4j.BackBuffer;
import com.rl4j.Dimension;
import com.rl4j.GameObject;
import com.rl4j.Roguelike;
import com.rl4j.event.Event;
import com.rl4j.event.MouseEvent.MouseButtonEvent;
import com.rl4j.event.MouseEvent.MouseMoveEvent;
import com.rl4j.ui.Box;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CitiesRL implements GameObject {

    private final Roguelike roguelike;
    private final Random random;
    private final Box gameBorder;
    private final Terrain terrain;
    private final City city;

    private boolean highlightQuitX;
    private String statusLine = "";

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
        city = new City(terrain);
        city.add(new Zone(Zone.Type.RESIDENTIAL, 9, 3));
        city.add(new PowerPlant(13, 3));
        city.add(new Road(15, 7));
        city.add(new Road(14, 7));
        city.add(new Road(13, 7));
        city.add(new Road(12, 7));
        city.add(new Road(11, 7));
        city.add(new Road(10, 7));
        city.add(new Road(9, 7));
        city.add(new Zone(Zone.Type.INDUSTRIAL, 9, 8));
        city.add(new Zone(Zone.Type.COMMERCIAL, 16, 4));
        roguelike.getCursor().setBlinkInterval(0);
    }

    @Override
    public void draw(final BackBuffer console) {
        gameBorder.draw(console);
        Color quitXColor = Color.BLACK;
        if (highlightQuitX) {
            quitXColor = Color.RED;
        }
        console.put("[x]", roguelike.getSize().getWidth() - 4, 0, Color.WHITE, quitXColor);

        terrain.draw(console);
        city.draw(console);
        console.put(statusLine, 1, console.getSize().getHeight() - 2);
    }

    @Override
    public void update(final float elapsed) {
        city.update(elapsed);
        terrain.update(elapsed);
    }

    @Override
    public void handle(final Event event) {
        final int quitXColumn = roguelike.getSize().getWidth() - 3;
        final int quitXRow = 0;

        event.as(MouseMoveEvent.class) //
                        .ifPresent(mouseMoveEvent -> {
                            final int column = mouseMoveEvent.getColumn();
                            final int row = mouseMoveEvent.getRow();
                            highlightQuitX = column == quitXColumn && row == quitXRow;

                            final Tile tile = terrain.get(column, row);
                            if (tile != null) {
                                final Entity entity = city.get(column, row);
                                if (entity != null) {
                                    statusLine = entity.getName();
                                    if (entity instanceof Powered) {
                                        if (!((Powered) entity).isPowered()) {
                                            statusLine += " (not powered)";
                                        }
                                    }
                                } else {
                                    statusLine = tile.getGround().name();
                                }
                            } else {
                                statusLine = "";
                            }
                        });

        event.as(MouseButtonEvent.class) //
                        .ifPresent(mouseButtonEvent -> {
                            final int column = mouseButtonEvent.getColumn();
                            final int row = mouseButtonEvent.getRow();
                            if (mouseButtonEvent.isPressed() && //
                            mouseButtonEvent.getButton() == MouseButtonEvent.Button.LEFT && //
                            column == quitXColumn && //
                            row == quitXRow) {
                                roguelike.stop();
                            }
                        });

        city.handle(event);
        terrain.handle(event);
    }

}
