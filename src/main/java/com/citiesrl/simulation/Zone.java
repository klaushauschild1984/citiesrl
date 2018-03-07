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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Zone extends Entity {

    private final Type type;

    public Zone(final Type type, final int top, final int left) {
        super(type.name(), top, left);
        this.type = type;
    }

    @Override
    protected void draw(final BackBuffer console, final int column, final int row) {
        console.put("┌─┐", column, row, type.getColor(), Palette.Terrain.DIRT);
        console.put(String.format("│%s│", type.getC()), column, row + 1, type.getColor(),
                        Palette.Terrain.DIRT);
        console.put("└─┘", column, row + 2, type.getColor(), Palette.Terrain.DIRT);
    }

    @RequiredArgsConstructor
    @Getter
    public enum Type {

        RESIDENTIAL('R', Palette.Entity.Zone.RESIDENTIAL),

        COMMERCIAL('C', Palette.Entity.Zone.COMMERCIAL),

        INDUSTRIAL('I', Palette.Entity.Zone.INDUSTRIAL),

        ;

        private final char c;
        private final Color color;

    }

}
