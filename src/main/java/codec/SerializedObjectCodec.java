package codec;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * JDK 编解码器
 *
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/20 15:47
 */
public class SerializedObjectCodec implements RedisCodec<String, Object> {
    private final Charset charset = Charset.forName(StandardCharsets.UTF_8.name());

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return charset.decode(bytes).toString();
    }

    @Override
    public Object decodeValue(ByteBuffer bytes) {
        try {
            byte[] array = new byte[bytes.remaining()];
            bytes.get(array);
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(array));
            return is.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return charset.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(Object value) {

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bytes);
            os.writeObject(value);
            return ByteBuffer.wrap(bytes.toByteArray());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Student implements Serializable {
        private int id;
        private String name;
        private int age;
    }

    public static void main(String[] args) {
        RedisClient client = RedisClient.create("redis://localhost:6379/0");
        StatefulRedisConnection<String, Object> connection =
                client.connect(new SerializedObjectCodec());
        RedisCommands<String, Object> commands = connection.sync();

        Student student = new Student(1, "hundanli", 22);
        commands.set("stu-" + student.getId(), student);
        Student s = (Student) commands.get("stu-" + student.getId());
        assert s.equals(student);

    }
}
