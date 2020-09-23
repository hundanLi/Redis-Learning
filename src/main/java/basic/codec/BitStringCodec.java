package basic.codec;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;

import java.nio.ByteBuffer;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/20 17:41
 */
public class BitStringCodec extends StringCodec {
    @Override
    public String decodeValue(ByteBuffer bytes) {
        StringBuilder bits = new StringBuilder(bytes.remaining() * 8);
        while (bytes.remaining() > 0) {
            byte b = bytes.get();
            for (int i = 0; i < 8; i++) {
                bits.append(Integer.valueOf(b >>> i & 1));
            }
        }
        return bits.toString();
    }

    public static void main(String[] args) {
        RedisClient client = RedisClient.create("redis://localhost:6379/0");
        StatefulRedisConnection<String, String> connection = client.connect(new BitStringCodec());
        RedisCommands<String, String> redis = connection.sync();
        String key = "bit";
        redis.setbit(key, 0, 1);
        redis.setbit(key, 1, 1);
        redis.setbit(key, 2, 0);
        redis.setbit(key, 3, 0);
        redis.setbit(key, 4, 0);
        redis.setbit(key, 5, 1);

        assert "00100011".equals(redis.get(key));
    }
}
