package cc.iotkit.data.config.id;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author: Longjun.Tu
 * @description:
 * @date:created in 2023/5/18 10:20
 * @modificed by:
 */
@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {

  @PostConstruct
  public void snowflakeIdGenerator() {
    // TODO: 2023/6/12 从配置文件中读取
    IdGeneratorOptions options = new IdGeneratorOptions((short) 1);
    YitIdHelper.setIdGenerator(options);
  }

  @Override
  public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
    Field id = null;
    try {
      id = o.getClass().getDeclaredField("id");
      id.setAccessible(true);
      Object val = id.get(o);
      if (Objects.nonNull(val)){
        return (Serializable) val;
      }
    } catch (NoSuchFieldException | IllegalAccessException e) {

    }
    return YitIdHelper.nextId();
  }
}
