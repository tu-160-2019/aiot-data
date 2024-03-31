package cc.iotkit.data.config.id;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SnowflakeIdGenerator implements IdentifierGenerator {

  @PostConstruct
  public void snowflakeIdGenerator() {
    // TODO: 2023/6/12 从配置文件中读取
    IdGeneratorOptions options = new IdGeneratorOptions((short) 1);
    YitIdHelper.setIdGenerator(options);
  }

  @Override
  public Long nextId(Object entity) {
    return YitIdHelper.nextId();
  }
}