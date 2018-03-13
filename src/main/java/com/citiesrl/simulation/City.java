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

package com.citiesrl.simulation;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.citiesrl.terrain.Terrain;
import com.rl4j.BackBuffer;
import com.rl4j.Dimension;
import com.rl4j.Draw;
import com.rl4j.Update;
import com.rl4j.event.Event;
import com.rl4j.event.Handler;
import com.rl4j.event.KeyboardEvent;
import com.rl4j.event.KeyboardEvent.Key;
import lombok.Getter;

public class City implements Update, Draw, Handler {

    private final Clock clock = new Clock();
    @Getter
    private final Terrain terrain;
    private final Boolean[][] powerMap;
    private final Set<Entity> entities = new HashSet<>();

    private boolean displayPower;

    public City(final Terrain terrain) {
        this.terrain = terrain;
        final int width = terrain.getSize().getWidth();
        final int height = terrain.getSize().getHeight();
        powerMap = new Boolean[width][height];
        for (int i = 0; i < width; i++) {
            powerMap[i] = new Boolean[height];
        }
    }

    @Override
    public void draw(final BackBuffer console) {
        clock.draw(console);
        // TODO use sub buffer if available
        final BackBuffer renderBuffer = new BackBuffer(console.getSize()) {

            @Override
            public void put(final char c, final int column, final int row, final Color foreground,
                            final Color background) {
                if (!contains(column, row)) {
                    return;
                }
                console.put(c, column, row, foreground, background);
            }

            @Override
            public boolean contains(final int column, final int row) {
                final Dimension size = console.getSize();
                return column >= 1 && column <= console.getSize().getWidth() - 1 && row >= 1
                                && row <= size.getHeight() - 1;
            }

        };
        entities.forEach(entity -> {
            entity.draw(renderBuffer);
        });
        if (displayPower) {
            for (int column = 0; column < terrain.getSize().getWidth(); column++) {
                for (int row = 0; row < terrain.getSize().getHeight(); row++) {
                    final Boolean power = powerMap[column][row];
                    if (power == null) {
                        continue;
                    }
                    renderBuffer.put('P', column - terrain.getOffsetColumn(), row, Color.WHITE,
                                    power ? Color.GREEN : Color.RED);
                }
            }
        }
    }

    @Override
    public void update(final float elapsed) {
        clock.update(elapsed);
        entities.forEach(entity -> entity.update(elapsed));
    }

    @Override
    public void handle(final Event event) {
        clock.handle(event);
        entities.forEach(entity -> entity.handle(event));
        event.as(Tick.class) //
                        .ifPresent(tick -> {
                            // TODO update city simulation
                        });
        event.as(KeyboardEvent.class) //
                        .ifPresent(keyboardEvent -> {
                            if (keyboardEvent.getKey() == Key.P && keyboardEvent.isPressed()) {
                                displayPower = !displayPower;
                            }
                        });
    }

    public void add(final Entity entity) {
        entity.setCity(this);
        entities.add(entity);
        updatePower(entity);
    }

    private void updatePower(final Entity entity) {
        final boolean power = entity instanceof PowerPlant;
        for (int column = 0; column < entity.getSize().getWidth(); column++) {
            for (int row = 0; row < entity.getSize().getHeight(); row++) {
                powerMap[column + entity.getLeft()][row + entity.getTop()] = power;
            }
        }
    }

}
