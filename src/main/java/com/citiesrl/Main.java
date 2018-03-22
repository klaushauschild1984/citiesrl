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

import java.util.Random;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.rl4j.Dimension;
import com.rl4j.Roguelike;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(final String[] args) throws CmdLineException {
        final Arguments arguments = new Arguments();
        final CmdLineParser cmdLineParser = new CmdLineParser(arguments);
        cmdLineParser.parseArgument(args);

        final Roguelike roguelike = Roguelike.builder() //
                        .title("Cities RL") //
                        .borderless(true) //
                        .fpsLimit(30) //
                        .size(new Dimension(80, 40)) //
                        .nativeCursor(true) //
                        .build();
        final CitiesRL2 citiesRL = new CitiesRL2(roguelike, getRandom(arguments.randomSeed));
        roguelike.start(citiesRL);
    }

    private static Random getRandom(final Long randomSeed) {
        if (randomSeed == null) {
            final long currentTimeMillis = System.currentTimeMillis();
            log.info("Random seed: {}", currentTimeMillis);
            return new Random(currentTimeMillis);
        }
        log.info("Given seed: {}", randomSeed);
        return new Random(randomSeed);
    }

    static class Arguments {

        @Option(name = "-randomSeed")
        Long randomSeed;

    }

}
