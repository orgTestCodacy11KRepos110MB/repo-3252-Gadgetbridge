/*  Copyright (C) 2021 José Rebelo

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.devices.sony.wh1000xm3;

public enum AutomaticPowerOff {
    OFF(new byte[]{(byte) 0x11, (byte) 0x00}),
    AFTER_5_MIN(new byte[]{(byte) 0x00, (byte) 0x00}),
    AFTER_30_MIN(new byte[]{(byte) 0x01, (byte) 0x01}),
    AFTER_1_HOUR(new byte[]{(byte) 0x02, (byte) 0x02}),
    AFTER_3_HOUR(new byte[]{(byte) 0x03, (byte) 0x03});

    public final byte[] code;

    AutomaticPowerOff(final byte[] code) {
        this.code = code;
    }
}