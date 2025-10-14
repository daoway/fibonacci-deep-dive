java -cp .;%JAVA_HOME%/lib/tools.jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 FibonacciTarget
