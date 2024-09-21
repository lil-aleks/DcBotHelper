import com.bothelper.event.interaction.*;
import com.bothelper.main.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Widget;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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

    // /ping command which response with "Pong"!
    @OnCommand(name = "ping", description = "ping the bot")
    public static void example(SlashCommandInteractionEvent event)
    {
        event.reply("Pong!").setEphemeral(true).queue();
    }

    // /time command which response with the time a Member is joined the server!
    @OnCommand(name = "time", description = "when did a user  join!", options = {
            @CmdOption(name = "user", description = "who?", type = OptionType.USER, required = true)
    })
    public static void slashCmd(SlashCommandInteractionEvent event)
    {
        Member member = event.getOption("user").getAsMember();
        event.reply(member.getAsMention() + " joined on " + member.getTimeJoined()).setEphemeral(true).queue();
    }

    // whenever a member presses a button with the id "verify"
    // the member will get a specific role on the server.
    @OnButton(id = "verify")
    public static void verifyMember(ButtonInteractionEvent event)
    {
        Member member = event.getMember();
        Role verifyRole = event.getGuild().getRoleById("1234567891234567891");
        event.getGuild().addRoleToMember(member, verifyRole).queue();
        event.reply("You are now verified!").setEphemeral(true).queue();
    }

    // react with a :speech_balloon: when a messsage is received.
    // I really couldn't think of any other example.
    @OnMessageReceived()
    public static void reactToAnyMessage(MessageReceivedEvent event)
    {
        event.getMessage().addReaction(Emoji.fromFormatted(":speech_balloon:")).queue();
    }

    // response whenever someone sends "Hello". (Case is ignored in the string)
    @OnMessageReceived(message = "hello")
    public static void responseOnHello(MessageReceivedEvent event)
    {
        event.getMessage().reply("Hello " + event.getMember().getAsMention()).queue();
    }

    // whenever someone select something from an EntitySelection
    // with the id "move-user-channel" the user will move to
    // the selected Voice-Channel.
    @OnEntitySelection(id = "move-user-channel")
    public static void moveUser(EntitySelectInteractionEvent event)
    {
        Channel channel = (Channel) event.getValues().get(0);
        if (!(channel instanceof AudioChannel))
        {
            event.reply("You need to select a Voice-Channel.").setEphemeral(true).queue();
            return;
        }
        if (!event.getMember().getVoiceState().inAudioChannel())
        {
            event.reply("You need to be in a Voice-Channel.").setEphemeral(true).queue();
            return;
        }
        event.getGuild().moveVoiceMember(event.getMember(), (AudioChannel) channel).queue();
    }

    // whenever someone select something from a StringSelection
    // with the id "favorite-fruit" the user will get a response
    // that the bot also like the fruit the user chose.
    @OnStringSelection(id = "favorite-fruit")
    public static void selectedFavFruit(StringSelectInteractionEvent event)
    {
        event.reply(event.getValues().get(0) + " is my fav fruit too!").setEphemeral(true).queue();
    }
}
