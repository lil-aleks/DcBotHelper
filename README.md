# DcBotHelper
This will help you to build your own Discord bot faster!
## 🛠️ Implement to your project
Unfortunately, we do not yet support implementation via Maven or Gradle. However, you can add the JAR file to your project via our releases and use the DiscordBotHelper.
### Intellij
#### Download and add to project
First download the JAR here: [Latest Release](https://github.com/lil-aleks/DcBotHelper/releases/latest)
Now create a folder called “libs” and paste the JAR there. That folder should be in your project root.
#### Adding necessary dependencies
If you're using maven add this to your `pom.xml`:
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
Now go to 'File' in the top right and select 'Project Structure', or press Ctrl+Alt+Shift+S.
Go to 'Libraries', click on the green plus icon, select 'Java', and then choose your JAR file from the 'libs' folder.
Don't forget to click on 'Apply,' and there you go! You can now start using our DiscordBotHelper.
#### Warning
Whenever you change your `pom.xml` and reload Maven, it may happen that the library is no longer recognized. In that case, you’ll need to add it again in the Project Structure.
## 🚀 Get started
When creating a bot at the [Discord Developer Portal](https://discord.com/developers), please don´t forget to toggle every privileged gateway intent on.

### Starting the bot
#### Prepared method
You can start your bot using this method:
```java 
Bot.startBot(YOUR_TOKEN_HERE); // Enter your token
```
#### Custom method
Or create your own method and add the EventHandler to the EventLiteners.
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
        new EventHandler() // DON'T FORGET THIS LINE
    )
    .build().awaitReady();
```
### Examples
Here are some examples how to make some events.
#### Slash-Command interactions:
Add `@OnCommand` with a `name` and `description` to a `public static` method and add `SlashCommandInteractionEvent event` as the parameter:
```java
// /ping command which response with "Pong"!
@OnCommand(name = "ping", description = "ping the bot")
public static void example(SlashCommandInteractionEvent event)
{
    event.reply("Pong!").setEphemeral(true).queue();
}
```
You can also add options to the command by adding as many `@CmdOption` to `options` as you want:
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
#### Button interactions:
Add `@OnButton` with a `id`  to a `public static` method and add `ButtonInteractionEvent event` as the parameter:
```java
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
```
This method will execute whenever a button with that id is pressed.
#### Message receive
Add `@OnMessageReceived` on a `public static` method and add `MessageReceivedEvent event` as the parameter:
```java
// react with a :speech_balloon: when a messsage is received.
// I really couldn't think of any other example.
@OnMessageReceived()
public static void reactToAnyMessage(MessageReceivedEvent event)
{
    event.getMessage().addReaction(Emoji.fromFormatted(":speech_balloon:")).queue();
}
```
You can also make a method react to a specific message by adding `message`(Case is ignored in the string):
```java
// response whenever someone sends "Hello". (Case is ignored in the string)
@OnMessageReceived(message = "hello")
public static void responseOnHello(MessageReceivedEvent event)
{
    event.getMessage().reply("Hello " + event.getMember().getAsMention()).queue();
}
```
This methods will execute every time someone sends a message.
#### More Events
A hole class full of all the events with examples which are supported can be found here: [MainTest.java](https://github.com/lil-aleks/DcBotHelper/blob/main/src/test/java/MainTest.java)
### MORE SOON.