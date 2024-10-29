# DcBotHelper
This library helps you build your own Discord bot faster!
## 🛠️ Adding to Your Project
Unfortunately, we do not yet support implementation via Maven or Gradle. However, you can add the JAR file to your project via our releases and use the DiscordBotHelper.
### IntelliJ
#### Download and add to project
First, download the JAR here: [Latest Release](https://github.com/lil-aleks/DcBotHelper/releases/latest)
Now create a folder called “libs” and paste the JAR there. That folder should be in your project root.
#### Adding necessary dependencies
If you're using Maven, add the following to your pom.xml:
```xml
<dependency>
    <groupId>net.dv8tion</groupId>
    <artifactId>JDA</artifactId>
    <version>5.1.1</version>
</dependency>
<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.10.2</version>
</dependency>
```
#### Adding the downloaded library to the project
Go to 'File' in the top menu and select 'Project Structure', or press Ctrl+Alt+Shift+S.
Go to 'Libraries', click on the green plus icon, select 'Java', and then choose your JAR file from the 'libs' folder.
Don't forget to click on 'Apply', and there you go! You can now start using our DiscordBotHelper.
#### Warning
Whenever you change your `pom.xml` and reload Maven, the library might no longer be recognized. If this happens, you’ll need to add it again in the Project Structure. In that case, you’ll need to add it again in the Project Structure.
## 🚀 Get started
When creating a bot at the [Discord Developer Portal](https://discord.com/developers), don’t forget to enable all privileged gateway intents.

### Starting the bot
#### Prepared method
You can start your bot using the following method:
```java 
Bot.startBot(YOUR_TOKEN_HERE); // Enter your token
```
#### Custom method
Or create your own method and add the EventHandler to the EventListeners.
```java
JDA bot = JDABuilder.createDefault(YOUR_TOKEN_HERE) // Enter your token
    .setStatus(OnlineStatus.ONLINE) // optional
    .setActivity(Activity.playing("nothing")) //optional
    .setChunkingFilter(ChunkingFilter.ALL)
    .setMemberCachePolicy(MemberCachePolicy.ALL)
    .enableIntents(
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_VOICE_STATES,
        GatewayIntent.DIRECT_MESSAGE_TYPING,
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.MESSAGE_CONTENT,
        GatewayIntent.GUILD_INVITES
    )
    .addEventListeners(
        new EventHandler() // DON'T FORGET THIS LINE!
    )
    .build().awaitReady();
```
### Examples
Here are some examples on how to listen to events.
#### Slash-Commands:
Add `@OnCommand` with a `name` and `description` parameter to a `public static` method and add the `SlashCommandInteractionEvent event` parameter:
```java
// /ping command that responds with 'Pong'!
@OnCommand(name = "ping", description = "ping the bot")
public static void example(SlashCommandInteractionEvent event)
{
    event.reply("Pong!").setEphemeral(true).queue();
}
```
You can also add options to the command by including `@CmdOption`s in the `options` parameter of the `@OnCommand` decorator:
```java
// /time command which response with the time a Member is joined the server!
@OnCommand(name = "time", description = "when did a user  join!", options = {
        @CmdOption(name = "user", description = "who?", type = OptionType.USER, required = true)
})
public static void slashCmd(SlashCommandInteractionEvent event)
{
    Member member = event.getOption("user").getAsMember();
    event.reply(member.getAsMention() + " joined on " + member.getTimeJoined()).setEphemeral(true).queue();
}
```
Those methods will automatically create a slash command and execute whenever the command is used.
#### Buttons:
Add `@OnButton` with an `id` parameter  to a `public static` method and add the `ButtonInteractionEvent event` parameter:
```java
// When a member presses a button with the id 'verify' they will receive a specific role on the server.
@OnButton(id = "verify")
public static void verifyMember(ButtonInteractionEvent event)
{
    Member member = event.getMember();
    Role verifyRole = event.getGuild().getRoleById("1234567891234567891");
    event.getGuild().addRoleToMember(member, verifyRole).queue();
    event.reply("You are now verified!").setEphemeral(true).queue();
}
```
This method will execute whenever a button with that id is pressed.
#### Message receive
Add `@OnMessageReceived` on a `public static` method and add the `MessageReceivedEvent event` parameter:
```java
// React with a :speech_balloon: emoji when any message is received.
@OnMessageReceived()
public static void reactToAnyMessage(MessageReceivedEvent event)
{
    event.getMessage().addReaction(Emoji.fromFormatted(":speech_balloon:")).queue();
}
```
You can also make a method react to a specific message by adding `message`(Case is ignored in the string):
```java
// Respond whenever someone sends 'Hello'. (Case is ignored in the string)
@OnMessageReceived(message = "hello")
public static void responseOnHello(MessageReceivedEvent event)
{
    event.getMessage().reply("Hello " + event.getMember().getAsMention()).queue();
}
```
This methods will execute every time someone sends a message.
#### More Events
A whole class full of all the events (with examples) which are supported can be found here: [MainTest.java](https://github.com/lil-aleks/DcBotHelper/blob/main/src/test/java/MainTest.java)
### MORE SOON.
