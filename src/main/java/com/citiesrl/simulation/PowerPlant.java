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
import com.citiesrl.Palette;
import com.rl4j.BackBuffer;
import com.rl4j.Dimension;

public class PowerPlant extends Entity {

    public PowerPlant(final int left, final int top) {
        super("Power plant", left, top, new Dimension(3, 4));
    }

    @Override
    protected void draw(final BackBuffer console, final int column, final int row) {
        console.put("PPP", column, row, Color.YELLOW, Palette.Terrain.DIRT);
        console.put("PPP", column, row + 1, Color.YELLOW, Palette.Terrain.DIRT);
        console.put("PPP", column, row + 2, Color.YELLOW, Palette.Terrain.DIRT);
        console.put("PPP", column, row + 3, Color.YELLOW, Palette.Terrain.DIRT);
    }

}
