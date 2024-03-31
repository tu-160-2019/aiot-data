package cc.iotkit.data.model;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.model.notify.ChannelConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ChannelConfig2Mapper {

    @Mappings({
            @Mapping(target = "param", expression = "java(stingToParam(vo.getParam()))")
    })
    ChannelConfig toDto(TbChannelConfig vo);

    @Mappings({
            @Mapping(target = "param", expression = "java(paramToSting(dto.getParam()))")
    })
    TbChannelConfig toVo(ChannelConfig dto);

    default String paramToSting(ChannelConfig.ChannelParam param) {
        if (Objects.isNull(param)) {
            return null;
        }
        return JsonUtils.toJsonString(param);

    }

    default ChannelConfig.ChannelParam stingToParam(String param) {
        if (Objects.isNull(param)) {
            return null;
        }
        return JsonUtils.parse(param, ChannelConfig.ChannelParam.class);

    }
}
