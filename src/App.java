public class App {
    public static void main(String[] args) throws Exception {
        // manual loading
        // HelloStrategy english = new EnglishHello();
        // HelloWorldService service = new HelloWorldService(english);
        // System.out.println(service.greet("Vikram"));
        // service.setStrategy(new SpanishHello());
        // System.out.println(service.greet("Vikram"));
        // service.setStrategy(new HindiHello());
        // System.out.println(service.greet("Vikram"));

        // Dynamic language loading from precompiled plugins folder
        // HelloStrategy dynamicHello = LanguageLoader.loadLanguage("SpanishHello");
        // if(dynamicHello !=null ) {
        //     HelloWorldService service = new HelloWorldService(dynamicHello);
        //     System.out.println(service.greet("Vikram!"));
        // } else {
        //     System.err.println("Failed to load language... :(");
        // }

        // Using server
        HelloApiServer.start();

    }
}
