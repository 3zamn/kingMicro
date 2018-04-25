package tool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Service {
    private static final Map<String, Service> serviceMap = new ConcurrentHashMap<>();

    public Service getService(String serviceName) {
        Service service = serviceMap.get(serviceName);
        if (service == null) {
            synchronized (Service.class) {
                service = serviceMap.get(serviceName);
                if (service == null) {
                    service = new Service();
                    serviceMap.put(serviceName, service);
                }
            }
        }
        return service;
    }
 /*   public Service getService(String serviceName) {
        Service service = serviceMap.get(serviceName);
        if (service == null) {
            synchronized (Service.class) {
                service = serviceMap.computeIfAbsent(serviceName, k -> new Service());
            }
        }
        return service;
    }*/
}
