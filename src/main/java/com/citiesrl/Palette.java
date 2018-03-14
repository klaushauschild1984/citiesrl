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

public interface Palette {

    interface Terrain {

        Color DIRT = new Color(170, 85, 0);

        Color ROCK = new Color(200, 115, 30);

        Color RIVER = new Color(0, 0, 255);

        Color WAVE = new Color(0, 0, 200);

        Color TREE = new Color(0, 200, 0);

    }

    interface Entity {

        interface Zone {

            Color RESIDENTIAL = new Color(0, 255, 0);

            Color COMMERCIAL = new Color(200, 0, 255);

            Color INDUSTRIAL = new Color(255, 255, 0);

        }

        interface Road {

            Color ASPHALT = new Color(0, 0, 0);

            Color MARKS = new Color(255, 255, 255);

        }

    }

}
