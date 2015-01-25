package my.test;

import javax.ejb.Remote;

@Remote
public interface BarBeanRemote {

    String test();

}
