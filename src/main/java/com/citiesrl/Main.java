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

import com.rl4j.Dimension;
import com.rl4j.Roguelike;

public class Main {

    public static void main(final String[] args) {
        final Roguelike roguelike = Roguelike.builder() //
                        .title("Cities RL") //
                        .borderless(true) //
                        .fpsLimit(30) //
                        .size(new Dimension(40, 30)) //
                        .nativeCursor(true) //
                        .build();
        final CitiesRL citiesRL = new CitiesRL(roguelike, null);
        roguelike.start(citiesRL);
    }

}
