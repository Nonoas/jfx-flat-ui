package github.nonoas.jfx.flat.ui;

/**
 * @author Nonoas
 * @version 1.0.0
 * @date 2025/9/27
 * @since 1.0.0
 */
import java.io.Closeable;
import java.io.IOException;

public interface Resource extends Closeable {
    /**
     * 释放资源，例如关闭数据库连接、文件流等。
     */
    void release() throws Exception;

    @Override
    default void close() throws IOException {
        try {
            release();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
