public class IDGenerator{
    private static int clientCounter = 1;
    private static int acountCounter = 1;

    public static String generateClientID(){
        return "C" + clientCounter++;
    }
    public static String generateAccountID(){
        return "A" + acountCounter++;
    }
}
