# FakeEntityLib
[![](https://jitpack.io/v/lewysDavies/FakeEntityLib.svg)](https://jitpack.io/#lewysDavies/FakeEntityLib)

Fake Minecraft Entities Using ProtocolLib & The Minecraft Protocol Directly. Supports Spigot 1.10 to 1.17.

### Clone the repository
Maven:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.lewysDavies</groupId>
    <artifactId>FakeEntityLib</artifactId>
    <version>[jitpack release]</version>
</dependency>
```

Gradle:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.lewysDavies:FakeEntityLib:[jitpack release]'
}
```

### Example code:
Spawning a fake armour stand and showing it to the player
```Java
FakeStand fakeStand = new FakeStand(location.getWorld(), location.getX(), location.getY(), location.getZ());
fakeStand.show(player)
//fakestand.setGloballyVisible(true)
```
