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

import com.citiesrl.terrain.Terrain;
import com.rl4j.BackBuffer;
import com.rl4j.Dimension;
import com.rl4j.Draw;
import com.rl4j.Update;
import com.rl4j.event.Event;
import com.rl4j.event.Handler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public abstract class Entity implements Update, Draw, Handler {

    @Getter
    private final String name;
    @Getter
    private final int left;
    @Getter
    private final int top;
    @Getter
    private final Dimension size;

    @Setter
    private City city;

    @Override
    public void draw(final BackBuffer console) {
        final Terrain terrain = city.getTerrain();
        draw(console, left - terrain.getOffsetColumn(), top - terrain.getOffsetRow());
    }

    protected abstract void draw(final BackBuffer console, final int column, final int row);

    @Override
    public void update(final float elapsed) {}

    @Override
    public void handle(final Event event) {}

}
