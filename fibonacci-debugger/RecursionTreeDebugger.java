import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class RecursionTreeDebugger {
    private static final String TARGET_CLASS = "FibonacciTarget";
    private static final String FIB_METHOD = "fibonacci";
    private static final String PARAM_NAME = "n";
    private final Map<String, CallNode> callMap = new HashMap<>();
    private final Deque<String> callStack = new ArrayDeque<>();
    private final MutableGraph graph =
            mutGraph("Fibonacci Call Tree").setDirected(true);
    private VirtualMachine vm;
    private EventRequestManager eventManager;

    public static void main(String[] args) {
        new RecursionTreeDebugger().debug();
    }

    public void debug() {
        connect();
        setInitialRequests();
        vm.resume();
        handleEvents();
    }

    private void connect() {
        System.out.println("Connecting to target JVM on localhost:5005...");
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
            throw new RuntimeException("Failed to connect", e);
        }
        eventManager = vm.eventRequestManager();
        System.out.println("Connected to: " + vm.name());
    }

    private void setInitialRequests() {
        // Ловимо завантаження класу
        ClassPrepareRequest cpr = eventManager.createClassPrepareRequest();
        cpr.addClassFilter(TARGET_CLASS);
        cpr.enable();

        // Ловимо входи/виходи з методів
        MethodEntryRequest entry = eventManager.createMethodEntryRequest();
        entry.addClassFilter(TARGET_CLASS);
        entry.enable();

        MethodExitRequest exit = eventManager.createMethodExitRequest();
        exit.addClassFilter(TARGET_CLASS);
        exit.enable();
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
                } else if (event instanceof MethodEntryEvent) {
                    handleMethodEntry((MethodEntryEvent) event);
                } else if (event instanceof MethodExitEvent) {
                    handleMethodExit((MethodExitEvent) event);
                } else if (event instanceof VMDeathEvent ||
                        event instanceof VMDisconnectEvent) {
                    System.out.println(
                            "Target VM terminated. Generating call tree...");
                    generateGraph();
                    return;
                }
            }
            eventSet.resume();
        }
    }

    private void handleClassPrepare(ClassPrepareEvent event) {
        System.out.println("Class loaded: " + event.referenceType().name());
    }

    private void handleMethodEntry(MethodEntryEvent event) {
        if (!FIB_METHOD.equals(event.method().name())) {
            return;
        }

        ThreadReference thread = event.thread();
        StackFrame frame;
        try {
            frame = thread.frame(0);
        } catch (IncompatibleThreadStateException e) {
            return;
        }

        String nStr = getLocalVariableValue(frame, PARAM_NAME);
        int n = nStr.matches("\\d+") ? Integer.parseInt(nStr) : -1;

        String callId = UUID.randomUUID().toString();
        String parentId = callStack.isEmpty() ? null : callStack.peek();

        CallNode node = new CallNode(callId, n, parentId);
        callMap.put(callId, node);
        callStack.push(callId);

        if (parentId != null) {
            CallNode parent = callMap.get(parentId);
            if (parent != null) {
                parent.children.add(node);
            }
        }

        System.out.printf("→ fibonacci(%d) [id=%s]%n", n,
                callId.substring(0, 8));
    }

    private void handleMethodExit(MethodExitEvent event) {
        if (!FIB_METHOD.equals(event.method().name())) {
            return;
        }

        String callId = callStack.pop();
        CallNode node = callMap.get(callId);
        if (node == null) {
            return;
        }

        if (event.returnValue() instanceof PrimitiveValue pv) {
            node.returnValue = pv.longValue();
        }

        System.out.printf("← fibonacci(%d) = %d [id=%s]%n", node.n,
                node.returnValue, callId.substring(0, 8));
    }

    private String getLocalVariableValue(StackFrame frame, String varName) {
        try {
            for (LocalVariable var : frame.location().method().variables()) {
                if (varName.equals(var.name())) {
                    Value val = frame.getValue(var);
                    return val != null ? val.toString() : "?";
                }
            }
        } catch (AbsentInformationException ignored) {
        }
        return "?";
    }

    private void generateGraph() {
        // Знайдемо кореневий виклик (той, у кого parentId == null)
        CallNode root = callMap.values().stream()
                .filter(node -> node.parentId == null)
                .findFirst()
                .orElse(null);

        if (root == null) {
            System.out.println("No fibonacci calls detected.");
            return;
        }

        // Рекурсивно будуємо граф
        buildGraphNode(root);

        try {
            Graphviz.fromGraph(graph)
                    .scale(2.5)
                    .render(Format.DOT)
                    .toFile(new File("fib_tree_debugger.dot"));
            System.out.println("Call tree saved to: fib_tree_debugger.dot");
        } catch (IOException e) {
            System.err.println("Failed to save graph: " + e.getMessage());
        }
    }

    private MutableNode buildGraphNode(CallNode node) {
        String label = node.returnValue >= 0
                ? String.format("fib(%d)\\n=%d", node.n, node.returnValue)
                : String.format("fib(%d)", node.n);

        MutableNode graphNode = mutNode(node.id).add(Label.of(label));

        for (CallNode child : node.children) {
            MutableNode childGraphNode = buildGraphNode(child);
            graphNode.addLink(childGraphNode);
        }

        graph.add(graphNode);
        return graphNode;
    }

    // Дерево викликів
    private static class CallNode {
        final String id;
        final int n;
        final String parentId;
        final List<CallNode> children = new ArrayList<>();
        long returnValue = -1;

        CallNode(String id, int n, String parentId) {
            this.id = id;
            this.n = n;
            this.parentId = parentId;
        }
    }
}