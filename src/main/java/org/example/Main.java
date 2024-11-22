
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
/
public class Main {

    public static void main(String[] args)  {
        String botToken = "7490282371:AAGdFdwd5zgNKlADMx_VXcyAc7pRK6oX9DY";
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new GersonV3(botToken));
            System.out.println("MyAmazingBot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}