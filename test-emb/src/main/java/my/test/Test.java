package my.test;

import com.sun.appserv.security.ProgrammaticLogin;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class Test {

    public static void main(String[] args) throws Exception {
        GlassFishUtils.construct(Paths.get(args[0]));
        try {
            System.out.println();
            System.out.println("Debug 1: Class path");
            listClassPath();

            System.out.println();
            System.out.println("Debug 2: Context tree");
            listContext();

            System.out.println();
            System.out.println("Test 1: Call unsecured bean");
            runFoo();

            System.out.println();
            System.out.println("Test 2: Call secured bean");
            runBar();

            System.out.println();
        } finally {
            GlassFishUtils.destroy();
        }
    }

    private static Context createContext() throws NamingException {
        return new InitialContext();
    }

    private static void listClassPath() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url: urls){
            System.out.println(url.getFile());
        }
    }

    private static void listContext() throws NamingException {
        Context ctx = createContext();
        StringBuilder sb = new StringBuilder();
        listContext(ctx, sb, "    ");
        System.out.println(sb);
    }

    private static void listContext(Context ctx, StringBuilder sb,
            String indent) throws NamingException {
        NamingEnumeration<Binding> list = ctx.listBindings("");
        while (list.hasMore()) {
            Binding item = list.next();
            sb.append(indent)
                    .append(item.getName())
                    .append(System.lineSeparator());
            Object obj = item.getObject();
            if (obj instanceof javax.naming.Context) {
                listContext((Context) obj, sb, indent + "  ");
            }
        }
    }

    private static void runBar() throws Exception {
        ProgrammaticLogin pl = new ProgrammaticLogin();
        pl.login("user", "user123".toCharArray());
        InitialContext ic = new InitialContext();
        BarBeanRemote barBean = (BarBeanRemote) ic.lookup("my.test.BarBeanRemote");
        System.out.println("BAR BEAN: " + barBean.test());
        pl.logout();
    }

    private static void runFoo() throws NamingException {
        InitialContext ic = new InitialContext();
        FooBeanRemote fooBean = (FooBeanRemote) ic.lookup("my.test.FooBeanRemote");
        System.out.println("FOO BEAN: " + fooBean.test());
    }

}
