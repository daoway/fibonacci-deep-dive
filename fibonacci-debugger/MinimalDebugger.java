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
            System.err.println("Error: " + e.getMessage());
        }
        eventManager = vm.eventRequestManager();

        System.out.println("Connected to: " + vm.name());
    }

    private void setBreakpoint() {
        ClassPrepareRequest classPrepareRequest =
                eventManager.createClassPrepareRequest();
        classPrepareRequest.addClassFilter("FibonacciTarget");
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
            Method mainMethod = clazz.methodsByName("main").get(0);
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
            Method fibMethod = clazz.methodsByName("fibonacci").get(0);
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

        if ("main".equals(methodName)) {
            System.out.println("=== PROGRAM STARTED ===");
            return;
        }

        if ("fibonacci".equals(methodName)) {
            Value nValue = null;
            List<LocalVariable> variables;
            try {
                variables = location.method().variables();
            } catch (AbsentInformationException e) {
                throw new RuntimeException(e);
            }
            for (LocalVariable localVariable : variables) {
                if ("n".equals(localVariable.name())) {
                    nValue = frame.getValue(localVariable);
                    break;
                }
            }
            if (nValue != null) {
                System.out.println("=== fibonacci(" + nValue + ") ===");
            } else {
                System.out.println("=== fibonacci(?) ===");
            }
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

                if ("fibonacci".equals(methodName)) {
                    String nValue = "?";
                    List<LocalVariable> variables;
                    try {
                        variables = location.method().variables();
                    } catch (AbsentInformationException e) {
                        throw new RuntimeException(e);
                    }
                    for (LocalVariable localVariable : variables) {
                        if ("n".equals(localVariable.name())) {
                            Value value = frame.getValue(localVariable);
                            if (value != null) {
                                nValue = value.toString();
                            }
                            break;
                        }
                    }

                    System.out.println(
                            "  [" + i + "] fibonacci(n=" + nValue + ")");
                    fibonacciCount++;
                } else {
                    System.out.println("  [" + i + "] " + methodName + "()");
                }
            }
            System.out.println("Fibonacci calls in stack: " + fibonacciCount);
    }
}