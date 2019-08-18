public class App {
    public static void  main(String [] args) {
        System.out.println("hello");
        Test test = new Test();
        int a=0;
        int b=3;
//        test.add(a,b);
        test.inc(a);
        a=a++;
        System.out.println(a);
    }
}
