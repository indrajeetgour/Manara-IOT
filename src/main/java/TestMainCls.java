public class TestMainCls {
    public static void main(String[] args) {













        MappingClass obj = new MappingClass();
        obj.put("Indrajeet", 1);
        obj.put("Nidhi", 2);
        Integer int1 = obj.put("Indrajeet", 3);
        Integer int2 = obj.put("Nidhi", 4);
        System.out.println("Printing obj values : " + obj.toString());
        System.out.println("Return int val :" + int1 + " : " + int2);
    }
}
