import java.util.ArrayList;

public class Station {

    private ArrayList<Equipment> equipmentList;

    public Station() {
        equipmentList = new ArrayList<Equipment>();
    }

    public void addEquipment(Equipment item) {
        equipmentList.add(item);
    }

    public void operateAll() {
        for (Equipment item : equipmentList) {
            try {
                checkCondition(item);
                item.operate();
            } catch (EquipmentFailureException e) {
                System.out.println("WARNING: " + e.getMessage());
            }
        }
    }

    private void checkCondition(Equipment item) throws EquipmentFailureException {
        if (item.getCondition() <= 0) {
            throw new EquipmentFailureException(item.getName() + " has failed and needs repair!");
        }
    }

    public ArrayList<Equipment> getBrokenEquipment() {
        ArrayList<Equipment> broken = new ArrayList<Equipment>();
        for (Equipment item : equipmentList) {
            if (item.needsRepair()) {
                broken.add(item);
            }
        }
        return broken;
    }

    public ArrayList<Equipment> getEquipmentList() {
        return equipmentList;
    }
}