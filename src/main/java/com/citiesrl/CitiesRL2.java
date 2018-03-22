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

import com.rl4j.BackBuffer;
import com.rl4j.GameObject;
import com.rl4j.Roguelike;
import com.rl4j.event.Event;
import com.rl4j.ui.Box;

public class CitiesRL2 implements GameObject {

    public static final int CONTROLLS_WIDTH = 7;
    public static final int STATUS_HEIGHT = 7;

    public CitiesRL2(final Roguelike roguelike, final Long randomSeed) {
        roguelike.getCursor().setBlinkInterval(0);
    }

    @Override
    public void draw(final BackBuffer console) {
        new Box(0, 0, CONTROLLS_WIDTH, console.getSize().getHeight() - STATUS_HEIGHT).draw(console);
        new Box(0, console.getSize().getHeight() - STATUS_HEIGHT, console.getSize().getWidth(),
                        STATUS_HEIGHT).draw(console);
        new Box(CONTROLLS_WIDTH, 0, console.getSize().getWidth() - CONTROLLS_WIDTH,
                        console.getSize().getHeight() - STATUS_HEIGHT).draw(console);
    }

    @Override
    public void update(final float elapsed) {

    }

    @Override
    public void handle(final Event event) {

    }

}
