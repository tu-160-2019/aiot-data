/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.SpringUtils;
import cc.iotkit.data.ICommonData;
import cc.iotkit.data.manager.*;
import cc.iotkit.data.system.*;
import cc.iotkit.model.Id;
import cc.iotkit.model.OauthClient;
import cc.iotkit.model.UserInfo;
import cc.iotkit.model.device.DeviceGroup;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.VirtualDevice;
import cc.iotkit.model.notify.Channel;
import cc.iotkit.model.notify.ChannelConfig;
import cc.iotkit.model.notify.ChannelTemplate;
import cc.iotkit.model.notify.NotifyMessage;
import cc.iotkit.model.plugin.PluginInfo;
import cc.iotkit.model.product.Category;
import cc.iotkit.model.product.Product;
import cc.iotkit.model.product.ProductModel;
import cc.iotkit.model.product.ThingModel;
import cc.iotkit.model.rule.RuleInfo;
import cc.iotkit.model.rule.TaskInfo;
import cc.iotkit.model.space.Home;
import cc.iotkit.model.space.Space;
import cc.iotkit.model.space.SpaceDevice;
import cc.iotkit.model.system.*;
import cc.iotkit.temporal.IDbStructureData;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Service
public class ExampleDataInit implements SmartInitializingSingleton {

    @Value("${init.data.flag:true}")
    private boolean initDataFlg;

    @Value("${init.data.path:.}")
    private String initDataPath;

    @Autowired
    private IDbStructureData dbStructureData;

    @Override
    public void afterSingletonsInstantiated() {
        //等redis实例化后再执行
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    File initFile = new File(".init");
                    if (initFile.exists()) {
                        return;
                    }

                    if (!initDataFlg) {
                        return;
                    }
                    initData("category", SpringUtils.getBean(ICategoryData.class), new TypeReference<List<Category>>() {
                    });
                    initData("deviceGroup", SpringUtils.getBean(IDeviceGroupData.class), new TypeReference<List<DeviceGroup>>() {
                    });
                    initData("deviceInfo", SpringUtils.getBean(IDeviceInfoData.class), new TypeReference<List<DeviceInfo>>() {
                    });
                    initData("home", SpringUtils.getBean(IHomeData.class), new TypeReference<List<Home>>() {
                    });
                    initData("oauthClient", SpringUtils.getBean(IOauthClientData.class), new TypeReference<List<OauthClient>>() {
                    });
                    initData("product", SpringUtils.getBean(IProductData.class), new TypeReference<List<Product>>() {
                    });
                    initData("productModel", SpringUtils.getBean(IProductModelData.class), new TypeReference<List<ProductModel>>() {
                    });
                    initData("ruleInfo", SpringUtils.getBean(IRuleInfoData.class), new TypeReference<List<RuleInfo>>() {
                    });
                    initData("space", SpringUtils.getBean(ISpaceData.class), new TypeReference<List<Space>>() {
                    });
                    initData("spaceDevice", SpringUtils.getBean(ISpaceDeviceData.class), new TypeReference<List<SpaceDevice>>() {
                    });
                    initData("taskInfo", SpringUtils.getBean(ITaskInfoData.class), new TypeReference<List<TaskInfo>>() {
                    });
                    List<ThingModel> thingModels = initData("thingModel", SpringUtils.getBean(IThingModelData.class), new TypeReference<>() {
                    });
                    //初始化物模型时序数据结构
                    for (ThingModel thingModel : thingModels) {
                        dbStructureData.defineThingModel(thingModel);
                    }

                    initData("userInfo", SpringUtils.getBean(IUserInfoData.class), new TypeReference<List<UserInfo>>() {
                    });
                    initData("virtualDevice", SpringUtils.getBean(IVirtualDeviceData.class), new TypeReference<List<VirtualDevice>>() {
                    });
                    initData("channel", SpringUtils.getBean(IChannelData.class), new TypeReference<List<Channel>>() {
                    });
                    initData("channelConfig", SpringUtils.getBean(IChannelConfigData.class), new TypeReference<List<ChannelConfig>>() {
                    });
                    initData("channelTemplate", SpringUtils.getBean(IChannelTemplateData.class), new TypeReference<List<ChannelTemplate>>() {
                    });
                    initData("notifyMessage", SpringUtils.getBean(INotifyMessageData.class), new TypeReference<List<NotifyMessage>>() {
                    });
                    initData("pluginInfo", SpringUtils.getBean(IPluginInfoData.class), new TypeReference<List<PluginInfo>>() {
                    });

                    initSysData();

                    log.info("init data finished.");

                    FileUtils.write(initFile, "", StandardCharsets.UTF_8);
                } catch (
                        Exception e) {
                    log.error("init error", e);
                }
            }
        }, 100);

    }

    private void initSysData() throws IOException {
        initData("sys_config", SpringUtils.getBean(ISysConfigData.class), new TypeReference<List<SysConfig>>() {
        });

        initData("sys_dept", SpringUtils.getBean(ISysDeptData.class), new TypeReference<List<SysDept>>() {
        });

        initData("sys_dict_data", SpringUtils.getBean(ISysDictData.class), new TypeReference<List<SysDictData>>() {
        });

        initData("sys_dict_type", SpringUtils.getBean(ISysDictTypeData.class), new TypeReference<List<SysDictType>>() {
        });

        initData("sys_logininfor", SpringUtils.getBean(ISysLogininforData.class), new TypeReference<List<SysLoginInfo>>() {
        });
        initData("sys_menu", SpringUtils.getBean(ISysMenuData.class), new TypeReference<List<SysMenu>>() {
        });

        initData("sys_notice", SpringUtils.getBean(ISysNoticeData.class), new TypeReference<List<SysNotice>>() {
        });

        initData("sys_oper_log", SpringUtils.getBean(ISysOperLogData.class), new TypeReference<List<SysOperLog>>() {
        });

        initData("sys_oss", SpringUtils.getBean(ISysOssData.class), new TypeReference<List<SysOss>>() {
        });

        initData("sys_oss_config", SpringUtils.getBean(ISysOssConfigData.class), new TypeReference<List<SysOssConfig>>() {
        });

        initData("sys_post", SpringUtils.getBean(ISysPostData.class), new TypeReference<List<SysPost>>() {
        });
        initData("sys_role", SpringUtils.getBean(ISysRoleData.class), new TypeReference<List<SysRole>>() {
        });

        initData("sys_role_dept", SpringUtils.getBean(ISysRoleDeptData.class), new TypeReference<List<SysRoleDept>>() {
        });

        initData("sys_role_menu", SpringUtils.getBean(ISysRoleMenuData.class), new TypeReference<List<SysRoleMenu>>() {
        });

        initData("sys_tenant", SpringUtils.getBean(ISysTenantData.class), new TypeReference<List<SysTenant>>() {
        });

        initData("sys_tenant_package", SpringUtils.getBean(ISysTenantPackageData.class), new TypeReference<List<SysTenantPackage>>() {
        });

        initData("sys_user", SpringUtils.getBean(ISysUserData.class), new TypeReference<List<SysUser>>() {
        });

        initData("sys_user_post", SpringUtils.getBean(ISysUserPostData.class), new TypeReference<List<SysUserPost>>() {
        });

        initData("sys_user_role", SpringUtils.getBean(ISysUserRoleData.class), new TypeReference<List<SysUserRole>>() {
        });

        initData("sys_app", SpringUtils.getBean(ISysAppData.class), new TypeReference<List<SysApp>>() {
        });
    }

    private <T> T initData(String name, ICommonData service, TypeReference<T> type) {
        try {
            log.info("init {} data...", name);
            if (service.count() > 0) {
                log.error("原数据库已存在" + name + "的旧数据，请清除后再重新初始化！系统正在退出。。。");
                System.exit(0);
            }
            String path = initDataPath;
            if (initDataPath.equals(".")) {
                path = "./data/init";
            }
            String json = FileUtils.readFileToString(Paths.get(path, name + ".json").toFile(), StandardCharsets.UTF_8);
            List list = (List) JsonUtils.parseObject(json, type);
            for (Object obj : list) {
                service.save((Id) obj);
            }
            return (T) list;
        } catch (Exception e) {
            log.error("initData error", e);
            return null;
        }
    }

}
