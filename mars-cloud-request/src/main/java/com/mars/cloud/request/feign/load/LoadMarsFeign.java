package com.mars.cloud.request.feign.load;

import com.mars.cloud.annotation.MarsFeign;
import com.mars.cloud.request.feign.proxy.FeignProxy;
import com.mars.common.annotation.api.MarsApi;
import com.mars.common.annotation.api.MarsInterceptor;
import com.mars.common.annotation.bean.MarsBean;
import com.mars.common.annotation.bean.MarsOnLoad;
import com.mars.common.annotation.jdbc.MarsDao;
import com.mars.common.constant.MarsConstant;
import com.mars.common.constant.MarsSpace;
import com.mars.core.load.LoadHelper;
import com.mars.core.model.MarsBeanModel;

import java.util.Map;
import java.util.Set;

/**
 * 加载
 */
public class LoadMarsFeign {

    private static MarsSpace marsSpace = MarsSpace.getEasySpace();

    /**
     * 加载RPC对象
     * @throws Exception 异常
     */
    public static void LoadCloudFeign() throws Exception {
        Set<String> classList = LoadHelper.getSacnClassList();

        Map<String, MarsBeanModel> marsBeanObjects = LoadHelper.getBeanObjectMap();

        for (String str : classList) {
            Class<?> cls = Class.forName(str);
            MarsFeign marsFeign = cls.getAnnotation(MarsFeign.class);
            MarsApi marsApi = cls.getAnnotation(MarsApi.class);
            MarsBean marsBean = cls.getAnnotation(MarsBean.class);
            MarsInterceptor marsInterceptor = cls.getAnnotation(MarsInterceptor.class);
            MarsDao marsDao = cls.getAnnotation(MarsDao.class);
            MarsOnLoad marsOnLoad = cls.getAnnotation(MarsOnLoad.class);

            if ((marsApi != null || marsBean != null || marsInterceptor != null
                    || marsDao != null || marsOnLoad != null) && marsFeign != null) {
                throw new Exception("类:["+cls.getName()+"]上不允许有多个Mars注解");
            }
            if (marsFeign != null) {
                loadMarsFeign(cls,marsFeign,marsBeanObjects);
            }
        }
    }

    /**
     * 将feign类存下来
     * @param cls
     */
    private static void loadMarsFeign(Class cls, MarsFeign marsFeign, Map<String, MarsBeanModel> marsBeanObjects) throws Exception {
        String beanName = LoadHelper.getBeanName(marsFeign.beanName(), cls);

        if (marsBeanObjects.get(beanName) == null) {
            MarsBeanModel beanModel = new MarsBeanModel();
            beanModel.setName(beanName);
            beanModel.setCls(cls);
            beanModel.setObj(new FeignProxy().getProxy(cls));
            marsBeanObjects.put(beanName, beanModel);

            /* 保险起见，数据重插 */
            marsSpace.setAttr(MarsConstant.MARS_BEAN_OBJECTS, marsBeanObjects);
        } else {
            throw new Exception("已经存在name为[" + beanName + "]的bean了");
        }
    }
}
