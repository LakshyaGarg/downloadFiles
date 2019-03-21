import com.project.test.commons.CommonUtils;
import com.project.test.protocols.FTPProtocol;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, URLConnection.class, CommonUtils.class})
public class FtpProtocolTest extends FTPProtocol {

    @InjectMocks
    private FtpProtocolTest protocol;
    private boolean isDirAvailable = false;

    @Before
    public void setUp() {
        try {
            Files.createDirectory(Paths.get("tempDir"));
            isDirAvailable = true;
        } catch ( IOException e) {
            isDirAvailable = false;
        }
    }


    @After
    public void cleanUp() {
        try {

            Files.walk(Paths.get("tempDir"))
                .map(Path::toFile)
                .forEach(File::delete);

            Files.deleteIfExists(Paths.get("tempDir"));
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void downloadFileTest() throws Exception {
        if(!isDirAvailable)
            return;

        //first download
        URL url = PowerMockito.mock(URL.class);
        String urlString = "http://test.com/123";
        PowerMockito.whenNew(URL.class).withParameterTypes(String.class).
                withArguments(Mockito.anyString()).thenReturn(url);
        URLConnection urlConnection = PowerMockito.mock(URLConnection.class);
        PowerMockito.mockStatic(CommonUtils.class);
        PowerMockito.when(CommonUtils.getUrlConnection(Mockito.any(URL.class), Mockito.anyInt(), Mockito.anyInt())).
                thenReturn(urlConnection);
        String data = "Hello World";
        PowerMockito.when(urlConnection.getContentLength()).thenReturn(data.length());
        PowerMockito.when(CommonUtils.readFile(urlConnection, "123")).thenReturn((long)data.length());
        PowerMockito.doNothing().when(CommonUtils.class, "handleDownload", Mockito.anyLong(), Mockito.anyLong(),
                Mockito.anyString(), Mockito.anyString());
        protocol.downloadFile(url, "123", "tempDir");
    }

    /*@Test
    public void downloadFileWithSameName() throws Exception {
        if(!isDirAvailable)
            return;
        // first download
        URL url = PowerMockito.mock(URL.class);
        String urlString = "ftp://test.com/123";
        PowerMockito.whenNew(URL.class).withParameterTypes(String.class).withArguments(Mockito.anyString()).thenReturn(url);
        URLConnection urlConnection = PowerMockito.mock(URLConnection.class);
        PowerMockito.when(url.openConnection()).thenReturn(urlConnection);
        PowerMockito.doNothing().when(urlConnection).setReadTimeout(Mockito.anyInt());
        PowerMockito.doNothing().when(urlConnection).setConnectTimeout(Mockito.anyInt());
        String data = "hello world";
        PowerMockito.when(urlConnection.getContentLength()).thenReturn(data.length());
        PowerMockito.when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(data.getBytes()));
        URL testUrl = new URL(urlString);
        protocol.downloadFile(testUrl, "123", "tempDir");

        //second download
        URL url1 = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withParameterTypes(String.class).withArguments(Mockito.anyString()).thenReturn(url1);
        URLConnection urlConnection1 = PowerMockito.mock(URLConnection.class);
        PowerMockito.when(url1.openConnection()).thenReturn(urlConnection1);
        PowerMockito.doNothing().when(urlConnection1).setReadTimeout(Mockito.anyInt());
        PowerMockito.doNothing().when(urlConnection1).setConnectTimeout(Mockito.anyInt());
        PowerMockito.when(urlConnection1.getContentLength()).thenReturn(data.length());
        PowerMockito.when(urlConnection1.getInputStream()).thenReturn(new ByteArrayInputStream(data.getBytes()));
        URL testUrl1 = new URL(urlString);
        protocol.downloadFile(testUrl1, "123", "tempDir");

        int count = 0;
        try (DirectoryStream<Path> stream  = Files.newDirectoryStream(Paths.get("tempDir"), "*123")){
            Iterator<Path> iterator = stream.iterator();
            while(iterator.hasNext()) {
                count++;
                Path path = iterator.next();
                Assert.assertEquals(data, new String(Files.readAllBytes(path)));
            }
        } catch (Exception e) {
        }
        Assert.assertEquals(2, count);
    }
    */
}
