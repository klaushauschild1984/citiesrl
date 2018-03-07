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
import lombok.Getter;

public class City implements Update, Draw, Handler {

    private final Clock clock = new Clock();
    @Getter
    private final Terrain terrain;
    private final Set<Entity> entities = new HashSet<>();

    public City(final Terrain terrain) {
        this.terrain = terrain;
    }

    @Override
    public void draw(final BackBuffer console) {
        clock.draw(console);
        // TODO use sub buffer if available
        entities.forEach(entity -> entity.draw(new BackBuffer(console.getSize()) {

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
        }));
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
    }

    public void add(final Entity entity) {
        entity.setCity(this);
        entities.add(entity);
    }

}
