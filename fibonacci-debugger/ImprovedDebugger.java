import com.sun.jdi.*;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ImprovedDebugger {
    private static final String TARGET_CLASS = "FibonacciTarget";
    private static final String MAIN_METHOD = "main";
    private static final String FIBONACCI_METHOD = "fibonacci";
    private static final String PARAM_NAME = "n"; // Параметр n

    private VirtualMachine vm;
    private EventRequestManager eventManager;

    public static void main(String[] args) {
        new ImprovedDebugger().debug();
    }

    public void debug() {
        connect();
        setInitialRequests(); // Перейменовано для кращої семантики
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
        System.out.println("------------------------------------------");
    }

    private void setInitialRequests() {
        // 1. Запит на підготовку класу
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
                    handleBreakpoint((BreakpointEvent) event); // Для main
                } else if (event instanceof MethodEntryEvent) {
                    handleMethodEntry((MethodEntryEvent) event); // Нова обробка входу
                } else if (event instanceof MethodExitEvent) {
                    handleMethodExit((MethodExitEvent) event); // Обробка виходу
                } else if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
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
        setMainBreakpoint(clazz);
        setFibonacciMethodRequests(clazz);
    }

    private void setMainBreakpoint(ReferenceType clazz) {
        try {
            Method method = clazz.methodsByName(MAIN_METHOD).stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Method " + MAIN_METHOD + " not found."));
            BreakpointRequest breakpointRequest =
                    eventManager.createBreakpointRequest(method.location());
            breakpointRequest.enable();
            System.out.println("Breakpoint set at main method start");
        } catch (Exception e) {
            System.err.println("Failed to set main breakpoint: " + e.getMessage());
        }
    }

    // Встановлюємо запити на ВХІД та ВИХІД з методу fibonacci
    private void setFibonacciMethodRequests(ReferenceType clazz) {
        // Встановлення MethodEntryRequest для фіксації входу
        MethodEntryRequest entryRequest = eventManager.createMethodEntryRequest();
        entryRequest.addClassFilter(TARGET_CLASS);
        entryRequest.enable();
        System.out.println("MethodEntryRequest set for " + TARGET_CLASS);

        // Встановлення MethodExitRequest для фіксації виходу
        MethodExitRequest exitRequest = eventManager.createMethodExitRequest();
        exitRequest.addClassFilter(TARGET_CLASS);
        exitRequest.enable();
        System.out.println("MethodExitRequest set for " + TARGET_CLASS);
    }

    private void handleBreakpoint(BreakpointEvent event) {
        // Використовуємо лише для main()
        String methodName = event.location().method().name();
        if (MAIN_METHOD.equals(methodName)) {
            System.out.println("BREAKPOINT in main() at line " + event.location().lineNumber());
            System.out.println("=== PROGRAM STARTED ===");
        }
    }

    private void handleMethodEntry(MethodEntryEvent event) {
        ThreadReference thread = event.thread();
        String methodName = event.method().name();

        if (FIBONACCI_METHOD.equals(methodName)) {
            StackFrame frame;
            try {
                frame = thread.frame(0);
            } catch (IncompatibleThreadStateException e) {
                return;
            }

            String nValue = getLocalVariableValue(frame, frame.location(), PARAM_NAME);

            System.out.println("\n------------------------------------------");
            System.out.println("-> ENTRY in fibonacci(" + nValue + ")");
            showStackFrames(thread, "ENTRY");

        }
    }

    private void handleMethodExit(MethodExitEvent event) {
        ThreadReference thread = event.thread();
        String methodName = event.method().name();

        if (FIBONACCI_METHOD.equals(methodName)) {
            long returnValue = event.returnValue() instanceof PrimitiveValue ?
                    ((PrimitiveValue) event.returnValue()).longValue() : -1;

            System.out.println("\n------------------------------------------");
            System.out.println("<- EXIT from fibonacci (returns: " + returnValue + ")");
            showStackFrames(thread, "EXIT");
        }
    }

    private void showStackFrames(ThreadReference thread, String type) {
        List<StackFrame> frames;
        try {
            frames = thread.frames();
        } catch (IncompatibleThreadStateException e) {
            throw new RuntimeException(e);
        }
        int fibonacciCount = 0;

        // При ВХОДІ кадр вже додано, при ВИХОДІ - ще не видалено
        System.out.println("Stack depth (" + type + "): " + frames.size());

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

    // ... (getLocalVariableValue залишається без змін)
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
            // Ігноруємо відсутність інформації, це не критично для демонстрації стека
        }
        return "?";
    }
}