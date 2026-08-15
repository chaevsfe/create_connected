package com.hlysine.create_connected.foundation.advancement;

import java.util.ArrayList;
import java.util.List;

public class CCAdvancements {

    public static final List<CCAdvancement> ENTRIES = new ArrayList<>();

    public static final CCAdvancement ROOT = external("root");

    public static final CCAdvancement SHEAR_PIN = triggered("shear_pin");

    public static final CCAdvancement OVERSTRESS_CLUTCH = triggered("overstress_clutch");

    public static final CCAdvancement BRASS_GEARBOX = external("brass_gearbox");

    public static final CCAdvancement OVERPOWERED_BRAKE = triggered("overpowered_brake_0");

    public static final CCAdvancement KINETIC_BATTERY = triggered("kinetic_battery");

    public static final CCAdvancement CONTROL_CHIP = external("control_chip");

    public static final CCAdvancement SEQUENCED_PULSE_GENERATOR = external("sequenced_pulse_generator");

    public static final CCAdvancement PULSE_GEN_INFINITE_LOOP = triggered("pulse_generator_infinite_loop");

    private static CCAdvancement triggered(String id) {
        return new CCAdvancement(id, false);
    }

    private static CCAdvancement external(String id) {
        return new CCAdvancement(id, true);
    }

    public static void register() {
    }
}
