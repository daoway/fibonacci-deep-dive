import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MinimalDebugger {
    private static final String TARGET_CLASS = "FibonacciTarget";
    private static final String MAIN_METHOD = "main";
    private static final String FIBONACCI_METHOD = "fibonacci";
    private static final String PARAM_NAME = "n";

    private VirtualMachine vm;
    private EventRequestManager eventManager;

    public static void main(String[] args) {
        new MinimalDebugger().debug();
    }

    public void debug() {
        connect();
        setBreakpoint();
        vm.resume();
        handleEvents();
    }

    private void connect() {
        System.out.println("Connecting to target JVM...");

        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
        AttachingConnector connector = vmm.attachingConnectors().stream()
                .filter(c -> c.transport().name().equals("dt_socket"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Socket connector not found"));

        Map<String, Connector.Argument> args = connector.defaultArguments();
        args.get("hostname").setValue("localhost");
        args.get("port").setValue("5005");

        try {
            vm = connector.attach(args);
        } catch (IOException | IllegalConnectorArgumentsException e) {
            throw new RuntimeException("Failed to connect: " + e.getMessage(), e);
        }
        eventManager = vm.eventRequestManager();

        System.out.println("Connected to: " + vm.name());
    }

    private void setBreakpoint() {
        ClassPrepareRequest classPrepareRequest =
                eventManager.createClassPrepareRequest();
        classPrepareRequest.addClassFilter(TARGET_CLASS);
        classPrepareRequest.enable();
    }

    private void handleEvents() {
        EventQueue queue = vm.eventQueue();

        while (true) {
            EventSet eventSet;
            try {
                eventSet = queue.remove();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Event handling interrupted");
                break;
            }
            for (Event event : eventSet) {
                if (event instanceof ClassPrepareEvent) {
                    handleClassPrepare((ClassPrepareEvent) event);
                } else if (event instanceof BreakpointEvent) {
                    handleBreakpoint((BreakpointEvent) event);
                } else if (event instanceof VMDeathEvent) {
                    System.out.println("Target VM terminated");
                    return;
                }
            }

            eventSet.resume();
        }
    }

    private void handleClassPrepare(ClassPrepareEvent event) {
        ReferenceType clazz = event.referenceType();
        System.out.println("Class loaded: " + clazz.name());

        // breakpoint at the start of main method
        try {
            Method mainMethod = clazz.methodsByName(MAIN_METHOD).get(0);
            BreakpointRequest mainBp =
                    eventManager.createBreakpointRequest(mainMethod.location());
            mainBp.enable();
            System.out.println("Breakpoint set at main method start");
        } catch (Exception e) {
            System.err.println(
                    "Failed to set main breakpoint: " + e.getMessage());
        }

        // breakpoint at the start of fibonacci method
        try {
            Method fibMethod = clazz.methodsByName(FIBONACCI_METHOD).get(0);
            BreakpointRequest fibBp =
                    eventManager.createBreakpointRequest(fibMethod.location());
            fibBp.enable();
            System.out.println("Breakpoint set in fibonacci method");
        } catch (Exception e) {
            System.err.println(
                    "Failed to set fibonacci breakpoint: " + e.getMessage());
        }
    }

    private void handleBreakpoint(BreakpointEvent event) {
        ThreadReference thread = event.thread();
        StackFrame frame;
        try {
            frame = thread.frame(0);
        } catch (IncompatibleThreadStateException e) {
            throw new RuntimeException(e);
        }
        Location location = frame.location();
        String methodName = location.method().name();

        System.out.println("BREAKPOINT in " + methodName + "() at line " +
                location.lineNumber());

        if (MAIN_METHOD.equals(methodName)) {
            System.out.println("=== PROGRAM STARTED ===");
            return;
        }

        if (FIBONACCI_METHOD.equals(methodName)) {
            System.out.println("=== FIBONACCI CALL ===");
            showStackFrames(thread);
        }
    }

    private void showStackFrames(ThreadReference thread) {
        List<StackFrame> frames;
        try {
            frames = thread.frames();
        } catch (IncompatibleThreadStateException e) {
            throw new RuntimeException(e);
        }
        int fibonacciCount = 0;

        System.out.println("Stack depth: " + frames.size());

        for (int i = 0; i < frames.size(); i++) {
            StackFrame frame = frames.get(i);
            Location location = frame.location();
            String methodName = location.method().name();

            if (FIBONACCI_METHOD.equals(methodName)) {
                String nValue = getLocalVariableValue(frame, location, PARAM_NAME);
                System.out.println("  [" + i + "] fibonacci(n=" + nValue + ")");
                fibonacciCount++;
            } else {
                System.out.println("  [" + i + "] " + methodName + "()");
            }
        }
        System.out.println("Fibonacci calls in stack: " + fibonacciCount);
    }

    private String getLocalVariableValue(StackFrame frame, Location location, String varName) {
        try {
            List<LocalVariable> variables = location.method().variables();
            for (LocalVariable localVariable : variables) {
                if (varName.equals(localVariable.name())) {
                    Value value = frame.getValue(localVariable);
                    return value != null ? value.toString() : "?";
                }
            }
        } catch (AbsentInformationException e) {
            System.err.println("Variable info not available: " + e.getMessage());
        }
        return "?";
    }
}