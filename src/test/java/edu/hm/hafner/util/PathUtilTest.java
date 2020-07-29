package edu.hm.hafner.util;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.*;

/**
 * Tests the class {@link PathUtil}.
 *
 * @author Ullrich Hafner
 */
@SuppressFBWarnings("DMI")
class PathUtilTest extends ResourceTest {
    private static final String NOT_EXISTING = "/should/not/exist";
    private static final String ILLEGAL = "\0 Null-Byte";
    private static final String FILE_NAME = "relative.txt";
    private static final String NOT_EXISTING_RELATIVE = "not-existing-relative";

    @Test
    void shouldConvertToAbsolute() {
        PathUtil pathUtil = new PathUtil();

        assertThat(pathUtil.createAbsolutePath(null, FILE_NAME)).isEqualTo(FILE_NAME);
        assertThat(pathUtil.createAbsolutePath("", FILE_NAME)).isEqualTo(FILE_NAME);
        assertThat(pathUtil.createAbsolutePath("/", FILE_NAME)).isEqualTo("/" + FILE_NAME);
        assertThat(pathUtil.createAbsolutePath("/tmp", FILE_NAME)).isEqualTo("/tmp/" + FILE_NAME);
        assertThat(pathUtil.createAbsolutePath("/tmp/", FILE_NAME)).isEqualTo("/tmp/" + FILE_NAME);
    }

    @Test
    void shouldConvertToRelative() {
        PathUtil pathUtil = new PathUtil();

        Path absolutePath = getResourceAsFile(FILE_NAME);

        assertThat(pathUtil.getRelativePath(absolutePath.getParent(), FILE_NAME)).isEqualTo(FILE_NAME);
        assertThat(pathUtil.getRelativePath(FILE_NAME)).isEqualTo(FILE_NAME);
        assertThat(pathUtil.getRelativePath(absolutePath.getParent(), NOT_EXISTING_RELATIVE)).isEqualTo(
                NOT_EXISTING_RELATIVE);

        assertThat(pathUtil.getRelativePath(absolutePath.getParent().getParent(), "util/" + FILE_NAME)).isEqualTo(
                "util/" + FILE_NAME);

        assertThat(pathUtil.getRelativePath(absolutePath.getParent(), absolutePath.toString())).isEqualTo(FILE_NAME);
        assertThat(pathUtil.getRelativePath(Paths.get(NOT_EXISTING), absolutePath.toString())).isEqualTo(
                pathUtil.getAbsolutePath(absolutePath));
        assertThat(pathUtil.getRelativePath(Paths.get(NOT_EXISTING), FILE_NAME)).isEqualTo(FILE_NAME);

        assertThat(pathUtil.getRelativePath(NOT_EXISTING, FILE_NAME)).isEqualTo(FILE_NAME);
    }

    @Test
    void shouldConvertNotResolvedToRelative() {
        PathUtil pathUtil = new PathUtil();

        Path absolutePath = getResourceAsFile(FILE_NAME);

        assertThat(pathUtil.getRelativePath(absolutePath.getParent().getParent(), "./util/" + FILE_NAME)).isEqualTo(
                "util/" + FILE_NAME);
        assertThat(pathUtil.getRelativePath(absolutePath.getParent().getParent(),
                "../hafner/util/" + FILE_NAME)).isEqualTo("util/" + FILE_NAME);
    }

    @Test
    void shouldSkipAlreadyAbsoluteOnUnix() {
        assumeThatTestIsRunningOnUnix();

        PathUtil pathUtil = new PathUtil();

        assertThat(pathUtil.createAbsolutePath("/tmp/", "/tmp/file.txt")).isEqualTo("/tmp/file.txt");
    }

    @Test
    void shouldSkipAlreadyAbsoluteOnWindows() {
        assumeThatTestIsRunningOnWindows();

        PathUtil pathUtil = new PathUtil();

        assertThat(pathUtil.createAbsolutePath("C:\\tmp", "C:\\tmp\\file.txt")).isEqualTo("C:/tmp/file.txt");
    }

    @Test
    void shouldNormalizeDriveLetter() {
        PathUtil pathUtil = new PathUtil();

        assertThat(pathUtil.getAbsolutePath("c:\\tmp")).isEqualTo("C:/tmp");
    }

    @Test
    void shouldStayInSymbolicLinks() throws IOException {
        Path current = Paths.get(".");
        Path real = current.toRealPath();
        Path realWithSymbolic = current.toRealPath(LinkOption.NOFOLLOW_LINKS);

        assumeThat(real).as("Current working directory path is not based on symbolic links").isNotEqualTo(realWithSymbolic);

        String fromUtil = new PathUtil().getAbsolutePath(current);
        String unixStyle = realWithSymbolic.toString().replace('\\', '/');
        assertThat(fromUtil).isEqualTo(unixStyle);
    }
}
