/*  Copyright (C) 2022 José Rebelo

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
package nodomain.freeyourgadget.gadgetbridge.devices.sony.headphones.coordinators;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.devices.sony.headphones.SonyHeadphonesCapabilities;
import nodomain.freeyourgadget.gadgetbridge.devices.sony.headphones.SonyHeadphonesCoordinator;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.model.DeviceType;

public class SonyLinkBudsSCoordinator extends SonyHeadphonesCoordinator {
    @NonNull
    @Override
    public DeviceType getSupportedType(final GBDeviceCandidate candidate) {
        if (candidate.getName().contains("LinkBuds S")) {
            return DeviceType.SONY_LINKBUDS_S;
        }

        return DeviceType.UNKNOWN;
    }

    @Override
    public DeviceType getDeviceType() {
        return DeviceType.SONY_LINKBUDS_S;
    }

    @Override
    public List<SonyHeadphonesCapabilities> getCapabilities() {
        return Arrays.asList(
                SonyHeadphonesCapabilities.BatteryDual,
                SonyHeadphonesCapabilities.BatteryCase,
                SonyHeadphonesCapabilities.AmbientSoundControl,
                SonyHeadphonesCapabilities.EqualizerSimple,
                SonyHeadphonesCapabilities.AudioUpsampling,
                SonyHeadphonesCapabilities.ButtonModesLeftRight,
                SonyHeadphonesCapabilities.PauseWhenTakenOff,
                SonyHeadphonesCapabilities.AutomaticPowerOffWhenTakenOff
        );
    }

    @Override
    public boolean preferServiceV2() {
        return true;
    }
}
