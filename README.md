<div align="center">
  <br>
  <img src="assets/icon.svg" alt="Slash Command icon" width="256">
  <br>
  <br>
  <h1>Slash Commands</h1>
  <h4>An open source utility library for <a href="https://github.com/DV8FromTheWorld/JDA" target="_blank">JDA</a> to simplify the use of <a href="https://discord.com/developers/docs/interactions/slash-commands" target="_blank">Discord Slash Commands</a></h4>
</div>

<p align="center">
  <a href="https://jitpack.io/#azzerial/slash-commands">
    <img src="https://img.shields.io/jitpack/v/github/azzerial/slash-commands?color=green&label=JitPack">
  </a>
  <a href="https://github.com/Azzerial/slash-commands/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/azzerial/slash-commands?color=lightgray&label=License&logo=apache">
  </a>
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#installation">Installation</a> •
  <a href="#license">License</a>
</p>


<br>

## Features

A few of the things you can do with Slash Commands:

* Create and manage Slash Commands
* Assign callbacks to Slash Commands events (supports per [command path](https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/interactions/commands/CommandInteraction.html#getCommandPath()) callbacks)
* Assign callbacks to buttons
* Session system for interactions (with session store)

## How to Use

```java
@Slash.Tag("slash_cmd")
@Slash.Command(
    name = "slash-command",
    description = "A proof of concept Slash Command"
)
public final class SlashCommand {

    public static void main(String[] args) throws LoginException, InterruptedException {
        final JDA jda = JDABuilder.createDefault(...)
            .build()
            .awaitReady();
        final SlashClient slash = SlashClientBuilder.create(jda)
            .addCommand(new SlashCommand()) // register your commands
            .build();

        slash.getCommand("slash_cmd") // get a SlashCommand by it's @Slash.Tag
            .upsertGuild(...); // upsert as a guild Slash Command
    }

    @Slash.Handler
    public void callback(SlashCommandEvent event) {
        event.deferReply()
            .setContent("Hello World!")
            .queue();
    }
}
```

*For more examples and usage, please refer to the [playground module](playground/).*

## Installation 

This project uses [Jitpack](https://jitpack.io/#azzerial/slash-commands).

Latest release: [![](https://jitpack.io/v/azzerial/slash-commands.svg)](https://jitpack.io/#azzerial/slash-commands)

### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.azzerial.slash-commands:api:1.0'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.azzerial.slash-commands</groupId>
    <artifactId>api</artifactId>
    <version>1.0</version>
</dependency>
```

## License

This project is licensed under the [Apache License 2.0](LICENSE) © 2021 [Robin Mercier](https://github.com/azzerial/).

---

<p align="center">
  GitHub <a href="https://github.com/azzerial">@Azzerial</a>
</p>
