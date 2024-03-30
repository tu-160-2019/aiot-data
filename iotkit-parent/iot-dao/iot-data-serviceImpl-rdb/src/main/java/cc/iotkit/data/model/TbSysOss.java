package cc.iotkit.data.model;

import cc.iotkit.model.system.SysOss;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OSS对象存储对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_oss")
@ApiModel(value = "OSS对象存储对象")
@AutoMapper(target = SysOss.class)
public class TbSysOss extends BaseEntity {

    /**
     * 对象存储主键
     */
    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "对象存储主键")
    private Long id;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantId;

    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String fileName;

    /**
     * 原名
     */
    @ApiModelProperty(value = "原名")
    private String originalName;

    /**
     * 文件后缀名
     */
    @ApiModelProperty(value = "文件后缀名")
    private String fileSuffix;

    /**
     * URL地址
     */
    @ApiModelProperty(value = "URL地址")
    private String url;

    /**
     * 服务商
     */
    @ApiModelProperty(value = "服务商")
    private String service;

}
