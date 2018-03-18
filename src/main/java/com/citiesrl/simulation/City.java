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
import com.rl4j.ui.sprite.Sprite;

import lombok.Getter;

public class City implements Update, Draw, Handler {

    private final Clock clock = new Clock();
    @Getter
    private final Terrain terrain;
    private final Set<Entity> entities = new HashSet<>();
    private final Entity[][] entityMap;

    private boolean displayPower;

    public City(final Terrain terrain) {
        this.terrain = terrain;
        final int width = terrain.getSize().getWidth();
        final int height = terrain.getSize().getHeight();
        entityMap = new Entity[width][height];
        for (int column = 0; column < width; column++) {
            entityMap[column] = new Entity[height];
        }
    }

    @Override
    public void draw(final BackBuffer console) {
        clock.draw(console);

        final Sprite cityConsole = new Sprite(1, 1, new Dimension(console.getSize().getWidth() - 2,
                        console.getSize().getHeight() - 3), false);
        entities.forEach(entity -> {
            entity.draw(cityConsole);
        });
        if (displayPower) {
            for (int column = 0; column < terrain.getSize().getWidth(); column++) {
                for (int row = 0; row < terrain.getSize().getHeight(); row++) {
                    final Entity entity = entityMap[column][row];
                    if (entity == null) {
                        continue;
                    }
                    if (!(entity instanceof Powered)) {
                        continue;
                    }
                    cityConsole.put('P', column - terrain.getOffsetColumn() + 1,
                                    row - terrain.getOffsetRow() + 1, Color.WHITE,
                                    ((Powered) entity).isPowered() ? Color.GREEN : Color.RED);
                }
            }
        }
        cityConsole.draw(console);
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
        updateEntityMap(entity);
        updatePower();
    }

    private void updateEntityMap(final Entity entity) {
        for (int column = 0; column < entity.getSize().getWidth(); column++) {
            for (int row = 0; row < entity.getSize().getHeight(); row++) {
                final int entityColumn = column + entity.getLeft() - 1;
                final int entityRow = row + entity.getTop() - 1;
                entityMap[entityColumn][entityRow] = entity;

            }
        }
    }

    private void updatePower() {
        entities.stream() //
                        .filter(entity -> entity instanceof Powered) //
                        .map(entity -> ((Powered) entity)) //
                        .forEach(powered -> powered.setPowered(false));
        entities.stream() //
                        .filter(entity -> entity instanceof PowerPlant) //
                        .map(entity -> ((PowerPlant) entity)) //
                        .forEach(this::updatePower);
    }

    private void updatePower(final Powered powerSource) {
        for (int column = 0; column < powerSource.getSize().getWidth() + 2; column++) {
            for (int row = 0; row < powerSource.getSize().getHeight() + 2; row++) {
                try {
                    final int entityColumn = (powerSource.getLeft() - 1) + (column - 1);
                    final int entityRow = (powerSource.getTop() - 1) + (row - 1);
                    final Entity entity = entityMap[entityColumn][entityRow];
                    if (entity == null) {
                        continue;
                    }
                    if (!(entity instanceof Powered)) {
                        continue;
                    }
                    final Powered powered = (Powered) entity;
                    if (powered == powerSource) {
                        continue;
                    }
                    if (powered.isPowered()) {
                        continue;
                    }
                    powered.setPowered(true);
                    updatePower(powered);
                } catch (final ArrayIndexOutOfBoundsException exception) {
                    continue;
                }
            }
        }
    }

}
