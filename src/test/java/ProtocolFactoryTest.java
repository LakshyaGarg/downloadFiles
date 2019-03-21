import com.project.test.protocols.FTPProtocol;
import com.project.test.protocols.HttpProtocol;
import com.project.test.protocols.Protocol;
import com.project.test.protocolsFactory.ProtocolFactory;
import org.junit.Assert;
import org.junit.Test;

public class ProtocolFactoryTest {

    @Test
    public void testHttpProtocolInstance() {
        Protocol protocol = ProtocolFactory.createProtocol("http");
        Assert.assertEquals(true, protocol instanceof HttpProtocol);
    }

    @Test
    public void testFtpProtocolInstance() {
        Protocol protocol = ProtocolFactory.createProtocol("ftp");
        Assert.assertEquals(true, protocol instanceof FTPProtocol);
    }
}
