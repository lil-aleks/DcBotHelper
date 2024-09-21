import com.bothelper.event.interaction.CmdOption;
import com.bothelper.event.interaction.OnCommand;
import com.bothelper.main.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainTest
{
    public static void main(String[] args) throws InterruptedException
    {
        String token;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/Tests/TestBotToken.txt"))))
        {
            token = reader.readLine();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        Bot.startBot(token);
    }

    @OnCommand(name = "time", description = "when did a user  join!", options = {
            @CmdOption(name = "user", description = "who?", type = OptionType.USER, required = true)
    })
    public static void slashCmd(SlashCommandInteractionEvent event)
    {
        Member member = event.getOption("user").getAsMember();
        event.reply(member.getAsMention() + " joined on" + member.getTimeJoined()).setEphemeral(true).queue();
    }
}
