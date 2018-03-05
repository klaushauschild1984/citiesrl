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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.rl4j.Backbuffer;
import com.rl4j.Draw;
import com.rl4j.Update;
import com.rl4j.event.Event;
import com.rl4j.event.Handler;
import com.rl4j.event.KeyboardEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Date implements Update, Draw, Handler {

    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final Calendar calendar = GregorianCalendar.getInstance();

    private java.util.Date date;
    private float seconds;
    private Speed speed = Speed.NORMAL;
    private Speed oldSpeed;

    public Date() {
        calendar.set(1900, Calendar.JANUARY, 1);
        date = calendar.getTime();
    }

    @Override
    public void draw(final Backbuffer console) {
        final String dateString =
                        String.format("%s %s", dateFormat.format(date), speed.getSymbol());
        console.put(dateString, 1, console.getSize().getHeight() - 1);
    }

    @Override
    public void update(final float elapsed) {
        seconds += elapsed;
        if (speed.getSeconds() > 0 && seconds > speed.getSeconds()) {
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
            seconds = 0;
        }
    }

    @Override
    public void handle(final Event event) {
        event.as(KeyboardEvent.class) //
                        .ifPresent(keyboardEvent -> {
                            if (!keyboardEvent.isPressed()) {
                                return;
                            }
                            switch (keyboardEvent.getKey()) {
                                // TODO handle faster, slower
                                case SPACE:
                                    if (speed != Speed.PAUSE) {
                                        oldSpeed = speed;
                                        speed = Speed.PAUSE;
                                    } else {
                                        speed = oldSpeed;
                                    }
                                    break;
                            }
                        });
    }

    @RequiredArgsConstructor
    @Getter
    private enum Speed {

        PAUSE(".", 0),

        NORMAL(">", 5),

        FAST("»", 0.5f),

        ;


        private final String symbol;
        private final float seconds;

    }

}
