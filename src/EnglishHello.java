public class EnglishHello implements HelloStrategy {

    @Override
    public String sayHello(String name) {
        return "Hello, "+name+"!";
    }

}
