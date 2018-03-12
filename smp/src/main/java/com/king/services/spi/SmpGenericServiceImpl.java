package com.king.services.spi;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.service.GenericException;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.king.api.smp.SmpGenericService;
@Service("smpGenericService")
public class  SmpGenericServiceImpl  implements SmpGenericService,GenericService{

	public Object $invoke(String methodName, String[] parameterTypes, Object[] args) throws GenericException {
        if ("sayHello".equals(methodName)) {
            return "Welcome " + args[0];
        }
		return args;
    }
	
	

}
