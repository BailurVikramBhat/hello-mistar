public class GermanHello implements HelloStrategy {

    @Override
    public String sayHello(String name) {
        return "Hallo " + name + "!";
    }

}
