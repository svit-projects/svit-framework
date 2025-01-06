package svit.beans;

import df.common.container.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

public class MapObjectBeanInfo extends AbstractBeanInfo {

    public MapObjectBeanInfo(Map<String, Object> map) {
        super(map);
        ReflectionUtils.readPropertyDescriptors(this.classType, this.descriptors);
        this.fields = createFields();
    }

    private Map<String, BeanField> createFields() {
        Map<String, BeanField> fields = new HashMap<>();

        for (String key : this.<Map<String, ?>>getObject().keySet()) {
            BeanField field = new MapBeanField(key, getObject());
            fields.put(field.getName(), field);
        }

        return fields;
    }

}

