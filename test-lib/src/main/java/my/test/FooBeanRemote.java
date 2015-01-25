package my.test;

import javax.ejb.Remote;

@Remote
public interface FooBeanRemote {

    String test();

}
