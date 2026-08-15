package com.hlysine.create_connected.content.sequencedpulsegenerator.instructions;

import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;

import static com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity.INSTRUCTION_CAPACITY;

public abstract class Instruction {

    public static final String LIST_KEY = "Instructions";

    private static final Map<String, Instruction> INSTRUCTION_MAP = new LinkedHashMap<>();

    static {
        register(new OutputInstruction(10, 15));
        register(new TransformInstruction(2, 15));
        register(new WaitForInstruction(1, 0));
        register(new WaitForMinInstruction(8, 0));
        register(new WaitForMaxInstruction(7, 0));
        register(new WaitForExactInstruction(7, 0));
        register(new LoopForInstruction(3));
        register(new LoopIfInstruction(1));
        register(new LoopIfMinInstruction(8));
        register(new LoopIfMaxInstruction(7));
        register(new LoopIfExactInstruction(7));
        register(new LoopInstruction());
        register(new EndInstruction());
    }

    private static void register(Instruction instruction) {
        INSTRUCTION_MAP.put(instruction.instructionId, instruction);
    }

    private final String instructionId;
    private final Background background;
    public final @Nullable ParameterConfig paramConfig;
    public final boolean hasSignal;
    public final boolean terminal;

    private int param = 0;
    private int signal = 0;

    public Instruction(String instructionId,
                       Background background,
                       @Nullable ParameterConfig paramConfig,
                       boolean hasSignal,
                       boolean terminal) {
        this.instructionId = instructionId;
        this.background = background;
        this.paramConfig = paramConfig;
        this.hasSignal = hasSignal;
        this.terminal = terminal;
    }

    public String getId() {
        return instructionId;
    }

    public int getOrdinal() {
        return INSTRUCTION_MAP.keySet().stream().toList().indexOf(getId());
    }

    public static Instruction getByOrdinal(int ordinal) {
        return INSTRUCTION_MAP.values().stream().toList().get(ordinal).copy();
    }

    public Background getBackground() {
        return background;
    }

    public InstructionResult tick(SequencedPulseGeneratorBlockEntity be) {
        return InstructionResult.incomplete();
    }

    public int transformOutput(SequencedPulseGeneratorBlockEntity be, int signal) {
        return signal;
    }

    public int getParam() {
        return param;
    }

    public int getSignal() {
        return signal;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public abstract void writeState(ValueOutput view);

    public abstract void readState(ValueInput view);

    public abstract Instruction copy();

    public static Vector<Instruction> createDefault() {
        Vector<Instruction> instructions = new Vector<>(INSTRUCTION_CAPACITY);
        instructions.add(new OutputInstruction(10, 15));
        instructions.add(new EndInstruction());
        return instructions;
    }

    @Nullable
    public static Instruction create(String instructionId) {
        Instruction template = INSTRUCTION_MAP.get(instructionId);
        if (template == null) return null;
        return template.copy();
    }

    public void write(ValueOutput view) {
        view.putString("ID", instructionId);
        if (hasSignal) {
            view.putInt("Signal", signal);
        }
        if (paramConfig != null) {
            view.putInt("Value", param);
        }
        writeState(view);
    }

    @Nullable
    public static Instruction read(ValueInput view) {
        String id = view.getString("ID").orElse(null);
        if (id == null) return null;
        Instruction instance = create(id);
        if (instance == null) return null;
        if (instance.hasSignal) {
            instance.signal = view.getIntOr("Signal", instance.signal);
        }
        if (instance.paramConfig != null) {
            instance.param = view.getIntOr("Value", instance.param);
        }
        instance.readState(view);
        return instance;
    }

    public static void writeAll(ValueOutput.ValueOutputList list, List<Instruction> instructions) {
        for (Instruction instruction : instructions) {
            instruction.write(list.addChild());
        }
    }

    public static Vector<Instruction> readAll(ValueInput.ValueInputList list) {
        Vector<Instruction> instructions = new Vector<>(INSTRUCTION_CAPACITY);
        for (ValueInput entry : list) {
            Instruction instruction = read(entry);
            if (instruction == null) {
                CreateConnected.LOGGER.error("Discarding an unreadable sequenced pulse generator instruction (id {})",
                        entry.getString("ID").orElse("absent"));
                continue;
            }
            instructions.add(instruction);
        }
        return instructions;
    }

    public static ListTag serializeAll(List<Instruction> instructions) {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        writeAll(output.childrenList(LIST_KEY), instructions);
        return output.buildResult().getListOrEmpty(LIST_KEY);
    }

    public static Vector<Instruction> deserializeAll(ListTag list) {
        if (list.isEmpty()) {
            return createDefault();
        }
        List<CompoundTag> entries = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            list.getCompound(i).ifPresent(entries::add);
        }
        Vector<Instruction> instructions =
                readAll(TagValueInput.create(ProblemReporter.DISCARDING, RegistryAccess.EMPTY, entries));
        if (instructions.isEmpty()) {
            CreateConnected.LOGGER.error("Received a sequenced pulse generator program with no readable instructions");
            return createDefault();
        }
        return instructions;
    }

    public String getLangKey() {
        return "gui.sequenced_pulse_generator.instruction." + ConnectedLang.asId(instructionId);
    }

    public String getDescriptiveLangKey() {
        return getLangKey() + ".descriptive";
    }

    public static List<Component> getOptions() {
        List<Component> options = new ArrayList<>();
        for (Instruction value : INSTRUCTION_MAP.values())
            options.add(ConnectedLang.translateDirect(value.getDescriptiveLangKey()));
        return options;
    }

    public enum Background {
        INSTRUCTION,
        DELAY,
        END
    }

    public record StepContext(int currentValue, boolean forward, boolean shift, boolean control) {
    }

    public record ParameterConfig(int minValue,
                                  int maxValue,
                                  @Nullable Function<StepContext, Integer> stepFunction,
                                  int shiftStepValue,
                                  int defaultValue,
                                  @Nullable Function<Integer, Component> formatter) {
        public static final Function<StepContext, Integer> timeStep = context -> {
            int v = context.currentValue();
            if (!context.forward())
                v--;
            if (v < 20)
                return context.shift() ? 20 : 1;
            return context.shift() ? 100 : 20;
        };
        public static final Function<Integer, Component> timeFormat = value -> {
            if (value >= 20) return Component.literal((value / 20) + "s");
            return Component.literal(value + "t");
        };
        public static final Function<Integer, Component> booleanFormat = value -> value == 1
                ? ConnectedLang.translateDirect("gui.sequenced_pulse_generator.on")
                : ConnectedLang.translateDirect("gui.sequenced_pulse_generator.off");
        public static final Function<Integer, Component> transformFormat = value -> Component.literal(switch (value) {
            case 0 -> "I+C";
            case 1 -> "I-C";
            case 2 -> "C-I";
            case 3 -> "I×C";
            case 4 -> "I÷C";
            case 5 -> "I&C";
            case 6 -> "I|C";
            case 7 -> "I^C";
            case 8 -> "I<<C";
            case 9 -> "I>>C";
            default -> Integer.toString(value);
        });
    }
}
