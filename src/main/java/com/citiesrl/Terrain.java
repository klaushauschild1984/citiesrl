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

import com.rl4j.Backbuffer;
import com.rl4j.Dimension;
import com.rl4j.Draw;
import com.rl4j.Update;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Terrain implements Update, Draw {

    private final Dimension size;

    private int offsetColumn;
    private int offsetRow;

    @Override
    public void draw(final Backbuffer console) {
        final Dimension consoleSize = console.getSize();
        for (int column = 1; column < consoleSize.getWidth() - 1; column++) {
            for (int row = 1; row < consoleSize.getHeight() - 1; row++) {
                console.put(' ', column, row, null, Palette.DIRT);
            }
        }
    }

    @Override
    public void update(final float elapsed) {

    }

}
