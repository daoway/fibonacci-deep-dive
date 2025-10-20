public class PassByValueExample {
    public static void main(String[] args) {
        int number = 10;
        StringBuilder sb = new StringBuilder("Hello");

        System.out.println("Before: number = " + number + ", sb = " + sb);

        changeNumber(number);
        modifyStringBuilder(sb);
        reassignStringBuilder(sb);

        System.out.println("After: number = " + number + ", sb = " + sb);
    }

    public static void changeNumber(int x) {
        x = 20; // Modifying the copy of the primitive value
    }

    public static void modifyStringBuilder(StringBuilder builder) {
        builder.append(", World!"); // Modifying the object's state
    }

    public static void reassignStringBuilder(StringBuilder builder) {
        builder = new StringBuilder("New String"); // Reassigning the reference
    }
}
