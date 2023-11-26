import io.github.aparx.bufig.ConfigPath;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-23 22:23
 * @since 1.0
 */
public class TestConfigPath {

  @Test
  public void of() {
    Assertions.assertArrayEquals(new String[]{"a", "b", "c"},
        ConfigPath.of(new String[]{"a", "b", "c"}).toArray());
    Assertions.assertArrayEquals(new String[0], ConfigPath.of(new String[0]).toArray());
    Assertions.assertArrayEquals(new String[]{"a"}, ConfigPath.of("a").toArray());
    Assertions.assertArrayEquals(new String[]{"a", "b"}, ConfigPath.of("a", "b").toArray());
    Assertions.assertArrayEquals(new String[0], ConfigPath.of().toArray());

    Assertions.assertArrayEquals(new String[]{"a", "b"},
        ConfigPath.of("a", " ", "b").toArray());
    Assertions.assertArrayEquals(new String[]{"a", "b", "c"},
        ConfigPath.of("a", " ", "b", "c", " ".repeat(3)).toArray());
  }

  @Test
  public void parse() {
    Assertions.assertArrayEquals(new String[]{"a", "b", "c"},
        ConfigPath.parse("a.b.c", '.').toArray());
    Assertions.assertArrayEquals(new String[]{"a", "b", "c"},
        ConfigPath.parse("a..b.c", '.').toArray());
    Assertions.assertArrayEquals(new String[]{"a", "b", "c"},
        ConfigPath.parse("..a..b.c", '.').toArray());
    Assertions.assertArrayEquals(new String[]{"a"},
        ConfigPath.parse("a", '.').toArray());
    Assertions.assertArrayEquals(new String[0],
        ConfigPath.parse("", '.').toArray());
    Assertions.assertArrayEquals(new String[]{"b", "c"},
        ConfigPath.parse("b.c", '.').toArray());
    Assertions.assertArrayEquals(new String[]{"b.c"},
        ConfigPath.parse("b.c", ',').toArray());
    Assertions.assertArrayEquals(new String[]{"b.c", "d.e"},
        ConfigPath.parse("b.c,d.e", ',').toArray());
  }

  @Test
  public void get() {
    Assertions.assertEquals("d", ConfigPath.of("a", "b", "c", "d").get(3));
    Assertions.assertEquals("a", ConfigPath.of("a", "b").get(0));
    Assertions.assertEquals("b", ConfigPath.of("a", "b").get(1));
    Assertions.assertThrows(IndexOutOfBoundsException.class,
        () -> ConfigPath.of("a", "b").get(3));
    Assertions.assertThrows(IndexOutOfBoundsException.class,
        () -> ConfigPath.of("a", "b").get(-1));
    Assertions.assertThrows(IndexOutOfBoundsException.class,
        () -> ConfigPath.of().get(1));
  }

  @Test
  public void join() {
    Assertions.assertEquals("a.b.c", ConfigPath.of("a", "b", "c").join('.'));
    Assertions.assertEquals("a,b,c", ConfigPath.of("a", "b", "c").join(','));
    Assertions.assertEquals("hox.a.b.c", ConfigPath.of("hox.a", "b", "c").join('.'));
    Assertions.assertEquals("a", ConfigPath.of("a").join('.'));
    Assertions.assertEquals("", ConfigPath.of().join('.'));
  }

  @Test
  public void add() {
    // add(ConfigPath)
    Assertions.assertArrayEquals(new String[]{"a", "b", "c"},
        ConfigPath.of("a", "b").add(ConfigPath.of("c")).toArray());
    Assertions.assertArrayEquals(new String[]{"a", "b"},
        ConfigPath.of("a", "b").add(ConfigPath.of()).toArray());
    Assertions.assertArrayEquals(new String[]{"a", "a", "b"},
        ConfigPath.of("a").add(ConfigPath.of("a", "b")).toArray());
    Assertions.assertArrayEquals(new String[]{"a", "b"},
        ConfigPath.of("a").add(ConfigPath.of("b")).toArray());
    Assertions.assertArrayEquals(new String[]{"a"},
        ConfigPath.of().add(ConfigPath.of("a")).toArray());

    // add(String) / add(String[])
    Assertions.assertArrayEquals(new String[]{"a", "b", "c"},
        ConfigPath.of("a", "b").add("c").toArray());
    Assertions.assertArrayEquals(new String[]{"a", "b"},
        ConfigPath.of("a", "b").add("").toArray());
    Assertions.assertArrayEquals(new String[]{"a", "a", "b"},
        ConfigPath.of("a").add(new String[]{"a", "b"}).toArray());
    Assertions.assertArrayEquals(new String[]{"a", "b"},
        ConfigPath.of("a").add(new String[]{"b"}).toArray());
    Assertions.assertArrayEquals(new String[]{"a"},
        ConfigPath.of().add(new String[]{"a"}).toArray());
  }

  @Test
  public void subpath() {
    ConfigPath path = ConfigPath.of("a", "b", "c", "d", "e", "f");
    Assertions.assertArrayEquals(new String[]{"a", "b"},
        path.subpath(0, 2).toArray());
    Assertions.assertArrayEquals(new String[]{"b"},
        path.subpath(1, 2).toArray());
    Assertions.assertArrayEquals(new String[0],
        path.subpath(2, 2).toArray());
    Assertions.assertArrayEquals(new String[]{"d", "e"},
        path.subpath(3, 5).toArray());
    Assertions.assertArrayEquals(path.toArray(),
        path.subpath(0, path.length()).toArray());
  }


}
