public class HelloWorldService {
    private HelloStrategy strategy;
    private final GreetingLogger logger = new GreetingLogger();

    public HelloWorldService(HelloStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(HelloStrategy strategy) {
        this.strategy = strategy;
    }
    public String greet(String name) {
        String greeting = strategy.sayHello(name);
        logger.log(greeting);
        return greeting;
    }

}
