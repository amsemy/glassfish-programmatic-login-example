package my.test;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
public class FooBean implements FooBeanRemote {

    @Resource
    private SessionContext context;

    public String test() {
        return context.getCallerPrincipal().getName();
    }

}
