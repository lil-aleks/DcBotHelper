import com.bothelper.event.interaction.*;
import com.bothelper.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.Objects;

public class MainTest
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        String token;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:/Tests/TestBotToken.txt"))))
        {
            token = reader.readLine();
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
        Member member = event.getOption("user", OptionMapping::getAsMember);
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


    // When a Modal is submitted with a value of the id "reason"
    // a ticket Channel will be crated in a category with the id
    // "1234567891234567891", a special role gets a
    @OnModal(id = "ticket-formular")
    public static void ticketFormular(ModalInteractionEvent event)
    {
        String input = event.getValue("reason").getAsString();
        String memberName = event.getMember().getEffectiveName();
        event.getGuild().getCategoryById("1234567891234567891").createTextChannel((memberName.endsWith("s") ? memberName : memberName + "´s") + " ticket")
                .addPermissionOverride(event.getGuild().getRoleById("1234567891234567891"), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .queue(
                        textChannel -> textChannel.sendMessage("Reason of " + memberName + ": " + input).queue()
                );
    }

    // This will send a welcome message to every
    // User that joins a Server.
    @OnGuild(action = Guild.JOIN)
    public static void joinAction(GuildMemberJoinEvent event)
    {
        event.getUser().openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage("Welcome to the Example-Server, have a nice time!").queue()
        );
    }

    // This will send a goodbye message to every
    // User that leaves or get kicked from a Server.
    @OnGuild(action = Guild.LEAVE)
    public static void leaveAction(GuildMemberRemoveEvent event)
    {
        event.getUser().openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage("We hope you had a nice time at the Example-Server!").queue()
        );
    }

    // Whenever a User joins a specific Channel a
    // temporary voice-channel will be crated.
    @OnGuild(action = Guild.VOICE_UPDATE)
    public static void tempChannel(GuildVoiceUpdateEvent event)
    {
        VoiceChannel tempChannel = event.getGuild().getVoiceChannelById("1234567891234567891");
        if (event.getChannelJoined().asVoiceChannel() == tempChannel)
        {
            event.getChannelJoined().getParentCategory().createVoiceChannel(event.getMember().getEffectiveName()).queue(
                    voiceChannel -> event.getGuild().moveVoiceMember(event.getMember(), voiceChannel).queue()
            );
        }
        // This is only a possible beginning of a temp-channel system...
    }

    // Deletes a created invite if the inviter don´t
    // have a specific role.
    @OnGuild(action = Guild.INVITE_CREATE)
    public static void createdInvite(GuildInviteCreateEvent event)
    {
        Member inviter = event.getGuild().getMember(event.getInvite().getInviter());
        Role inviterRole = event.getGuild().getRoleById("1234567891234567891");
        if (!inviter.getRoles().contains(inviterRole))
            event.getInvite().delete().queue();
    }

    // Do what ever you want when an invite is deleted.
    @OnGuild(action = Guild.INVITE_DELETE)
    public static void deletedInvite(GuildInviteDeleteEvent event)
    {
        // Sorry, no example here. Do what ever you want.
    }

    @OnGuild(action = Guild.JOIN)
    public static void botJoin(GuildJoinEvent event)
    {
        event.getGuild().getSystemChannel().sendMessage("Hello, I am the ExampleBot. Thanks for using me!").queue();
    }

    @OnGuild(action = Guild.LEAVE)
    public static void botLeave(GuildLeaveEvent event)
    {
        event.getGuild().getOwner().getUser().openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage("Sorry for not meeting your expectations.").queue()
        );
    }
}
