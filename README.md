# 👉 Bufig - Bukkit configuration library
Advanced Bukkit configuration wrapper. Providing a way to easily integrate configurations using plain old Java objects, 
nested documentation, custom serialization and much more. Built with modularity in mind, which gives users the ability 
to alter the library's behaviour to their specific needs.

## Installation
[![](https://jitpack.io/v/aparx/bufig-library.svg)](https://jitpack.io/#aparx/bufig-library)

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
```xml
<dependency>
  <groupId>com.github.aparx</groupId>
  <artifactId>bufig-library</artifactId>
  <version>VERSION</version>
</dependency>
```
## Requirements
1. JDK 11 or newer
2. Bukkit 1.8.8 or newer (for the tested experience)
3. Shading of this library into your plugin

## Simple example
```java
  @Override
  public void onEnable() {
    // Refer to `ConfigHandlers` to create different types of handlers
    ConfigHandler<? super YamlConfig> handler = ConfigHandler.of(this);
    MainConfig mainConfig = new MainConfig(handler);
    mainConfig.load();
  }

  @ConfigId("main")
  @Document({
      "<============================>",
      "    Our main configuration    ",
      "<============================>"
  })
  public static class MainConfig extends ConfigObject {

    @ConfigMapping
    @Document("The primary prefix of our example plugin")
    public String prefix = "[Prefix] ";

    @ConfigMapping
    @Document("The (initial) spawning location of all new players")
    public Location spawnLocation = new Location(Bukkit.getWorld("world"), 30, 80, 30);

    @ConfigMapping("hourly.rotations")
    @Document({
        "The amount of rotations done by the sun per hour.",
        "The number must be less than 360."
    })
    public int rotations = 3;

    @ConfigMapping("hourly.particles")
    @Document("The amount of particles spawned per hour")
    public int particles = 3;

    public MainConfig(@NonNull ConfigHandler<?> handler) {
      super(handler);
    }
  }
```
Outputs following at `plugins/BufigExample/main.yml`:
```yaml
# <============================>
#     Our main configuration    
# <============================>

# The primary prefix of our example plugin
prefix: '[Prefix] '
# The (initial) spawning location of all new players
spawnLocation:
  world: world
  x: 30.0
  y: 80.0
  z: 30.0
  pitch: 0.0
  yaw: 0.0
hourly:
  # The amount of rotations done by the sun per hour.
  # The number must be less than 360.
  rotations: 3
  # The amount of particles spawned per hour
  particles: 3
```
