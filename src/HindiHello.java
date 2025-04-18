public class HindiHello implements HelloStrategy{

    @Override
    public String sayHello(String name) {
        return "Namaste, "+name+"!";
    }
    

}
