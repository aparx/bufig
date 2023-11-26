import io.github.aparx.bufig.ConfigPath;
import io.github.aparx.bufig.ConfigPaths;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-26 02:32
 * @since 1.0
 */
public class TestConfigPaths {

  @Test
  public void concat() {
    Assertions.assertArrayEquals(new String[]{"a", "b"},
        ConfigPaths.concat(ConfigPath.of("a"), ConfigPath.of("b")).toArray());

    Assertions.assertArrayEquals(new String[]{"a"},
        ConfigPaths.concat(ConfigPath.of("a"), ConfigPath.of()).toArray());

    Assertions.assertArrayEquals(new String[]{"a"},
        ConfigPaths.concat(ConfigPath.of(), ConfigPath.of("a")).toArray());
  }

  @Test
  public void parseConcat() {
    Assertions.assertArrayEquals(new String[]{"a", "b", "c"},
        ConfigPaths.parseConcat(ConfigPath.of("a"), "b.c", '.').toArray());

    Assertions.assertArrayEquals(new String[]{"b", "c", "a"},
        ConfigPaths.parseConcat("b.c", ConfigPath.of("a"), '.').toArray());

    Assertions.assertArrayEquals(new String[]{"a", "b", "d", "e"},
        ConfigPaths.parseConcat("a.b", ".d.e", '.').toArray());

    Assertions.assertArrayEquals(new String[]{"a", "b", "d"},
        ConfigPaths.parseConcat("a.b", "d. .", '.').toArray());

    Assertions.assertArrayEquals(new String[]{"a", "b"},
        ConfigPaths.parseConcat("a.b", " ", '.').toArray());
  }

  @Test
  public void parseConcatJoin() {
    Assertions.assertEquals("a.b.c", ConfigPaths.parseConcatJoin(ConfigPath.of("a", "b"), "c", '.'));
    Assertions.assertEquals("a.b.c", ConfigPaths.parseConcatJoin("a.b", "c", '.'));
    Assertions.assertEquals("a.b. c", ConfigPaths.parseConcatJoin("a.b", ". c", '.'));
    Assertions.assertEquals("a.b. c", ConfigPaths.parseConcatJoin("a.b", ConfigPath.of(" c"), '.'));
    Assertions.assertEquals("c.a.b", ConfigPaths.parseConcatJoin("c", "a.b", '.'));
  }


}
