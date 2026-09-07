
public class Main {
    public static void main(String[] args) {
        Container<String> strings = new Container<>();

        strings.add("hello");
        strings.add("world");

        addInteger(strings);

        String first = strings.get(0);
        String second = strings.get(1);
        String third = strings.get(2);

        System.out.println(first + " " + second + " " + third);
    }

    public static void addInteger(Container container) {
        container.add(123);
    }
}

