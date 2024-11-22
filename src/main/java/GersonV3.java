import io.github.ollama4j.exceptions.OllamaBaseException;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;


public class GersonV3 implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final  GersonTools gersontools;
    Ollama ollama = new Ollama();

    public GersonV3 (String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
        gersontools = new GersonTools();
    }

    @Override
    public void consume(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String response = null;

            try {
                response = ollama.getOllamaResponse(message_text);
            } catch (OllamaBaseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            SendMessage message = SendMessage // Create a message object
                    .builder()
                    .chatId(chat_id)
                    .text(response)
                    .build();
            try {
                telegramClient.execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
