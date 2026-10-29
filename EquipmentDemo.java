public class EquipmentDemo {

    public static void main(String[] args) {

        Container<Equipment> equipment = new Container<>();

        equipment.add(new SolarPanel());
        equipment.add(new OxygenGenerator());
        equipment.add(new Camera());

        System.out.println("Mission equipment:");

        for (int i = 0; i < equipment.size(); i++) {
            Equipment item = equipment.get(i);
            item.operate();
        }

        System.out.println("\nTesting damaged equipment:");

        Camera camera = new Camera();
        camera.damage(100);

        try {
            camera.operate();
        } catch (MissionException e) {
            System.out.println("Mission warning: " + e.getMessage());
        }
    }
}