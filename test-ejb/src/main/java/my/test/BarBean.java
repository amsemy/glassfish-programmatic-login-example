package my.test;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

@Stateless
public class BarBean implements BarBeanRemote {

    @Resource
    private SessionContext context;

    @RolesAllowed("user_role")
    public String test() {
        return context.getCallerPrincipal().getName();
    }

}
